package com.app.koshpal.app.domain.coordinator

import com.app.koshpal.app.Events
import com.app.koshpal.app.StateReflector
import com.app.koshpal.app.data.UserPreferences
import com.app.koshpal.app.domain.model.Budget
import com.app.koshpal.app.domain.model.Category
import com.app.koshpal.app.domain.model.CategoryAllocationUi
import com.app.koshpal.app.domain.model.Notification
import com.app.koshpal.app.domain.usecase.authusecase.AuthUseCases
import com.app.koshpal.app.domain.usecase.budgetusecase.BudgetUseCases
import com.app.koshpal.app.domain.usecase.categoriesusecase.CategoryUseCases
import com.app.koshpal.app.domain.usecase.notificationusecase.NotificationUseCases
import com.app.koshpal.app.domain.usecase.transactionsusecase.TransactionQueries
import com.app.koshpal.app.fluxdeck.BudgetFluxDeck
import com.app.koshpal.app.handleResult
import com.app.koshpal.core.data.entities.enums.BudgetPeriod
import com.app.koshpal.core.data.entities.enums.BudgetType
import com.app.koshpal.core.data.entities.enums.NotificationType
import com.app.koshpal.core.data.entities.enums.toReadableString
import com.app.koshpal.core.domain.util.DatabaseCallError
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.notification.NotificationHelper
import com.app.koshpal.core.presentation.util.parseIsoToLocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class BudgetCoordinator(
    private val budgetUseCases: BudgetUseCases,
    private val transactionQueries: TransactionQueries,
    private val categoryUseCases: CategoryUseCases,
    private val notificationUseCases: NotificationUseCases,
    private val notificationHelper: NotificationHelper,
    private val userPreferences: UserPreferences,
    private val fluxDeck: BudgetFluxDeck,
    private val authUseCases: AuthUseCases,
    private val scope: CoroutineScope
) {
    val reflector = StateReflector<Events>(scope)
    val events = reflector.events

    init {
        sync()
        scope.launch {
            fluxDeck.createBudgetIntent.collect { createBudget() }
        }
        scope.launch {
            fluxDeck.saveCategoryIntent.collect { createCategory() }
        }
        scope.launch {
            fluxDeck.createSubCategoryIntent.collect { preset -> createSubCategory(preset)  }
        }
        scope.launch {
            fluxDeck.saveBudgetChangesIntent.collect { updateBudget() }
        }
    }

    private suspend fun processRenewals() {
        val allBudgets = budgetUseCases.getAllBudgetsWithDetails().first()
        val todayMillis = Clock.System.now().toEpochMilliseconds()
        
        allBudgets.forEach { budget ->
            val range = budget.getDateRange() ?: return@forEach
            val endMillis = range.second
            
            if (todayMillis > endMillis) {
                budgetUseCases.archiveBudget(budget.id)
                
                if (budget.budgetType == BudgetType.RECURRING && budget.isRepeating) {
                    val originalStart = budget.startDate.parseIsoToLocalDate() ?: return@forEach
                    var currentStart = originalStart
                    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                    
                    while (true) {
                        val nextStart = when (budget.period) {
                            BudgetPeriod.Weekly -> currentStart.plus(7, DateTimeUnit.DAY)
                            BudgetPeriod.Monthly -> currentStart.plus(1, DateTimeUnit.MONTH)
                            BudgetPeriod.Yearly -> currentStart.plus(1, DateTimeUnit.YEAR)
                            else -> currentStart.plus(1, DateTimeUnit.MONTH)
                        }
                        if (nextStart > today) {
                            break
                        }
                        currentStart = nextStart
                    }
                    
                    val newAllocations = budget.allocations.map {
                        it.copy(id = Uuid.random().toString())
                    }
                    val newBudget = budget.copy(
                        id = Uuid.random().toString(),
                        startDate = "${currentStart}T00:00:00Z",
                        allocations = newAllocations,
                        isSynced = false
                    )
                    budgetUseCases.createBudget(newBudget)
                }
            }
        }
    }

    fun syncRemoteBudgets() {
        scope.launch {
            if (userPreferences.isGuestUser.first()) return@launch
            val token = userPreferences.accessToken.first() ?: return@launch
            val result = budgetUseCases.getRemoteBudgetsUseCase(token)
            reflector.handleResult(
                result,
                onError = { error ->
                    if (error == NetworkError.INVALID_USER) {
                        onRefreshToken {
                            syncRemoteBudgets()
                        }
                    }
                }
            ) { remoteBudgets ->
                remoteBudgets.forEach { budget ->
                    budgetUseCases.createBudget(budget)
                }
            }
        }
    }

    fun onRefreshToken(onSuccess: () -> Unit = {}) {
        scope.launch {
            if (userPreferences.isGuestUser.first()) return@launch
            val currentRefreshToken = userPreferences.refreshToken.first() ?: return@launch
            val currentRefreshTokenId = userPreferences.refreshTokenId.first() ?: ""
            val result = authUseCases.onRefreshTokenUseCase(currentRefreshToken, currentRefreshTokenId)
            reflector.handleResult(result) { user ->
                userPreferences.saveAccessToken(user.accessToken)
                userPreferences.saveRefreshToken(user.refreshToken)
                if (!user.refreshTokenId.isNullOrBlank()) {
                    userPreferences.saveRefreshTokenId(user.refreshTokenId)
                }
                onSuccess()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun sync() {
        scope.launch {
            processRenewals()
            syncRemoteBudgets()
        }
        scope.launch {
            categoryUseCases.deleteOrphanedCategories()
        }
        scope.launch {
            budgetUseCases.getAllBudgetsWithDetails()
                .onEach { fluxDeck.updateAllBudgets(it) }
                .flatMapLatest { list ->
                    if (list.isEmpty()) flowOf(emptyMap())
                    else combine(list.map { b ->
                        transactionQueries.getSpentForBudget(b.id).map { spent -> b.id to (spent ?: 0.0) }
                    }) { it.toMap() }
                }
                .collectLatest { fluxDeck.updateSpentMap(it) }
        }

        scope.launch {
            fluxDeck.showHistory.flatMapLatest { show ->
                if (show) budgetUseCases.getArchivedBudgetsUseCase() else flowOf(emptyList())
            }.collectLatest { fluxDeck.updateArchivedBudgets(it) }
        }

        scope.launch {
            categoryUseCases.getMainCategories().collectLatest { fluxDeck.updateMainCategories(it) }
        }

        scope.launch {
            fluxDeck.allBudgets.flatMapLatest { list ->
                val allCats = list.flatMap { it.allocations }.mapNotNull { it.category }
                if (allCats.isEmpty()) flowOf(emptyMap())
                else {
                    val rawSpentFlows = allCats.map { c ->
                        val budgetId = list.find { b -> b.allocations.any { it.categoryId == c.id } }?.id ?: ""
                        transactionQueries.getCategorySpentById(c.id, budgetId).map { c.id to (it ?: 0.0) }
                    }
                    combine(rawSpentFlows) { rawItems ->
                        val rawMap = rawItems.toMap()
                        allCats.associate { c ->
                            val childIds = allCats.filter { it.parentCategoryId == c.id }.map { it.id }
                            val totalSpend = (rawMap[c.id] ?: 0.0) + childIds.sumOf { rawMap[it] ?: 0.0 }
                            c.id to totalSpend
                        }
                    }
                }
            }.collectLatest { fluxDeck.updateCategorySpentMap(it) }
        }

        scope.launch {
            fluxDeck.budgetsWithSpent.collectLatest { list ->
                list.forEach { (budget, spent) ->
                    val percent = if (budget.amount > 0) (spent / budget.amount) * 100 else 0.0
                    when {
                        percent >= 100 -> triggerBudgetNotification(budget, "100%")
                        percent >= 80 -> triggerBudgetNotification(budget, "80%")
                    }
                }
            }
        }
    }

    private suspend fun triggerBudgetNotification(budget: Budget, threshold: String) {
        val existing = notificationUseCases.getAllNotifications().first()
        val alreadyNotified = existing.any {
            it.type == NotificationType.BUDGET_WATCH &&
                    it.featureId == budget.id &&
                    it.message.contains(threshold)
        }

        if (!alreadyNotified) {
            val title = "Budget Watch: ${budget.title}"
            val message = "You have reached $threshold of your budget."

            notificationUseCases.insertNotification(
                Notification(
                    id = Uuid.random().toString(),
                    title = title,
                    message = message,
                    type = NotificationType.BUDGET_WATCH,
                    timestamp = Clock.System.now().toEpochMilliseconds(),
                    featureId = budget.id
                )
            )

            notificationHelper.showBudgetNotification(
                id = budget.id,
                title = title,
                message = message
            )
        }
    }


    fun deleteBudget(budget: Budget) {
        scope.launch {
            val result = budgetUseCases.deleteBudget(budget)
            reflector.handleResult(result) {
                categoryUseCases.deleteOrphanedCategories()
            }
            if (!userPreferences.isGuestUser.first()) {
                val token = userPreferences.accessToken.first()
                if (token != null) {
                    val remoteResult = budgetUseCases.deleteRemoteBudgetUseCase(token, budget.id)
                    reflector.handleResult(
                        remoteResult,
                        onError = { error ->
                            if (error == NetworkError.INVALID_USER) {
                                onRefreshToken {
                                    deleteBudget(budget)
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    fun toggleIndividualFlaggedState(id: String, allBudgets: List<Budget>) {
        scope.launch {
            val flagged = userPreferences.flaggedBudgetIds.first()
            if (flagged.contains(id)) {
                userPreferences.updateFlaggedBudgets(flagged - id)
                reflector.emitEvent(Events.Success("Flag removed"))
            } else {
                val budget = allBudgets.find { it.id == id } ?: return@launch
                val currentFlaggedOfThisType = (allBudgets.filter { flagged.contains(it.id) && it.budgetType == budget.budgetType }).size

                if (currentFlaggedOfThisType >= 3) {
                    reflector.emitEvent(Events.Error(
                        DatabaseCallError.UNKNOWN,
                        "Limit of 3 flagged ${budget.budgetType.toReadableString().lowercase()} budgets reached."
                    ))
                    return@launch
                }
                userPreferences.updateFlaggedBudgets(flagged + id)
                reflector.emitEvent(Events.Success("Budget flagged"))
            }
            fluxDeck.updateEditingCategoryId(null)
        }
    }

    fun toggleIndividualHiddenState(id: String) {
        scope.launch {
            val hidden = userPreferences.hiddenBudgetIds.first()
            userPreferences.updateHiddenBudgets(if (hidden.contains(id)) hidden - id else hidden + id)
            fluxDeck.updateEditingCategoryId(null)
            reflector.emitEvent(Events.Success("Visibility updated"))
        }
    }

    fun toggleSelectionHiddenState(isItemClicked: Boolean, selectedCategories: List<String>, selectedItems: List<String>) {
        scope.launch {
            if (isItemClicked) {
                val hidden = userPreferences.hiddenCategoryIds.first()
                userPreferences.updateHiddenCategories(if (selectedCategories.any { hidden.contains(it) }) hidden - selectedCategories.toSet() else hidden + selectedCategories)
            } else {
                val hidden = userPreferences.hiddenBudgetIds.first()
                userPreferences.updateHiddenBudgets(if (selectedItems.any { hidden.contains(it) }) hidden - selectedItems.toSet() else hidden + selectedItems)
            }
            reflector.emitEvent(Events.Success("Visibility updated"))
        }
    }

    fun toggleFlaggedState(selectedItems: List<String>, allBudgets: List<Budget>) {
        scope.launch {
            if (selectedItems.isEmpty()) return@launch

            val flagged = userPreferences.flaggedBudgetIds.first()
            val anySelectedFlagged = selectedItems.any { flagged.contains(it) }

            if (anySelectedFlagged) {
                userPreferences.updateFlaggedBudgets(flagged - selectedItems.toSet())
                reflector.emitEvent(Events.Success("Items unflagged"))
                return@launch
            }

            val toFlagBudgets = allBudgets.filter { selectedItems.contains(it.id) }
            val typesToEnforce = toFlagBudgets.map { it.budgetType }.distinct()

            for (type in typesToEnforce) {
                val currentlyFlaggedOfThisType = allBudgets.count { flagged.contains(it.id) && it.budgetType == type }
                val newToFlagOfThisType = toFlagBudgets.count { it.budgetType == type }

                if (currentlyFlaggedOfThisType + newToFlagOfThisType > 3) {
                    reflector.emitEvent(Events.Error(
                        DatabaseCallError.UNKNOWN,
                        "Limit of 3 flagged ${type.toReadableString().lowercase()} budgets reached."
                    ))
                    return@launch
                }
            }

            userPreferences.updateFlaggedBudgets(flagged + selectedItems)
            reflector.emitEvent(Events.Success("Items flagged"))
        }
    }

    fun removeFlaggedBudget(id: String) {
        scope.launch {
            val flagged = userPreferences.flaggedBudgetIds.first()
            userPreferences.updateFlaggedBudgets(flagged - id)
            reflector.emitEvent(Events.Success("Flag removed"))
        }
    }

    fun excludeSelection(isItemClicked: Boolean, selectedCategories: List<String>, selectedItems: List<String>) {
        scope.launch {
            if (isItemClicked) fluxDeck.excludeCategorySelection(selectedCategories)
            else fluxDeck.excludeBudgetSelection(selectedItems)
            reflector.emitEvent(Events.Success("Selection excluded"))
        }
    }

    fun excludeIndividualCategory(id: String) {
        scope.launch {
            fluxDeck.excludeCategorySelection(listOf(id))
            reflector.emitEvent(Events.Success("Category excluded"))
        }
    }

    fun createBudget() {
        scope.launch {
            val draft = fluxDeck.buildBudgetDraft.first()
            val totalCatSum = fluxDeck.totalCategorySum.first()
            val finalAmount = if (draft.amount > 0) draft.amount else totalCatSum

            if (finalAmount <= 0) {
                fluxDeck.showZeroAmountAlert.value = true
                return@launch
            }

            val budget = draft.copy(amount = finalAmount)
            val result = budgetUseCases.createBudget(budget)

            reflector.handleResult(result) {
                fluxDeck.updateLastCreatedBudgetId(budget.id)
                fluxDeck.clearWorkflow()
                reflector.emitEvent(Events.Success("Budget created"))
            }
        }
    }

    fun addCategory(category: Category) {
        scope.launch {
            fluxDeck.addCategory(category)
            reflector.emitEvent(Events.Success("${category.title} Category added"))
        }
    }

    fun updateBudget() {
        scope.launch {
            val budgetId = fluxDeck.clickedBudgetId.value
            val draft = fluxDeck.buildBudgetDraft.first()
            val totalCatSum = fluxDeck.totalCategorySum.first()
            val finalAmount = if (draft.amount > 0) draft.amount else totalCatSum

            if (finalAmount <= 0) {
                fluxDeck.showZeroAmountAlert.value = true
                return@launch
            }

            val updatedBudget = draft.copy(id = budgetId, amount = finalAmount)
            val result = budgetUseCases.updateBudget(updatedBudget)

            reflector.handleResult(result) {
                reflector.emitEvent(Events.Success("Budget updated"))
            }
        }
    }

    suspend fun createCategory() {
        val mainCategory = fluxDeck.buildCategoryDraft.first()
        val drafts = fluxDeck.subCategoryDrafts.value

        val result = categoryUseCases.createCategory(mainCategory)
        reflector.handleResult(result) {
            fluxDeck.addCategory(mainCategory)

            drafts.forEach { draft ->
                val linkedSub = draft.category.copy(id = Uuid.random().toString(), parentCategoryId = mainCategory.id)
                categoryUseCases.createCategory(linkedSub)
                fluxDeck.addCategoryWithAmount(linkedSub, draft.amountString)
            }
            fluxDeck.clearCategoryDraft()
            reflector.emitEvent(Events.Success("${mainCategory.title} Category added"))
        }
    }

    private suspend fun createSubCategory(preset: Category?) {
        val sub = preset ?: fluxDeck.buildSubCategoryDraft.first()

        if (fluxDeck.isCreatingCategoryInSheet.value) {
            val current = fluxDeck.subCategoryDrafts.value
            fluxDeck.updateSubCategoryDrafts(current + CategoryAllocationUi(category = sub, amountString = ""))
            reflector.emitEvent(Events.Success("Sub-category added"))
        } else {
            val parentId = fluxDeck.parentCategoryId.value

            if (parentId != null) {
                val linkedSub = sub.copy(id = Uuid.random().toString(), parentCategoryId = parentId)
                val result = categoryUseCases.createCategory(linkedSub)
                reflector.handleResult(result) {
                    fluxDeck.addCategory(linkedSub)
                    reflector.emitEvent(Events.Success("${linkedSub.title} added"))
                }
            }
        }
    }
}
