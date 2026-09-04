package com.app.koshpal.app.domain.usecase

import com.app.koshpal.app.domain.model.Transactions
import com.app.koshpal.app.domain.repository.BudgetRepo
import com.app.koshpal.app.domain.repository.GoalRepo
import com.app.koshpal.app.domain.repository.TransactionsRepo
import com.app.koshpal.app.domain.usecase.budgetusecase.SyncBudgetsUseCase
import com.app.koshpal.app.domain.usecase.goalusecase.SyncGoalsUseCase
import com.app.koshpal.app.domain.usecase.transactionsusecase.SyncSmsTransactionsUseCase
import com.app.koshpal.core.notification.NotificationHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class SyncAllUseCase(
    private val budgetRepo: BudgetRepo,
    private val goalRepo: GoalRepo,
    private val transactionsRepo: TransactionsRepo,
    private val syncBudgetsUseCase: SyncBudgetsUseCase,
    private val syncGoalsUseCase: SyncGoalsUseCase,
    private val syncSmsTransactionsUseCase: SyncSmsTransactionsUseCase,
    private val notificationHelper: NotificationHelper? = null,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend operator fun invoke() = withContext(defaultDispatcher) {
        notificationHelper?.showSyncNotification()
        try {
            val localTransactions = transactionsRepo.getAllLocalTransactions().first()
            val unsyncedTxns = localTransactions.transactions.filter { !it.isSynced }
            if (unsyncedTxns.isNotEmpty()) {
                transactionsRepo.syncRemoteTransactions(Transactions(unsyncedTxns))
            }

            val localBudgets = budgetRepo.getAllBudgets().first()
            localBudgets.filter { !it.isSynced }.forEach { budget ->
                budgetRepo.syncRemoteBudget(budget)
            }

            val localGoals = goalRepo.getAllGoals().first()
            localGoals.filter { !it.isSynced }.forEach { goal ->
                goalRepo.syncRemoteGoal(goal)
            }

            syncBudgetsUseCase()
            syncGoalsUseCase()
            syncSmsTransactionsUseCase()
        } finally {
            notificationHelper?.cancelSyncNotification()
        }
    }
}
