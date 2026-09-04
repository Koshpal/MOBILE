package com.app.koshpal.app.domain.coordinator

import com.app.koshpal.app.Events
import com.app.koshpal.app.StateReflector
import com.app.koshpal.app.domain.model.Transactions
import com.app.koshpal.app.domain.usecase.transactionsusecase.TransactionUseCases
import com.app.koshpal.app.domain.usecase.budgetusecase.BudgetUseCases
import com.app.koshpal.app.domain.usecase.tagusecase.TagUseCases
import com.app.koshpal.app.fluxdeck.CashFluxDeck
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CashCoordinator(
    private val transactionUseCases: TransactionUseCases,
    private val budgetUseCases: BudgetUseCases,
    private val tagUseCases: TagUseCases,
    private val fluxDeck: CashFluxDeck,
    private val scope: CoroutineScope
) {
    val reflector = StateReflector<Events>(scope)
    val events = reflector.events

    init {
        sync()
    }

    private fun sync() {
        scope.launch {
            transactionUseCases.getAllTransactionsInRange(0, Long.MAX_VALUE).collectLatest { fluxDeck.updateAllTransactions(it) }
        }
        scope.launch {
            budgetUseCases.getAllBudgetsWithDetails().collectLatest { fluxDeck.updateAllBudgets(it) }
        }
        scope.launch {
            tagUseCases.getAllTags().collectLatest { fluxDeck.updateAllTags(it) }
        }
    }

    fun deleteTransactions(transactions: Transactions) {
        scope.launch {
            transactionUseCases.deleteLocalTransactions(transactions)
            reflector.emitEvent(Events.Success("${transactions.transactions.size} transactions deleted"))
        }
    }
}
