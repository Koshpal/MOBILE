package com.app.koshpal.core.data.local.source

import com.app.koshpal.core.data.entities.BudgetAllocationEntity
import com.app.koshpal.core.data.entities.BudgetEntity
import com.app.koshpal.core.data.entities.BudgetHistoryEntity
import com.app.koshpal.core.data.entities.BudgetWithDetails
import kotlinx.coroutines.flow.Flow

interface BudgetLocalDataSource {
    fun getAllBudgets(): Flow<List<BudgetEntity>>
    fun getBudgetsInRange(fromDate: String, toDate: String): Flow<List<BudgetEntity>>
    fun getBudgetsForMonth(month: Int, year: Int): Flow<List<BudgetEntity>>
    fun getBudgetsForYearly(year: Int): Flow<List<BudgetEntity>>
    fun getAllBudgetsWithDetails(): Flow<List<BudgetWithDetails>>

    suspend fun getBudgetById(id: String): BudgetWithDetails?
    suspend fun insertBudget(budget: BudgetEntity)
    suspend fun updateBudget(budget: BudgetEntity)
    suspend fun deleteBudget(budget: BudgetEntity)
    suspend fun deleteAllBudgets()

    suspend fun insertAllocations(allocations: List<BudgetAllocationEntity>)
    suspend fun deleteAllocationsForBudget(budgetId: String)
    suspend fun archiveBudget(history: BudgetHistoryEntity)
    fun getArchivedBudgets(): Flow<List<BudgetWithDetails>>
}