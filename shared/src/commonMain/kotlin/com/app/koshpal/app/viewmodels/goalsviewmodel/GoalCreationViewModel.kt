package com.app.koshpal.app.viewmodels.goalsviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.koshpal.app.domain.model.Tag
import com.app.koshpal.app.fluxdeck.GoalFluxDeck
import com.app.koshpal.app.domain.coordinator.GoalCoordinator
import kotlinx.coroutines.flow.*

class GoalCreationViewModel(
    coordinator: GoalCoordinator,
    private val fluxDeck: GoalFluxDeck
) : ViewModel() {

    val events = coordinator.events

    val title = fluxDeck.draftTitle
    val targetAmount = fluxDeck.draftTargetAmount
    val selectedTagId = fluxDeck.draftTagId
    val targetDate = fluxDeck.draftDate
    val isDateEnabled = fluxDeck.isDateEnabled
    val goalIcon = fluxDeck.draftIcon
    val goalColor = fluxDeck.draftColor
    val imageUri = fluxDeck.draftImageUri
    val isLoading = fluxDeck.isLoading

    val isEditing: StateFlow<Boolean> = fluxDeck.isEditing
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isFormValid: StateFlow<Boolean> = fluxDeck.isFormValid
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val allTags: StateFlow<List<Tag>> = fluxDeck.allTags
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateTitle(value: String) = fluxDeck.updateDraftTitle(value)
    fun updateTargetAmount(value: String) = fluxDeck.updateDraftTargetAmount(value)
    fun onTagSelect(id: String) = fluxDeck.toggleTagSelection(id)
    fun updateDate(value: Long) = fluxDeck.updateDraftDate(value)
    fun toggleDateEnabled(value: Boolean) = fluxDeck.toggleDateEnabled(value)
    fun updateIcon(value: String) = fluxDeck.updateDraftIcon(value)
    fun updateColor(value: String) = fluxDeck.updateDraftColor(value)
    fun updateImageUri(value: String?) = fluxDeck.updateDraftImageUri(value)
    fun clearDraft() = fluxDeck.clearGoalDraft()

    fun saveGoal() {
        fluxDeck.save()
    }
}
