package com.app.koshpal.app.viewmodels.tagsviewmodel



import androidx.lifecycle.ViewModel
import com.app.koshpal.app.domain.model.Tag
import com.app.koshpal.app.fluxdeck.TagsFluxDeck
import com.app.koshpal.app.domain.coordinator.TagsCoordinator

class TagsCreationViewModel(
    coordinator: TagsCoordinator,
    private val fluxDeck: TagsFluxDeck
) : ViewModel() {

    val events = coordinator.events

    val tagName = fluxDeck.tagName
    val tagBudgetGoal = fluxDeck.tagBudgetGoal
    val tagColor = fluxDeck.tagColor
    val lastCreatedTagId = fluxDeck.lastCreatedTagId
    val isLoading = fluxDeck.isLoading

    fun updateTagName(value: String) = fluxDeck.updateTagName(value)
    fun updateTagBudgetGoal(value: String) = fluxDeck.updateTagBudgetGoal(value)
    fun updateTagColor(value: String) = fluxDeck.updateTagColor(value)
    fun clearForm() = fluxDeck.clearForm()

    fun createTag() {
        fluxDeck.save()
    }
}
