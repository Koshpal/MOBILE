package com.app.koshpal.app.viewmodels.budgetviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.koshpal.app.domain.model.*
import com.app.koshpal.app.fluxdeck.BudgetFluxDeck
import com.app.koshpal.app.domain.coordinator.BudgetCoordinator
import com.app.koshpal.core.data.entities.enums.BudgetPeriod
import kotlinx.coroutines.flow.*

class BudgetSettingsViewModel(
    private val coordinator: BudgetCoordinator,
    private val fluxDeck: BudgetFluxDeck
) : ViewModel() {

    private val manuallyModifiedCategoryIds = mutableSetOf<String>()

    private val _currentBudget = MutableStateFlow<Budget?>(null)

    val events = coordinator.events

    val title = fluxDeck.title
    val overallAmountString = fluxDeck.overallAmountString
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    val period = fluxDeck.period
    val startDate = fluxDeck.startDate
    val endDate = fluxDeck.endDate
    val budgetType = fluxDeck.budgetType
    val editingCategory = fluxDeck.editingCategory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val allocations = fluxDeck.allocations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val totalCategorySum = fluxDeck.totalCategorySum
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    val overAllocatedAmount = fluxDeck.overAllocatedAmount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val categoryName = fluxDeck.categoryName
    val categoryIcon = fluxDeck.categoryIcon
    val categoryColor = fluxDeck.categoryColor

    val subCategoryDrafts = fluxDeck.subCategoryDrafts
    val subCategoryName = fluxDeck.subCategoryName
    val subCategoryIcon = fluxDeck.subCategoryIcon
    val subCategoryColor = fluxDeck.subCategoryColor

    init {
        fluxDeck.allBudgets.combine(fluxDeck.clickedBudgetId) { budgets, currentId ->
            val b = budgets.find { it.id == currentId }
            if (b != null) {
                fluxDeck.prepareEdit(b)
            }
            b
        }.onEach { _currentBudget.value = it }.launchIn(viewModelScope)
    }

    private var isOverallManuallyModified = false

    fun updateTitle(value: String) = fluxDeck.updateTitle(value)

    private val _showZeroAmountAlert = MutableStateFlow(false)
    val showZeroAmountAlert: StateFlow<Boolean> = _showZeroAmountAlert.asStateFlow()

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

    fun startEditingCategory(uiState: CategoryAllocationUiState) {
        fluxDeck.updateEditingCategoryId(uiState.category.id)
    }

    fun stopEditingCategory() {
        fluxDeck.updateEditingCategoryId(null)
    }

    fun updateCategoryAmount(categoryId: String, newAmountStr: String, isManual: Boolean = true) {
        if (isManual) manuallyModifiedCategoryIds.add(categoryId)
        fluxDeck.updateCategoryAmount(categoryId, newAmountStr)
    }

    fun addCategory(newCategory: Category) = fluxDeck.addCategory(newCategory)

    fun removeCategory(category: Category) {
        fluxDeck.removeCategory(category)
        manuallyModifiedCategoryIds.remove(category.id)
    }

    fun saveBudgetChanges() {
        fluxDeck.saveBudgetChanges()
    }

    fun deleteBudget(onFinish: () -> Unit = {}) {
        val current = _currentBudget.value ?: return
        coordinator.deleteBudget(current)
        onFinish()
    }

    fun updateCategoryName(value: String) = fluxDeck.updateCategoryName(value)
    fun updateCategoryIcon(value: String) = fluxDeck.updateCategoryIcon(value)
    fun updateCategoryColor(value: String) = fluxDeck.updateCategoryColor(value)
    fun clearCategoryDraft() = fluxDeck.clearCategoryDraft()

    fun updateSubCategoryName(value: String) = fluxDeck.updateSubCategoryName(value)
    fun updateSubCategoryIcon(value: String) = fluxDeck.updateSubCategoryIcon(value)
    fun prepareSubCategoryFor(parentId: String, inheritedColor: String) = fluxDeck.prepareSubCategoryFor(parentId, inheritedColor)



    fun createCategoryDraft(onFinish: () -> Unit = {}) {
        fluxDeck.createCategory()
        onFinish()
    }

    fun createSubCategoryDraft(presetCategory: Category? = null, onFinish: () -> Unit = {}) {
        fluxDeck.createSubCategory(presetCategory)
        onFinish()
    }

    fun addSubCategoryToExisting(subCat: Category) {
        val parent = editingCategory.value?.category ?: return
        fluxDeck.addSubCategoryToExisting(subCat, parent)
    }


    val subCategoryDraft: StateFlow<Category> = fluxDeck.buildSubCategoryDraft
        .stateIn(viewModelScope, SharingStarted.Eagerly, Category(
            title = "",
            iconResId = "category",
            colorHex = "#000000",
            parentCategoryId = null
        ))


    fun buildSubCategoryDraft(): Category {
        return subCategoryDraft.value
    }


    fun removeSubCategoryFromExisting(subCatId: String) {
        fluxDeck.removeSubCategoryFromExisting(subCatId)
    }

    fun removeSubCategoryDraft(subCatId: String) {
        fluxDeck.updateSubCategoryDrafts(subCategoryDrafts.value.filterNot { it.category.id == subCatId })
    }

}
