package com.app.koshpal.app.domain.usecase.transactionsusecase

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
