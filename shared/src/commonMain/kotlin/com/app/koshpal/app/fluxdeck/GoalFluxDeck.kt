package com.app.koshpal.app.fluxdeck

import com.app.koshpal.app.domain.model.Goal
import com.app.koshpal.app.domain.model.GoalSavingsSummary
import com.app.koshpal.app.domain.model.Tag
import com.app.koshpal.core.data.entities.enums.BudgetPeriod
import com.app.koshpal.core.presentation.util.parseIsoToLocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class GoalFluxDeck {
    private val _saveGoalIntent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val saveGoalIntent = _saveGoalIntent.asSharedFlow()

    private val _allGoals = MutableStateFlow<List<Goal>>(emptyList())
    val allGoals = _allGoals.asStateFlow()

    private val _hiddenGoalIds = MutableStateFlow<Set<String>>(emptySet())
    val hiddenGoalIds = _hiddenGoalIds.asStateFlow()

    private val _flaggedGoalIds = MutableStateFlow<Set<String>>(emptySet())
    val flaggedGoalIds = _flaggedGoalIds.asStateFlow()

    private val _allTags = MutableStateFlow<List<Tag>>(emptyList())
    val allTags = _allTags.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _clickedGoalId = MutableStateFlow("")
    val clickedGoalId = _clickedGoalId.asStateFlow()

    private val _showHistory = MutableStateFlow(false)
    val showHistory = _showHistory.asStateFlow()

    private val _filterPeriod = MutableStateFlow<BudgetPeriod?>(null)
    val filterPeriod = _filterPeriod.asStateFlow()

    private val _filterDate = MutableStateFlow<LocalDate?>(null)
    val filterDate = _filterDate.asStateFlow()

    private val _draftTitle = MutableStateFlow("")
    val draftTitle = _draftTitle.asStateFlow()

    private val _draftTargetAmount = MutableStateFlow("")
    val draftTargetAmount = _draftTargetAmount.asStateFlow()

    private val _draftTagId = MutableStateFlow<String?>(null)
    val draftTagId = _draftTagId.asStateFlow()

    private val _draftDate = MutableStateFlow(Clock.System.now().toEpochMilliseconds())
    val draftDate = _draftDate.asStateFlow()

    private val _isDateEnabled = MutableStateFlow(false)
    val isDateEnabled = _isDateEnabled.asStateFlow()

    private val _draftIcon = MutableStateFlow("flag")
    val draftIcon = _draftIcon.asStateFlow()

    private val _draftColor = MutableStateFlow("0xFF4CAF50")
    val draftColor = _draftColor.asStateFlow()

    private val _draftImageUri = MutableStateFlow<String?>(null)
    val draftImageUri = _draftImageUri.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _editingGoalId = MutableStateFlow<String?>(null)
    val editingGoalId = _editingGoalId.asStateFlow()

    val isEditing: Flow<Boolean> = _editingGoalId.map { it != null }

    val isFormValid: Flow<Boolean> = combine(_draftTitle, _draftTargetAmount) { title, amount ->
        title.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0
    }

    val filteredGoals = combine(
        _allGoals, _searchQuery, _showHistory, _filterPeriod, _filterDate, _hiddenGoalIds
    ) { args ->
        val goals = args[0] as List<Goal>
        val query = args[1] as String
        val sHistory = args[2] as Boolean
        val hIds = args[5] as Set<String>

        goals.filter { goal ->
            if (hIds.contains(goal.id)) return@filter false
            val matchesHistory = if (sHistory) goal.isAchieved else !goal.isAchieved
            val matchesQuery = goal.title.contains(query, ignoreCase = true) ||
                    (goal.targetAmount.toString().contains(query))
            
            matchesHistory && matchesQuery
        }
    }

    val activeGoal: Flow<Goal?> = combine(_allGoals, _clickedGoalId) { goals, id ->
        goals.find { it.id == id }
    }

    val activeGoalTag: Flow<Tag?> = combine(activeGoal, _allTags) { goal, tags ->
        if (goal?.tagId == null) null else tags.find { it.id == goal.tagId }
    }

    val totalAmountSaved: Flow<Double> = _allGoals.map { list ->
        list.sumOf { it.savedAmount }
    }

    val achievementPercentage: Flow<Int> = _allGoals.map { list ->
        val total = list.sumOf { it.targetAmount }
        if (total > 0) ((list.sumOf { it.savedAmount } / total) * 100).toInt() else 0
    }

    val historyStats: Flow<HistoryStats> = _allGoals.map { list ->
        val completed = list.filter { it.isAchieved }
        HistoryStats(
            completedCount = completed.size,
            totalAchieved = completed.sumOf { it.targetAmount },
            avgCompletionMonths = if (completed.isNotEmpty()) completed.sumOf { it.durationMonths }.toDouble() / completed.size else 0.0
        )
    }

    data class HistoryStats(
        val completedCount: Int,
        val totalAchieved: Double,
        val avgCompletionMonths: Double
    )

    fun getTimeRemaining(goal: Goal): String {
        if (goal.isAchieved) return "Achieved"
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val startDate = goal.creationDate.parseIsoToLocalDate() ?: today
        val endDate = startDate.plus(goal.durationMonths, DateTimeUnit.MONTH)
        val days = today.daysUntil(endDate)
        return when {
            days < 0 -> "Overdue"
            days == 0 -> "Last day"
            days < 30 -> "$days days left"
            else -> "${days / 30} months left"
        }
    }

    fun getRecommendedPerDay(goal: Goal): String {
        if (goal.isAchieved || goal.savedAmount >= goal.targetAmount) return "Goal Achieved"
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val startDate = goal.creationDate.parseIsoToLocalDate() ?: today
        val endDate = startDate.plus(goal.durationMonths, DateTimeUnit.MONTH)
        val days = today.daysUntil(endDate)
        
        if (days <= 0) return "Goal Overdue"
        
        val perDay = (goal.targetAmount - goal.savedAmount) / days
        val whole = perDay.toLong()
        val fraction = ((perDay - whole) * 100).toInt()
        val fracStr = fraction.toString().padStart(2, '0')
        return "₹$whole.$fracStr / day"
    }


    fun getGoalSavingsSummary(goal: Goal): GoalSavingsSummary {
        val totalSaved = goal.savedAmount
        val monthlyDepositAmount = if (goal.monthlySavings > 0 && totalSaved > 0) {
            minOf(totalSaved, goal.monthlySavings * goal.durationMonths)
        } else {
            totalSaved * 0.50
        }
        val manualTopUpAmount = if (totalSaved > 0) maxOf(0.0, totalSaved - monthlyDepositAmount) else 0.0

        val monthlyDepositPercentage = if (totalSaved > 0) ((monthlyDepositAmount / totalSaved) * 100).toInt().coerceIn(1, 99) else 50
        val manualTopUpPercentage = 100 - monthlyDepositPercentage

        val monthlyDepositTimes = if (goal.monthlySavings > 0) {
            (monthlyDepositAmount / goal.monthlySavings).toInt().coerceAtLeast(if (monthlyDepositAmount > 0) 1 else 0)
        } else {
            if (monthlyDepositAmount > 0) 1 else 0
        }
        val manualTopUpTimes = if (manualTopUpAmount > 0) maxOf(1, (manualTopUpAmount / 500.0).toInt()) else 0
        val totalTimes = monthlyDepositTimes + manualTopUpTimes

        return GoalSavingsSummary(
            totalTimesSaving = totalTimes,
            monthlyDepositAmount = monthlyDepositAmount,
            monthlyDepositTimes = monthlyDepositTimes,
            monthlyDepositPercentage = monthlyDepositPercentage,
            manualTopUpAmount = manualTopUpAmount,
            manualTopUpTimes = manualTopUpTimes,
            manualTopUpPercentage = manualTopUpPercentage
        )
    }

    fun updateAllGoals(list: List<Goal>) { _allGoals.value = list }
    fun updateAllTags(list: List<Tag>) { _allTags.value = list }

    fun updateSearchQuery(value: String) { _searchQuery.value = value }
    fun updateClickedGoalId(value: String) { _clickedGoalId.value = value }
    fun toggleHistory() { _showHistory.value = !_showHistory.value }

    fun updateFilterPeriod(period: BudgetPeriod?) { _filterPeriod.value = period }
    fun updateFilterDate(date: LocalDate?) { _filterDate.value = date }

    fun updateDraftTitle(value: String) { _draftTitle.value = value.take(20) }
    fun updateDraftTargetAmount(value: String) { _draftTargetAmount.value = value }
    fun toggleTagSelection(tagId: String) {
        _draftTagId.value = if (_draftTagId.value == tagId) null else tagId
    }
    fun updateDraftDate(value: Long) { _draftDate.value = value }
    fun toggleDateEnabled(value: Boolean) { _isDateEnabled.value = value }
    fun updateDraftIcon(value: String) { _draftIcon.value = value }
    fun updateDraftColor(value: String) { _draftColor.value = value }
    fun updateDraftImageUri(value: String?) { _draftImageUri.value = value }
    fun updateLoading(value: Boolean) { _isLoading.value = value }

    fun prepareEdit(goal: Goal) {
        _editingGoalId.value = goal.id
        _draftTitle.value = goal.title
        _draftTargetAmount.value = goal.targetAmount.toString()
        _draftTagId.value = goal.tagId
        _draftIcon.value = goal.iconResId
        _draftColor.value = goal.colorHex
        _draftImageUri.value = goal.imageUri
        
        try {
            val date = goal.creationDate.parseIsoToLocalDate() ?: LocalDate.parse(goal.creationDate)
            val targetDate = date.plus(goal.durationMonths, DateTimeUnit.MONTH)
            val tz = TimeZone.currentSystemDefault()
            _draftDate.value = Instant.parse("${targetDate}T00:00:00Z").toEpochMilliseconds()
            _isDateEnabled.value = true
        } catch (_: Exception) {
            _draftDate.value = Clock.System.now().toEpochMilliseconds()
            _isDateEnabled.value = false
        }
    }

    fun updateHiddenGoalIds(ids: Set<String>) { _hiddenGoalIds.value = ids }
    fun updateFlaggedGoalIds(ids: Set<String>) { _flaggedGoalIds.value = ids }

    fun clear() {
        _allGoals.value = emptyList()
        _allTags.value = emptyList()
        _searchQuery.value = ""
        _clickedGoalId.value = ""
        _showHistory.value = false
        _filterPeriod.value = null
        _filterDate.value = null
        _isLoading.value = false
        _editingGoalId.value = null
        clearGoalDraft()
    }

    fun clearGoalDraft() {
        _editingGoalId.value = null
        _draftTitle.value = ""
        _draftTargetAmount.value = ""
        _draftTagId.value = null
        _draftDate.value = Clock.System.now().toEpochMilliseconds()
        _isDateEnabled.value = false
        _draftIcon.value = "flag"
        _draftColor.value = "0xFF4CAF50"
        _draftImageUri.value = null
    }

    fun save() {
        _saveGoalIntent.tryEmit(Unit)
    }
}
