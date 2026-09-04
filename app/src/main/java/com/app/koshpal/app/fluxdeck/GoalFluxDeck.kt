package com.app.koshpal.app.fluxdeck

import com.app.koshpal.app.domain.model.*
import com.app.koshpal.core.data.entities.enums.BudgetPeriod
import com.app.koshpal.core.presentation.util.parseIsoToLocalDate
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

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

    private val _draftDate = MutableStateFlow(System.currentTimeMillis())
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
        val startDate = goal.creationDate.parseIsoToLocalDate() ?: LocalDate.now()
        val endDate = startDate.plusMonths(goal.durationMonths.toLong())
        val days = ChronoUnit.DAYS.between(LocalDate.now(), endDate)
        return when {
            days < 0 -> "Overdue"
            days == 0L -> "Last day"
            days < 30 -> "$days days left"
            else -> "${days / 30} months left"
        }
    }

    fun getRecommendedPerDay(goal: Goal): String {
        if (goal.isAchieved || goal.savedAmount >= goal.targetAmount) return "Goal Achieved"
        val startDate = goal.creationDate.parseIsoToLocalDate() ?: LocalDate.now()
        val endDate = startDate.plusMonths(goal.durationMonths.toLong())
        val days = ChronoUnit.DAYS.between(LocalDate.now(), endDate)
        
        if (days <= 0) return "N/A"
        
        val perDay = (goal.targetAmount - goal.savedAmount) / days
        return String.format(Locale.ENGLISH, "₹%.2f / day", perDay)
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
        
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
        try {
            val date = LocalDate.parse(goal.creationDate, formatter)
            val targetDate = date.plusMonths(goal.durationMonths.toLong())
            _draftDate.value = targetDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            _isDateEnabled.value = true
        } catch (_: Exception) {
            _draftDate.value = System.currentTimeMillis()
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
        _draftDate.value = System.currentTimeMillis()
        _isDateEnabled.value = false
        _draftIcon.value = "flag"
        _draftColor.value = "0xFF4CAF50"
        _draftImageUri.value = null
    }

    fun save() {
        _saveGoalIntent.tryEmit(Unit)
    }
}
