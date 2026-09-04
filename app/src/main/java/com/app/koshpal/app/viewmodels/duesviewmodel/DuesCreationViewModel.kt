package com.app.koshpal.app.viewmodels.duesviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.koshpal.app.domain.model.ReminderType
import com.app.koshpal.app.fluxdeck.DuesFluxDeck
import com.app.koshpal.app.domain.coordinator.DuesCoordinator
import com.app.koshpal.core.data.entities.enums.TransactionType
import kotlinx.coroutines.flow.*

class DuesCreationViewModel(
    private val coordinator: DuesCoordinator,
    private val fluxDeck: DuesFluxDeck
) : ViewModel() {

    val reminderTitle = fluxDeck.reminderTitle
    val reminderAmount = fluxDeck.reminderAmount
    val reminderDate = fluxDeck.reminderDate
    val reminderFrequency = fluxDeck.reminderFrequency
    val customFrequencyDays = fluxDeck.customFrequencyDays
    val reminderHour = fluxDeck.reminderHour
    val reminderMinute = fluxDeck.reminderMinute
    val selectedReminderType = fluxDeck.selectedReminderType
    val transactionType = fluxDeck.transactionType
    val isLoading = fluxDeck.isLoading
    val reminderTypes = fluxDeck.reminderTypes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val events = coordinator.events

    val titleSuggestions = fluxDeck.titleSuggestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateReminderTitle(value: String) {
        fluxDeck.updateReminderTitle(value)
        reminderTypes.value.find { it.name.equals(value, ignoreCase = true) }?.let {
            fluxDeck.updateSelectedReminderType(it)
        }
    }
    fun updateReminderAmount(value: String) = fluxDeck.updateReminderAmount(value)
    fun updateReminderDate(value: String) = fluxDeck.updateReminderDate(value)
    fun updateReminderFrequency(value: String) = fluxDeck.updateReminderFrequency(value)
    fun updateCustomFrequencyDays(value: Int?) = fluxDeck.updateCustomFrequencyDays(value)
    fun updateReminderTime(hour: Int, minute: Int) = fluxDeck.updateReminderTime(hour, minute)
    fun updateSelectedReminderType(value: ReminderType?) = fluxDeck.updateSelectedReminderType(value)
    fun updateTransactionType(value: TransactionType) = fluxDeck.updateTransactionType(value)
    fun insertReminderType(reminderType: ReminderType) = coordinator.insertReminderType(reminderType)

    fun insertDue() {
        fluxDeck.save()
    }

    fun clearReminderForm() = fluxDeck.clearReminderForm()
}
