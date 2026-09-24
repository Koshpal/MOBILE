package com.app.koshpal.app.viewmodels.notificationsviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.koshpal.app.fluxdeck.NotificationsFluxDeck
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val fluxDeck: NotificationsFluxDeck
) : ViewModel() {

    val selectedDate = fluxDeck.selectedDate
    
    val last7Days = fluxDeck.last7Days
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val groupedNotifications = fluxDeck.groupedNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun onDateSelected(timestamp: Long) {
        fluxDeck.updateSelectedDate(timestamp)
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            fluxDeck.markAsRead(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            fluxDeck.clearHistory()
        }
    }

    init {
        viewModelScope.launch {
            fluxDeck.deleteOldNotifications()
        }
    }
}
