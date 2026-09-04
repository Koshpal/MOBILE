package com.app.koshpal.app.domain.repository

import com.app.koshpal.app.domain.model.Budget
import com.app.koshpal.app.domain.model.CommonResponse
import com.app.koshpal.core.domain.util.DatabaseCallError
import com.app.koshpal.core.domain.util.NetworkError
import kotlinx.coroutines.flow.Flow
import com.app.koshpal.core.domain.util.Result


interface BudgetRepo {
    fun getAllBudgets(): Flow<List<Budget>>
    fun getBudgetsInRange(fromDate: String, toDate: String): Flow<List<Budget>>
    fun getBudgetsForMonth(month: Int, year: Int): Flow<List<Budget>>
    fun getBudgetsForYearly(year: Int): Flow<List<Budget>>
    fun getAllBudgetsWithDetails(): Flow<List<Budget>>
    fun getArchivedBudgets(): Flow<List<Budget>>

    suspend fun getBudgetById(id: String):  Result<Budget?, DatabaseCallError>
    suspend fun insertBudget(budget: Budget):  Result<Unit, DatabaseCallError>
    suspend fun updateBudget(budget: Budget):  Result<Unit, DatabaseCallError>
    suspend fun deleteBudget(budget: Budget):  Result<Unit, DatabaseCallError>
    suspend fun deleteAllBudgets():  Result<Unit, DatabaseCallError>
    suspend fun archiveBudget(budgetId: String)

    suspend fun syncBudgets(budgets: List<Budget>): Result<Unit, DatabaseCallError>
    suspend fun syncRemoteBudget(budget: Budget): Result<CommonResponse, NetworkError>
    suspend fun updateRemoteBudget(budget: Budget): Result<CommonResponse, NetworkError>
}
