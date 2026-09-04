package com.app.koshpal.app.domain.usecase.transactionsusecase

import com.app.koshpal.app.domain.model.Transaction
import com.app.koshpal.app.domain.model.Transactions
import com.app.koshpal.app.domain.repository.TransactionsRepo
import kotlinx.coroutines.flow.Flow

data class TransactionUseCases(
    val createTransactions: CreateTransactionUseCase,
    val getTransactions: GetTransactionUseCase,
    val deleteTransactions: DeleteTransactionUseCase,
    val getTotalSpent: GetTotalSpentUseCase,
    val getCategorySpent: GetCategorySpentUseCase,
    val getSubCategorySpent: GetSubCategorySpentUseCase,
    val getCategorySpentById: GetSpentForCategoryByIdUseCase,
    val getRecentTransactions: GetRecentTransactionsUseCase,
    val getAllTransactionsInRange: GetAllTransactionsInRangeUseCase,
    val syncSmsTransactions: SyncSmsTransactionsUseCase,
    val processIncomingSms: ProcessIncomingSmsUseCase,
    val updateLocalTransaction: UpdateLocalTransactionUseCase,
    val deleteLocalTransactions: DeleteLocalTransactionsUseCase,
    val deleteLocalTransactionsByIds: DeleteLocalTransactionsByIdsUseCase,
    val getSpentForBudget: GetSpentForBudgetUseCase
)


//Local-Room Use-cases
class GetSpentForBudgetUseCase(private val repository: TransactionsRepo) {
    operator fun invoke(budgetId: String): Flow<Double?> {
        return repository.getSpentForBudget(budgetId)
    }
}

class UpdateLocalTransactionUseCase(private val repository: TransactionsRepo) {
    suspend operator fun invoke(transaction: Transaction) {
        repository.saveLocalTransactions(Transactions(listOf(transaction)))
    }
}

class DeleteLocalTransactionsUseCase(private val repository: TransactionsRepo) {
    suspend operator fun invoke(transactions: Transactions) {
        repository.deleteLocalTransactions(transactions)
    }
}

class DeleteLocalTransactionsByIdsUseCase(private val repository: TransactionsRepo) {
    suspend operator fun invoke(ids: List<String>) {
        repository.deleteLocalTransactionsByIds(ids)
    }
}

class GetTotalSpentUseCase(private val repository: TransactionsRepo) {
    operator fun invoke(startDate: Long, endDate: Long): Flow<Double?> {
        return repository.getTotalSpentGlobal(startDate, endDate)
    }
}

class GetCategorySpentUseCase(private val repository: TransactionsRepo) {
    operator fun invoke(categoryName: String, startDate: Long, endDate: Long): Flow<Double?> {
        return repository.getSpentForCategory(categoryName, startDate, endDate)
    }
}

class GetSubCategorySpentUseCase(private val repository: TransactionsRepo) {
    operator fun invoke(subCategoryName: String, startDate: Long, endDate: Long): Flow<Double?> {
        return repository.getSpentForSubCategory(subCategoryName, startDate, endDate)
    }
}

class GetSpentForCategoryByIdUseCase(private val repository: TransactionsRepo) {
    operator fun invoke(categoryId: String, budgetId: String): Flow<Double?> {
        return repository.getSpentForCategoryById(categoryId, budgetId)
    }
}

class GetRecentTransactionsUseCase(private val repository: TransactionsRepo) {
    operator fun invoke(limit: Int): Flow<Transactions> {
        return repository.getRecentTransactions(limit)
    }
}

class GetAllTransactionsInRangeUseCase(private val repository: TransactionsRepo) {
    operator fun invoke(startDate: Long, endDate: Long): Flow<Transactions> {
        return repository.getAllTransactionsInRange(startDate, endDate)
    }
}
