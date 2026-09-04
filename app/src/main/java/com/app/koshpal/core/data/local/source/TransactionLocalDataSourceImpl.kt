package com.app.koshpal.core.data.local.source

import com.app.koshpal.core.data.entities.TransactionEntity
import com.app.koshpal.core.data.entities.enums.TransactionType
import com.app.koshpal.core.data.local.dao.TransactionDao
import kotlinx.coroutines.flow.Flow


class TransactionLocalDataSourceImpl(
    private val transactionDao: TransactionDao
) : TransactionLocalDataSource {

    override fun getAllTransactions(): Flow<List<TransactionEntity>> =
        transactionDao.getAllTransactions()

    override fun getTransactionsByType(
        type: TransactionType,
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsByTypeAndDateRange(type, startDate, endDate)

    override fun getTransactionsByCategory(
        categoryId: String
    ): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsByCategory(categoryId)

    override fun getTotalByType(
        startDate: Long,
        endDate: Long
    ): Flow<Double?> =
        transactionDao.getTotalSpentGlobal(startDate, endDate)

    override fun getSpentForCategory(
        categoryName: String,
        startDate: Long,
        endDate: Long
    ): Flow<Double?> =
        transactionDao.getSpentForCategory(categoryName, startDate, endDate)

    override fun getSpentForSubCategory(
        subCategoryName: String,
        startDate: Long,
        endDate: Long
    ): Flow<Double?> =
        transactionDao.getSpentForSubCategory(subCategoryName, startDate, endDate)

    override fun getSpentForCategoryById(categoryId: String, budgetId: String): Flow<Double?> =
        transactionDao.getSpentForCategoryById(categoryId, budgetId)

    override fun getSpentForBudget(budgetId: String): Flow<Double?> =
        transactionDao.getSpentForBudget(budgetId)

    override fun getRecentTransactions(limit: Int): Flow<List<TransactionEntity>> =
        transactionDao.getRecentTransactions(limit)

    override suspend fun insertTransaction(transaction: TransactionEntity): Unit =
         transactionDao.insertTransaction(transaction)

    override suspend fun insertTransactions(transactions: List<TransactionEntity>): Unit =
        transactionDao.insertTransactions(transactions)

    override suspend fun updateTransaction(transaction: TransactionEntity): Unit =
        transactionDao.updateTransaction(transaction)

    override suspend fun deleteTransaction(transaction: TransactionEntity): Unit =
        transactionDao.deleteTransaction(transaction)

    override suspend fun deleteTransactionsByIds(ids: List<String>): Unit =
        transactionDao.deleteTransactionsByIds(ids)

    override suspend fun getContactNameByIdentifier(identifier: String): String? =
        transactionDao.getContactNameByIdentifier(identifier)
}
