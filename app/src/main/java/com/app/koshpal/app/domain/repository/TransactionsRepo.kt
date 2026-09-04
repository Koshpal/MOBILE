package com.app.koshpal.app.domain.repository

import com.app.koshpal.app.domain.model.CommonResponse
import com.app.koshpal.app.domain.model.Transactions
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface TransactionsRepo {
    suspend fun getTransactions(accessToken: String): Result<Transactions, NetworkError>
    suspend fun deleteTransactions(accessToken: String, transaction: Transactions): Result<CommonResponse, NetworkError>

    fun getTotalSpentGlobal(startDate: Long, endDate: Long): Flow<Double?>
    fun getSpentForCategory(categoryName: String, startDate: Long, endDate: Long): Flow<Double?>
    fun getSpentForSubCategory(subCategoryName: String, startDate: Long, endDate: Long): Flow<Double?>
    fun getSpentForCategoryById(categoryId: String, budgetId: String): Flow<Double?>
    fun getSpentForBudget(budgetId: String): Flow<Double?>
    fun getRecentTransactions(limit: Int): Flow<Transactions>
    fun getAllTransactionsInRange(startDate: Long, endDate: Long): Flow<Transactions>
    suspend fun saveLocalTransactions(transactions: Transactions)
    suspend fun deleteLocalTransactions(transactions: Transactions)
    suspend fun deleteLocalTransactionsByIds(ids: List<String>)
    fun getAllLocalTransactions(): Flow<Transactions>
    suspend fun getContactNameByIdentifier(identifier: String): String?

    suspend fun syncRemoteTransactions(transactions: Transactions): Result<CommonResponse, NetworkError>
}
