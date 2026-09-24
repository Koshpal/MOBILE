package com.app.koshpal.core.data.local.source

import com.app.koshpal.core.data.entities.BudgetAllocationEntity
import com.app.koshpal.core.data.entities.BudgetEntity
import com.app.koshpal.core.data.entities.BudgetHistoryEntity
import com.app.koshpal.core.data.entities.BudgetWithDetails
import com.app.koshpal.core.data.local.dao.BudgetDao
import kotlinx.coroutines.flow.Flow

class BudgetLocalDataSourceImpl(
    private val budgetDao: BudgetDao
) : BudgetLocalDataSource {

    override fun getAllBudgets(): Flow<List<BudgetEntity>> =
        budgetDao.getAllBudgets()

    override fun getBudgetsInRange(
        fromDate: String,
        toDate: String
    ): Flow<List<BudgetEntity>> =
        budgetDao.getBudgetsInRange(fromDate, toDate)

    override fun getBudgetsForMonth(
        month: Int,
        year: Int
    ): Flow<List<BudgetEntity>> {
        val yearStr = year.toString().padStart(4, '0')
        val monthStr = month.toString().padStart(2, '0')
        val yearMonthPrefix = "${yearStr}-${monthStr}%"
        return budgetDao.getBudgetsForMonth(yearMonthPrefix)
    }

    override fun getBudgetsForYearly(
        year: Int
    ): Flow<List<BudgetEntity>> {
        val yearStr = year.toString().padStart(4, '0')
        val yearPrefix = "${yearStr}%"
        return budgetDao.getBudgetsForYearly(yearPrefix)
    }

    override fun getAllBudgetsWithDetails(): Flow<List<BudgetWithDetails>> =
        budgetDao.getAllBudgetsWithDetails()

    override suspend fun getBudgetById(
        id: String
    ): BudgetWithDetails? =
        budgetDao.getBudgetById(id)

    override suspend fun insertBudget(budget: BudgetEntity) =
        budgetDao.insertBudget(budget)

    override suspend fun updateBudget(budget: BudgetEntity) =
        budgetDao.updateBudget(budget)

    override suspend fun deleteBudget(budget: BudgetEntity) =
        budgetDao.deleteBudget(budget)

    override suspend fun deleteAllBudgets() =
        budgetDao.deleteAllBudgets()

    override suspend fun insertAllocations(allocations: List<BudgetAllocationEntity>) =
        budgetDao.insertAllocations(allocations)

    override suspend fun deleteAllocationsForBudget(budgetId: String) =
        budgetDao.deleteAllocationsForBudget(budgetId)

    override suspend fun archiveBudget(history: BudgetHistoryEntity) = budgetDao.archiveBudget(history)
    override fun getArchivedBudgets(): Flow<List<BudgetWithDetails>> = budgetDao.getArchivedBudgets()
}
