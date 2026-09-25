package com.app.koshpal.app.fluxdeck

import com.app.koshpal.app.domain.model.Due
import com.app.koshpal.app.domain.model.ReminderType
import com.app.koshpal.core.data.entities.enums.TransactionType
import com.app.koshpal.core.presentation.util.parseIsoToLocalDate
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

class DuesFluxDeck {

    private val _saveIntent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val saveIntent = _saveIntent.asSharedFlow()

    private val _allDues = MutableStateFlow<List<Due>>(emptyList())
    val allDues = _allDues.asStateFlow()

    private val _reminderTypes = MutableStateFlow<List<ReminderType>>(emptyList())
    val reminderTypes = _reminderTypes.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedTab = MutableStateFlow("upcoming")
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

    private val _showCompletedReminders = MutableStateFlow(false)
    val showCompletedReminders: StateFlow<Boolean> = _showCompletedReminders.asStateFlow()

    private val _filterDate = MutableStateFlow<LocalDate?>(null)
    val filterDate: StateFlow<LocalDate?> = _filterDate.asStateFlow()

    private val _isItemClicked = MutableStateFlow(false)
    val isItemClicked: StateFlow<Boolean> = _isItemClicked.asStateFlow()

    private val _clickedDueId = MutableStateFlow("")
    val clickedDueId: StateFlow<String> = _clickedDueId.asStateFlow()

    private val _reminderTitle = MutableStateFlow("")
    val reminderTitle: StateFlow<String> = _reminderTitle.asStateFlow()

    private val _reminderAmount = MutableStateFlow("")
    val reminderAmount: StateFlow<String> = _reminderAmount.asStateFlow()

    private val _reminderDate = MutableStateFlow("")
    val reminderDate: StateFlow<String> = _reminderDate.asStateFlow()

    private val _reminderFrequency = MutableStateFlow("Do not repeat")
    val reminderFrequency: StateFlow<String> = _reminderFrequency.asStateFlow()

    private val _customFrequencyDays = MutableStateFlow<Int?>(null)
    val customFrequencyDays: StateFlow<Int?> = _customFrequencyDays.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _reminderHour = MutableStateFlow(9)
    val reminderHour: StateFlow<Int> = _reminderHour.asStateFlow()

    private val _reminderMinute = MutableStateFlow(0)
    val reminderMinute: StateFlow<Int> = _reminderMinute.asStateFlow()

    private val _selectedReminderType = MutableStateFlow<ReminderType?>(null)
    val selectedReminderType: StateFlow<ReminderType?> = _selectedReminderType.asStateFlow()

    private val _transactionType = MutableStateFlow(TransactionType.EXPENSE)
    val transactionType: StateFlow<TransactionType> = _transactionType.asStateFlow()

    private val _excludedDueIds = MutableStateFlow<Set<String>>(emptySet())

    val searchSuggestions = flowOf(listOf("Upcoming", "Overdue", "Monthly", "Rent", "Salary"))

    val filteredDues = combine(
        _allDues, _searchQuery, _selectedTab, 
        _showCompletedReminders, _filterDate, _excludedDueIds
    ) { args ->
        @Suppress("UNCHECKED_CAST")
        val list = args[0] as List<Due>
        val query = args[1] as String
        val tab = args[2] as String
        val showCompleted = args[3] as Boolean
        val fDate = args[4] as LocalDate?
        @Suppress("UNCHECKED_CAST")
        val eIds = args[5] as Set<String>

        list.filter { due ->
            if (eIds.contains(due.id)) return@filter false
            
            if (!showCompleted && due.isCompleted) return@filter false

            val matchesQuery = (due.title.contains(query, ignoreCase = true) ||
                    due.amount.toString().contains(query) ||
                    due.reminderType?.contains(query, ignoreCase = true) == true)

            val matchesTab = when (tab) {
                "upcoming" -> !isOverdue(due.date)
                "overdue" -> isOverdue(due.date)
                else -> true
            }

            val matchesDate = if (fDate != null) {
                val dueLocalDate = due.date.parseIsoToLocalDate()
                dueLocalDate?.let { it.month.number == fDate.month.number && it.year == fDate.year } ?: true
            } else true

            matchesQuery && matchesTab && matchesDate
        }
    }

    val totalUpcomingAmount = filteredDues.map { list ->
        list.filter { !it.isCompleted && !isOverdue(it.date) }.sumOf { it.amount }
    }

    val totalOverdueAmount = filteredDues.map { list ->
        list.filter { !it.isCompleted && isOverdue(it.date) }.sumOf { it.amount }
    }

    val titleSuggestions = _allDues.map { list ->
        val mostUsed = list.groupBy { it.title }.mapValues { it.value.size }.toList().sortedByDescending { it.second }.take(3).map { it.first }
        if (mostUsed.size < 3) {
            val defaults = listOf("Rent", "Electricity Bill", "Internet", "Water Bill", "Subscription")
            (mostUsed + defaults).distinct().take(3)
        } else mostUsed
    }

    private fun isOverdue(dateStr: String): Boolean {
        val date = dateStr.parseIsoToLocalDate() ?: return false
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        return date < today
    }

    fun updateAllDues(list: List<Due>) { _allDues.value = list }
    fun updateReminderTypes(list: List<ReminderType>) { _reminderTypes.value = list }
    fun updateExcludedDueIds(ids: Set<String>) { _excludedDueIds.value = ids }

    fun updateSearchQuery(value: String) { _searchQuery.value = value }
    fun updateSelectedTab(value: String) { _selectedTab.value = value }
    fun toggleShowCompletedReminders() { _showCompletedReminders.value = !_showCompletedReminders.value }
    fun updateFilterDate(value: LocalDate?) { _filterDate.value = value }

    fun updateClickedDueId(value: String) { _clickedDueId.value = value }
    fun updateIsItemClicked(value: Boolean) { _isItemClicked.value = value }

    fun updateReminderTitle(value: String) { _reminderTitle.value = value.take(20) }
    fun updateReminderAmount(value: String) { _reminderAmount.value = value }
    fun updateReminderDate(value: String) { _reminderDate.value = value }
    fun updateReminderFrequency(value: String) { _reminderFrequency.value = value }
    fun updateCustomFrequencyDays(value: Int?) { _customFrequencyDays.value = value }
    fun updateLoading(value: Boolean) { _isLoading.value = value }
    fun updateReminderTime(hour: Int, minute: Int) {
        _reminderHour.value = hour
        _reminderMinute.value = minute
    }
    fun updateSelectedReminderType(value: ReminderType?) { _selectedReminderType.value = value }
    fun updateTransactionType(value: TransactionType) { _transactionType.value = value }

    fun clear() {
        _allDues.value = emptyList()
        _reminderTypes.value = emptyList()
        _searchQuery.value = ""
        _selectedTab.value = "upcoming"
        _showCompletedReminders.value = false
        _filterDate.value = null
        _isItemClicked.value = false
        _clickedDueId.value = ""
        _excludedDueIds.value = emptySet()
        clearReminderForm()
    }

    fun clearReminderForm() {
        _clickedDueId.value = ""
        _reminderTitle.value = ""
        _reminderAmount.value = ""
        _reminderDate.value = ""
        _reminderFrequency.value = "Do not repeat"
        _customFrequencyDays.value = null
        _reminderHour.value = 9
        _reminderMinute.value = 0
        _selectedReminderType.value = null
        _transactionType.value = TransactionType.EXPENSE
    }

    fun prepopulateDue(due: Due) {
        _clickedDueId.value = due.id
        _reminderTitle.value = due.title
        _reminderAmount.value = due.amount.let { if (it == 0.0) "" else if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() }
        _reminderDate.value = due.date
        _reminderFrequency.value = due.frequency.ifBlank { "Do not repeat" }
        _customFrequencyDays.value = due.customFrequencyDays
        _transactionType.value = try { TransactionType.valueOf(due.type) } catch(_: Exception) { TransactionType.EXPENSE }
        
        val typeMatch = _reminderTypes.value.find { it.name.equals(due.reminderType, ignoreCase = true) }
        _selectedReminderType.value = typeMatch ?: due.reminderType?.let { name ->
            ReminderType(
                name = name,
                iconResId = due.iconResId,
                colorHex = due.colorHex ?: "0xFFC45100"
            )
        }

        val reminderTime = due.reminderTime
        if (reminderTime != null && reminderTime > 0) {
            try {
                val dt = Instant.fromEpochMilliseconds(reminderTime).toLocalDateTime(TimeZone.currentSystemDefault())
                _reminderHour.value = dt.hour
                _reminderMinute.value = dt.minute
            } catch (_: Exception) {
                _reminderHour.value = 9
                _reminderMinute.value = 0
            }
        } else {
            _reminderHour.value = 9
            _reminderMinute.value = 0
        }
    }

    fun save() {
        _saveIntent.tryEmit(Unit)
    }
}
