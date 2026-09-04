package com.app.koshpal.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.koshpal.core.data.entities.BudgetAllocationEntity
import com.app.koshpal.core.data.entities.BudgetEntity
import com.app.koshpal.core.data.entities.BudgetHistoryEntity
import com.app.koshpal.core.data.entities.BudgetWithDetails
import kotlinx.coroutines.flow.Flow


@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    @Query("DELETE FROM budgets")
    suspend fun deleteAllBudgets()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllocations(allocations: List<BudgetAllocationEntity>)

    @Query("DELETE FROM budget_allocations WHERE budgetId = :budgetId")
    suspend fun deleteAllocationsForBudget(budgetId: String)

    @Query("SELECT * FROM budgets ORDER BY startDate DESC")
    fun getAllBudgets(): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE startDate LIKE :yearMonthPrefix")
    fun getBudgetsForMonth(yearMonthPrefix: String): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE startDate LIKE :yearPrefix")
    fun getBudgetsForYearly(yearPrefix: String): Flow<List<BudgetEntity>>

    @Transaction
    @Query("SELECT * FROM budgets WHERE id = :id LIMIT 1")
    suspend fun getBudgetById(id: String): BudgetWithDetails?

    @Query("SELECT * FROM budgets WHERE startDate BETWEEN :fromDate AND :toDate")
    fun getBudgetsInRange(fromDate: String, toDate: String): Flow<List<BudgetEntity>>

    @Transaction
    @Query("SELECT * FROM budgets ORDER BY startDate DESC")
    fun getAllBudgetsWithDetails(): Flow<List<BudgetWithDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun archiveBudget(history: BudgetHistoryEntity)

    @Transaction
    @Query("SELECT * FROM budgets WHERE id IN (SELECT budgetId FROM budget_history)")
    fun getArchivedBudgets(): Flow<List<BudgetWithDetails>>
}