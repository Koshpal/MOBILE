package com.app.koshpal.app.domain.coordinator

import com.app.koshpal.app.Events
import com.app.koshpal.app.StateReflector
import com.app.koshpal.app.data.UserPreferences
import com.app.koshpal.app.domain.model.Transaction
import com.app.koshpal.app.domain.model.Transactions
import com.app.koshpal.app.domain.usecase.authusecase.AuthUseCases
import com.app.koshpal.app.domain.usecase.budgetusecase.BudgetUseCases
import com.app.koshpal.app.domain.usecase.tagusecase.TagUseCases
import com.app.koshpal.app.domain.usecase.transactionsusecase.SmsTransactionSyncer
import com.app.koshpal.app.domain.usecase.transactionsusecase.TransactionQueries
import com.app.koshpal.app.fluxdeck.TransactionsFluxDeck
import com.app.koshpal.app.handleResult
import com.app.koshpal.app.states.SyncStatus
import com.app.koshpal.core.data.entities.enums.TransactionType
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class TransactionsCoordinator(
    private val transactionQueries: TransactionQueries,
    private val smsTransactionSyncer: SmsTransactionSyncer,
    private val budgetUseCases: BudgetUseCases,
    private val tagUseCases: TagUseCases,
    private val userPreferences: UserPreferences,
    private val fluxDeck: TransactionsFluxDeck,
    private val authUseCases: AuthUseCases,
    private val scope: CoroutineScope
) {
    val reflector = StateReflector<Events>(scope)
    val events = reflector.events

    private var lastInitializedTxnId: String? = null

    init {
        sync()
        scope.launch {
            fluxDeck.classifyIntent.collect {
                classifyTransaction()
            }
        }
        scope.launch {
            fluxDeck.transaction.collectLatest { txn ->
                if (txn != null && txn.id != lastInitializedTxnId) {
                    fluxDeck.prepopulate(txn)
                    lastInitializedTxnId = txn.id
                } else if (txn == null) {
                    lastInitializedTxnId = null
                }
            }
        }
    }

    private fun sync() {
        syncSmsTransactions()
        syncRemoteTransactions()
        scope.launch {
            transactionQueries.getAllTransactionsInRange(0, Long.MAX_VALUE)
                .collectLatest { fluxDeck.updateAllTransactions(it) }
        }
        scope.launch {
            budgetUseCases.getAllBudgetsWithDetails()
                .collectLatest { fluxDeck.updateAllBudgets(it) }
        }
        scope.launch {
            tagUseCases.getAllTags()
                .collectLatest { fluxDeck.updateAllTags(it) }
        }
    }

    fun syncRemoteTransactions(page: Int = 1) {
        scope.launch {
            if (userPreferences.isGuestUser.first()) return@launch
            val token = userPreferences.accessToken.first() ?: return@launch

            val result = transactionQueries.getTransactions(token, page = page, limit = 50)
            reflector.handleResult(
                result,
                onError = { error ->
                    if (error == NetworkError.INVALID_USER) {
                        onRefreshToken {
                            syncRemoteTransactions(page = page)
                        }
                    }
                }
            ) { remoteTransactions ->
                transactionQueries.saveLocalTransactions(remoteTransactions)
                val currentResponsePage = remoteTransactions.page ?: page
                if (remoteTransactions.hasMore == true) {
                    syncRemoteTransactions(page = currentResponsePage + 1)
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
                reflector.emitEvent(Events.Success("Token refreshed"))
                onSuccess()
            }
        }
    }

    fun syncSmsTransactions() {
        scope.launch {
            fluxDeck.updateSyncStatus(SyncStatus.Loading)
            val result = smsTransactionSyncer.sync()
            reflector.handleResult(result) { count ->
                fluxDeck.updateSyncStatus(SyncStatus.Success(count))
            }
            if (result is Result.Error) {
                fluxDeck.updateSyncStatus(SyncStatus.Error(result.message ?: "Unknown error"))
            }
            delay(1000.milliseconds)
            fluxDeck.updateSyncStatus(SyncStatus.Idle)
        }
    }

    fun deleteTransactions(ids: List<String>) {
        scope.launch {
            val token = userPreferences.accessToken.first()
            val currentTxns = transactionQueries.getAllTransactionsInRange(0, Long.MAX_VALUE).first().transactions
            val toDelete = currentTxns.filter { ids.contains(it.id) }

            transactionQueries.deleteLocalTransactionsByIds(ids)

            if (token != null && !userPreferences.isGuestUser.first() && toDelete.isNotEmpty()) {
                val result = transactionQueries.deleteTransactions(token, Transactions(toDelete))
                reflector.handleResult(
                    result,
                    onError = { error ->
                        if (error == NetworkError.INVALID_USER) {
                            onRefreshToken()
                            deleteTransactions(ids)
                        }
                    }
                )
            } else {
                reflector.emitEvent(Events.Success("${ids.size} transactions deleted"))
            }
        }
    }

    private suspend fun classifyTransaction() {
        val currentTxn = fluxDeck.transaction.first()

        val resolvedCategoryId = fluxDeck.selectedCategoryId.value ?: fluxDeck.selectedParentCategoryId.value
        val categoryName = if (fluxDeck.selectedBudgetId.value != null && resolvedCategoryId != null) {
            fluxDeck.availableCategories.first().find { it.id == resolvedCategoryId }?.title ?: (currentTxn?.category ?: "Uncategorized")
        } else (currentTxn?.category ?: "Uncategorized")

        val isCash = fluxDeck.isCash.value
        val defaultMode = if (isCash) "Cash" else "Manual"

        val updatedTxn = currentTxn?.copy(
            amount = fluxDeck.amount.value.toDoubleOrNull() ?: currentTxn.amount,
            type = fluxDeck.selectedTransactionType.value ?: currentTxn.type,
            senderName = fluxDeck.senderName.value,
            receiverName = fluxDeck.receiverName.value,
            contactName = fluxDeck.contactName.value.ifBlank { null },
            transactionDate = fluxDeck.date.value,
            bank = fluxDeck.bank.value.ifBlank { if (isCash) "Cash" else "Bank" },
            mode = fluxDeck.mode.value.ifBlank { defaultMode },
            category = categoryName,
            budgetId = fluxDeck.selectedBudgetId.value,
            categoryId = resolvedCategoryId,
            tagIds = fluxDeck.selectedTagIds.value,
            notes = fluxDeck.notes.value.ifBlank { null },
            isBookmarked = fluxDeck.isBookmarked.value,
            isCash = isCash,
            hasReceipt = fluxDeck.hasReceipt.value,
            isExcludedFromCashFlow = fluxDeck.isExcludedFromCashFlow.value
        )
            ?: Transaction(
                id = Uuid.random().toString(),
                accountId = "",
                amount = fluxDeck.amount.value.toDoubleOrNull() ?: 0.0,
                type = fluxDeck.selectedTransactionType.value ?: TransactionType.EXPENSE,
                category = categoryName,
                subCategory = "",
                source = if (isCash) "Cash" else "Manual",
                description = if (isCash) "Cash Transaction" else "Manual Transaction",
                transactionDate = fluxDeck.date.value,
                senderName = fluxDeck.senderName.value,
                receiverName = fluxDeck.receiverName.value,
                bank = fluxDeck.bank.value.ifBlank { if (isCash) "Cash" else "Bank" },
                maskedAccountNo = 0,
                provider = if (isCash) "Cash" else "Manual",
                budgetId = fluxDeck.selectedBudgetId.value,
                categoryId = resolvedCategoryId,
                tagIds = fluxDeck.selectedTagIds.value,
                contactName = fluxDeck.contactName.value.ifBlank { null },
                notes = fluxDeck.notes.value.ifBlank { null },
                isBookmarked = fluxDeck.isBookmarked.value,
                isCash = isCash,
                hasReceipt = fluxDeck.hasReceipt.value,
                isExcludedFromCashFlow = fluxDeck.isExcludedFromCashFlow.value,
                mode = fluxDeck.mode.value.ifBlank { defaultMode }
            )

        fluxDeck.updateLoading(true)
        transactionQueries.updateLocalTransaction(updatedTxn)
        fluxDeck.updateLoading(false)

        reflector.emitEvent(Events.Success("Transaction updated"))
        fluxDeck.clearCreationDraft()
    }
}
