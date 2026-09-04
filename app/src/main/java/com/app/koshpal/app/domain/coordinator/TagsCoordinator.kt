package com.app.koshpal.app.domain.coordinator

import com.app.koshpal.app.Events
import com.app.koshpal.app.StateReflector
import com.app.koshpal.app.data.UserPreferences
import com.app.koshpal.app.domain.model.Category
import com.app.koshpal.app.domain.model.Goal
import com.app.koshpal.app.domain.model.Tag
import com.app.koshpal.app.domain.usecase.budgetusecase.BudgetUseCases
import com.app.koshpal.app.domain.usecase.categoriesusecase.CategoryUseCases
import com.app.koshpal.app.domain.usecase.goalusecase.GoalUseCases
import com.app.koshpal.app.domain.usecase.tagusecase.TagUseCases
import com.app.koshpal.app.domain.usecase.transactionsusecase.TransactionUseCases
import com.app.koshpal.app.fluxdeck.GoalFluxDeck
import com.app.koshpal.app.fluxdeck.TagsFluxDeck
import com.app.koshpal.app.handleResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

class TagsCoordinator(
    private val tagUseCases: TagUseCases,
    private val transactionUseCases: TransactionUseCases,
    private val budgetUseCases: BudgetUseCases,
    private val categoryUseCases: CategoryUseCases,
    private val goalUseCases: GoalUseCases,
    private val goalFluxDeck: GoalFluxDeck,
    private val userPreferences: UserPreferences,
    private val fluxDeck: TagsFluxDeck,
    private val scope: CoroutineScope
) {
    val reflector = StateReflector<Events>(scope)
    val events = reflector.events

    init {
        sync()
        scope.launch {
            fluxDeck.saveTagIntent.collect {
                saveTag()
            }
        }
    }

    private fun sync() {
        scope.launch {
            tagUseCases.getAllTags().collectLatest { fluxDeck.updateAllTags(it) }
        }
        scope.launch {
            transactionUseCases.getAllTransactionsInRange(0, Long.MAX_VALUE).collectLatest { fluxDeck.updateAllTransactions(it) }
        }
        scope.launch {
            budgetUseCases.getAllBudgetsWithDetails().collectLatest { fluxDeck.updateAllBudgets(it) }
        }
        scope.launch {
            categoryUseCases.getAllCategories().collectLatest { list ->
                fluxDeck.updateAllCategories(list)
            }
        }
        scope.launch {
            goalUseCases.getAllGoals().collectLatest { fluxDeck.updateAllGoals(it) }
        }
    }

    fun deleteTag(tag: Tag) {
        scope.launch {
            val result = tagUseCases.deleteTag(tag)
            reflector.handleResult(result)
        }
    }


    fun toggleSelectionHiddenState(selectedIds: List<String>) {
        scope.launch {
            val hidden = userPreferences.hiddenTagIds.first()
            userPreferences.updateHiddenTags(if (selectedIds.any { hidden.contains(it) }) hidden - selectedIds.toSet() else hidden + selectedIds)
            reflector.emitEvent(Events.Success("Visibility updated"))
        }
    }

    fun excludeSelection(selectedIds: List<String>) {
        scope.launch {
            val result = tagUseCases.deleteTagsByIds(selectedIds)
            reflector.handleResult(result) {
                reflector.emitEvent(Events.Success("Selection deleted"))
            }
        }
    }

    suspend fun saveTag() {
        val tag = Tag(
            id = UUID.randomUUID().toString(),
            name = fluxDeck.tagName.value,
            budgetGoal = fluxDeck.tagBudgetGoal.value.toDoubleOrNull() ?: 0.0,
            colorHex = fluxDeck.tagColor.value
        )
        fluxDeck.updateLoading(true)
        val result = tagUseCases.createTag(tag)
        fluxDeck.updateLoading(false)
        reflector.handleResult(result) {
            fluxDeck.updateLastCreatedTagId(tag.id)
            fluxDeck.clearForm()
            reflector.emitEvent(Events.Success("Tag created"))
        }
    }

    fun getSubCategoriesForCategory(categoryId: String): Flow<List<Category>> {
        return categoryUseCases.getSubCategoriesForParent(categoryId)
    }

    fun updateClickedGoalId(id: String) {
        goalFluxDeck.updateClickedGoalId(id)
    }

    fun deleteGoal(goal: Goal) {
        scope.launch {
            val result = goalUseCases.deleteGoal(goal)
            reflector.handleResult(result)
        }
    }
}
