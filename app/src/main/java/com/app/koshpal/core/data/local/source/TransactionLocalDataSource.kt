package com.app.koshpal.core.data.local.source

import com.app.koshpal.core.data.entities.TransactionEntity
import com.app.koshpal.core.data.entities.enums.TransactionType
import kotlinx.coroutines.flow.Flow


interface TransactionLocalDataSource {
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    fun getTransactionsByType(type: TransactionType, startDate: Long, endDate: Long): Flow<List<TransactionEntity>>
    fun getTransactionsByCategory(categoryId: String): Flow<List<TransactionEntity>>
    fun getTotalByType(startDate: Long, endDate: Long): Flow<Double?>
    fun getSpentForCategory(categoryName: String, startDate: Long, endDate: Long): Flow<Double?>
    fun getSpentForSubCategory(subCategoryName: String, startDate: Long, endDate: Long): Flow<Double?>
    fun getSpentForCategoryById(categoryId: String, budgetId: String): Flow<Double?>
    fun getSpentForBudget(budgetId: String): Flow<Double?>
    fun getRecentTransactions(limit: Int): Flow<List<TransactionEntity>>
    suspend fun insertTransaction(transaction: TransactionEntity)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)
    suspend fun updateTransaction(transaction: TransactionEntity)
    suspend fun deleteTransaction(transaction: TransactionEntity)
    suspend fun deleteTransactionsByIds(ids: List<String>)
    suspend fun getContactNameByIdentifier(identifier: String): String?
}
