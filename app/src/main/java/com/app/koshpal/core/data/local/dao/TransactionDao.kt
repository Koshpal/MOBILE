package com.app.koshpal.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.koshpal.core.data.entities.TransactionEntity
import com.app.koshpal.core.data.entities.enums.TransactionType
import kotlinx.coroutines.flow.Flow


@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id IN (:ids)")
    suspend fun deleteTransactionsByIds(ids: List<String>)

    @Query("DELETE FROM transactions WHERE accountId = :accountId")
    fun deleteAllUserTransactions(accountId: String)

    @Query("SELECT * FROM transactions ORDER BY transactionDate DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY transactionDate DESC")
    fun getTransactionsByCategory(category: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type AND transactionDate BETWEEN :startDate AND :endDate ORDER BY transactionDate DESC")
    fun getTransactionsByTypeAndDateRange(
        type: TransactionType,
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE accountId = :userId AND type = :type AND transactionDate BETWEEN :startDate AND :endDate AND isExcludedFromCashFlow = 0")
    fun getTotalAmountForTypeAndDateRange(
        userId: String,
        type: TransactionType,
        startDate: Long,
        endDate: Long
    ): Flow<Double?>


    @Query("SELECT SUM(CASE WHEN type = 'EXPENSE' THEN amount WHEN type = 'INCOME' THEN -amount ELSE 0 END) FROM transactions WHERE type IN ('EXPENSE', 'INCOME') AND transactionDate BETWEEN :startDate AND :endDate AND isExcludedFromCashFlow = 0")
    fun getTotalSpentGlobal(
        startDate: Long,
        endDate: Long
    ): Flow<Double?>


    @Query("SELECT SUM(CASE WHEN type = 'EXPENSE' THEN amount WHEN type = 'INCOME' THEN -amount ELSE 0 END) FROM transactions WHERE category = :categoryName AND transactionDate BETWEEN :startDate AND :endDate AND type IN ('EXPENSE', 'INCOME') AND isExcludedFromCashFlow = 0")
    fun getSpentForCategory(categoryName: String, startDate: Long, endDate: Long): Flow<Double?>

    @Query("SELECT SUM(CASE WHEN type = 'EXPENSE' THEN amount WHEN type = 'INCOME' THEN -amount ELSE 0 END) FROM transactions WHERE subCategory = :subCategoryName AND transactionDate BETWEEN :startDate AND :endDate AND type IN ('EXPENSE', 'INCOME') AND isExcludedFromCashFlow = 0")
    fun getSpentForSubCategory(subCategoryName: String, startDate: Long, endDate: Long): Flow<Double?>

    @Query("SELECT SUM(CASE WHEN type = 'EXPENSE' THEN amount WHEN type = 'INCOME' THEN -amount ELSE 0 END) FROM transactions WHERE categoryId = :categoryId AND budgetId = :budgetId AND type IN ('EXPENSE', 'INCOME') AND isExcludedFromCashFlow = 0")
    fun getSpentForCategoryById(categoryId: String, budgetId: String): Flow<Double?>

    @Query("SELECT SUM(CASE WHEN type = 'EXPENSE' THEN amount WHEN type = 'INCOME' THEN -amount ELSE 0 END) FROM transactions WHERE budgetId = :budgetId AND type IN ('EXPENSE', 'INCOME') AND isExcludedFromCashFlow = 0")
    fun getSpentForBudget(budgetId: String): Flow<Double?>

    @Query("SELECT * FROM transactions ORDER BY transactionDate DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int): Flow<List<TransactionEntity>>

    @Query("""
    SELECT contactName FROM transactions 
    WHERE (senderName = :identifier OR receiverName = :identifier OR contactName = :identifier) 
    AND contactName IS NOT NULL 
    LIMIT 1
""")
    suspend fun getContactNameByIdentifier(identifier: String): String?
}
