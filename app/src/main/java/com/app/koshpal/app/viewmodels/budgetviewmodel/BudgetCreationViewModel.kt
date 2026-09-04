package com.app.koshpal.app.viewmodels.budgetviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.koshpal.app.domain.model.*
import com.app.koshpal.app.fluxdeck.BudgetFluxDeck
import com.app.koshpal.app.domain.coordinator.BudgetCoordinator
import com.app.koshpal.core.data.entities.enums.BudgetPeriod
import com.app.koshpal.core.data.entities.enums.BudgetType
import kotlinx.coroutines.flow.*

class BudgetCreationViewModel(
    private val coordinator: BudgetCoordinator,
    private val fluxDeck: BudgetFluxDeck
) : ViewModel() {

    private val manuallyModifiedCategoryIds = mutableSetOf<String>()

    private val _isEditingWorkflow = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditingWorkflow.asStateFlow()

    private val _isSubCategoryEditing = MutableStateFlow(false)
    val isSubCategoryEditing: StateFlow<Boolean> = _isSubCategoryEditing.asStateFlow()

    val title = fluxDeck.title
    val overallAmountString: StateFlow<String> = fluxDeck.overallAmountString
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val period = fluxDeck.period
    val startDate = fluxDeck.startDate
    val endDate = fluxDeck.endDate
    val budgetType = fluxDeck.budgetType
    val allocations: StateFlow<List<CategoryAllocationUiState>> = fluxDeck.allocations
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val categoryName = fluxDeck.categoryName
    val categoryIcon = fluxDeck.categoryIcon
    val categoryColor = fluxDeck.categoryColor
    val subCategoryDrafts = fluxDeck.subCategoryDrafts
    val subCategoryName = fluxDeck.subCategoryName
    val subCategoryIcon = fluxDeck.subCategoryIcon
    val subCategoryColor = fluxDeck.subCategoryColor
    val parentCategoryId = fluxDeck.parentCategoryId
    val editingCategory: StateFlow<CategoryAllocationUiState?> = fluxDeck.editingCategory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val creationStep = fluxDeck.creationStep
    val isCreatingCategoryInSheet = fluxDeck.isCreatingCategoryInSheet
    val lastCreatedBudgetId = fluxDeck.lastCreatedBudgetId
    val isLoading = fluxDeck.isLoading

    val events = coordinator.events

    val titleSuggestions: StateFlow<List<String>> = fluxDeck.titleSuggestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var isOverallManuallyModified = false

    private val _showZeroAmountAlert = MutableStateFlow(false)
    val showZeroAmountAlert: StateFlow<Boolean> = _showZeroAmountAlert.asStateFlow()

    fun updateTitle(value: String) = fluxDeck.updateTitle(value)

    fun updateOverallAmount(value: String) {
        isOverallManuallyModified = true
        fluxDeck.updateOverallAmount(value)
        if (value.isBlank()) {
            manuallyModifiedCategoryIds.clear()
        }
    }

    fun updatePeriod(value: BudgetPeriod) = fluxDeck.updatePeriod(value)
    fun updateStartDate(value: String) = fluxDeck.updateStartDate(value)
    fun updateEndDate(value: String) = fluxDeck.updateEndDate(value)
    fun updateBudgetType(value: BudgetType) = fluxDeck.updateBudgetType(value)
    fun updateIsEditing(value: Boolean) { _isEditingWorkflow.value = value }
    fun updateIsSubCategoryEditing(value: Boolean) { _isSubCategoryEditing.value = value }
    fun updateCreationStep(step: Int) = fluxDeck.updateCreationStep(step)
    fun updateIsCreatingCategoryInSheet(value: Boolean) = fluxDeck.updateIsCreatingCategoryInSheet(value)
    fun updateCategoryName(value: String) = fluxDeck.updateCategoryName(value)
    fun updateCategoryIcon(value: String) = fluxDeck.updateCategoryIcon(value)
    fun updateCategoryColor(value: String) = fluxDeck.updateCategoryColor(value)
    fun updateSubCategoryName(value: String) = fluxDeck.updateSubCategoryName(value)
    fun updateSubCategoryIcon(value: String) = fluxDeck.updateSubCategoryIcon(value)

    val overAllocatedAmount: StateFlow<Double> = fluxDeck.overAllocatedAmount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalCategorySum: StateFlow<Double> = fluxDeck.totalCategorySum
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    fun startEditingCategory(uiState: CategoryAllocationUiState) {
        fluxDeck.updateEditingCategoryId(uiState.category.id)
        updateIsEditing(true)
    }

    fun stopEditingCategory() {
        fluxDeck.updateEditingCategoryId(null)
        fluxDeck.updateIsCreatingCategoryInSheet(false)
        updateIsEditing(false)
    }

    fun addSubCategoryToExisting(subCat: Category) {
        val parent = editingCategory.value?.category ?: return
        fluxDeck.addSubCategoryToExisting(subCat, parent)
    }

    fun removeSubCategoryFromExisting(subCatId: String) {
        fluxDeck.removeSubCategoryFromExisting(subCatId)
    }

    fun updateCategoryAmount(categoryId: String, newAmountStr: String, isManual: Boolean = true) {
        if (isManual) {
            manuallyModifiedCategoryIds.add(categoryId)
        }
        fluxDeck.updateCategoryAmount(categoryId, newAmountStr)
    }

    fun addCategory(newCategory: Category) {
        coordinator.addCategory(newCategory)
    }

    fun removeCategory(category: Category) {
        fluxDeck.removeCategory(category)
        manuallyModifiedCategoryIds.remove(category.id)
    }

    fun clearBudgetDraft() = fluxDeck.clearWorkflow()


    fun createBudget() {
        fluxDeck.createBudget()
    }

    fun clearCategoryDraft() {
        fluxDeck.updateIsCreatingCategoryInSheet(false)
        fluxDeck.clearCategoryDraft()
    }


    fun prepareSubCategoryFor(parentId: String, inheritedColor: String) = fluxDeck.prepareSubCategoryFor(parentId, inheritedColor)

    fun createCategoryDraft() {
        fluxDeck.createCategory()
    }

    fun createSubCategoryDraft(presetCategory: Category? = null) {
        fluxDeck.createSubCategory(presetCategory)
    }

    val budgetDraft: StateFlow<Budget> = fluxDeck.buildBudgetDraft
        .stateIn(viewModelScope, SharingStarted.Eagerly, Budget(
            title = "",
            amount = 0.0,
            period = BudgetPeriod.Monthly,
            startDate = "",
            endDate = null,
            budgetType = BudgetType.RECURRING,
            allocations = emptyList()
        ))


    val categoryDraft: StateFlow<Category> = fluxDeck.buildCategoryDraft.stateIn(viewModelScope, SharingStarted.Eagerly, Category(
        title = "",
        iconResId = "category",
        colorHex = "#000000"
    ))

    val subCategoryDraft: StateFlow<Category> = fluxDeck.buildSubCategoryDraft
        .stateIn(viewModelScope, SharingStarted.Eagerly, Category(
            title = "",
            iconResId = "category",
            colorHex = "#000000",
            parentCategoryId = null
        ))

    fun buildBudgetDraft(): Budget {
        return budgetDraft.value
    }
    fun buildCategoryDraft(): Category {
        return categoryDraft.value
    }
    fun buildSubCategoryDraft(): Category{
        return subCategoryDraft.value
    }


    fun removeSubCategoryDraft(subCatId: String) {
        fluxDeck.updateSubCategoryDrafts(subCategoryDrafts.value.filterNot { it.category.id == subCatId })
    }
}
