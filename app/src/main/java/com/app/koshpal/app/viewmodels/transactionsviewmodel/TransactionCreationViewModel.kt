package com.app.koshpal.app.viewmodels.transactionsviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.koshpal.app.domain.coordinator.TransactionsCoordinator
import com.app.koshpal.app.domain.model.Tag
import com.app.koshpal.app.fluxdeck.TransactionsFluxDeck
import com.app.koshpal.core.data.entities.enums.BudgetType
import com.app.koshpal.core.data.entities.enums.TransactionType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class TransactionCreationViewModel(
    coordinator: TransactionsCoordinator,
    private val fluxDeck: TransactionsFluxDeck,
) : ViewModel() {

    val selectedBudgetType = fluxDeck.selectedBudgetType
    val selectedBudgetId = fluxDeck.selectedBudgetId
    val selectedParentCategoryId = fluxDeck.selectedParentCategoryId
    val selectedCategoryId = fluxDeck.selectedCategoryId
    val selectedTagIds = fluxDeck.selectedTagIds
    val selectedTransactionType = fluxDeck.selectedTransactionType
    val notes = fluxDeck.notes
    val senderName = fluxDeck.senderName
    val receiverName = fluxDeck.receiverName
    val contactName = fluxDeck.contactName
    val amount = fluxDeck.amount
    val bank = fluxDeck.bank
    val mode = fluxDeck.mode
    val date = fluxDeck.date
    val isBookmarked = fluxDeck.isBookmarked
    val isCash = fluxDeck.isCash
    val hasReceipt = fluxDeck.hasReceipt
    val isExcludedFromCashFlow = fluxDeck.isExcludedFromCashFlow
    val isFromNotification = fluxDeck.isFromNotification
    val isLoading = fluxDeck.isLoading

    val allBudgets = fluxDeck.allBudgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transaction = fluxDeck.transaction
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val availableBudgets = fluxDeck.availableBudgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTags: StateFlow<List<Tag>> = fluxDeck.allTags
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val events = coordinator.events

    fun onAmountChange(value: String) = fluxDeck.updateAmount(value)
    fun onBankChange(value: String) = fluxDeck.updateBank(value)
    fun onModeChange(value: String) = fluxDeck.updateMode(value)
    fun onSenderNameChange(value: String) = fluxDeck.updateSenderName(value)
    fun onReceiverNameChange(value: String) = fluxDeck.updateReceiverName(value)
    fun onContactNameChange(value: String) = fluxDeck.updateContactName(value)
    fun onTransactionTypeSelect(type: TransactionType) = fluxDeck.updateTransactionType(type)
    fun onDateChange(value: Long) = fluxDeck.updateDate(value)
    fun onBudgetTypeSelect(type: BudgetType) = fluxDeck.updateBudgetType(type)
    fun onBudgetSelect(id: String) = fluxDeck.updateBudgetId(id)
    fun onParentCategorySelect(id: String) = fluxDeck.updateParentCategoryId(id)
    fun onCategorySelect(id: String) = fluxDeck.updateCategoryId(id)
    fun onTagToggle(id: String) = fluxDeck.onTagToggle(id)
    fun onTagAdd(id: String) = fluxDeck.onTagAdd(id)
    fun onNotesChange(value: String) = fluxDeck.updateNotes(value)
    fun onBookmarkedToggle(value: Boolean) = fluxDeck.updateIsBookmarked(value)
    fun onCashToggle(value: Boolean) = fluxDeck.updateIsCash(value)
    fun onReceiptToggle(value: Boolean) = fluxDeck.updateHasReceipt(value)
    fun onExcludeToggle(value: Boolean) = fluxDeck.updateIsExcludedFromCashFlow(value)

    fun classifyTransaction() {
        fluxDeck.classify()
    }
}
