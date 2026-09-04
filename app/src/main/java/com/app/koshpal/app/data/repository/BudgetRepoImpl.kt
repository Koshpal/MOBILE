package com.app.koshpal.app.data.repository

import com.app.koshpal.app.data.UserPreferences
import com.app.koshpal.app.data.mapper.toBudget
import com.app.koshpal.app.data.mapper.toBudgetDto
import com.app.koshpal.app.data.mapper.toBudgetEntity
import com.app.koshpal.app.data.mapper.toCategoryEntity
import com.app.koshpal.app.data.mapper.toCommonResponse
import com.app.koshpal.app.domain.model.Budget
import com.app.koshpal.app.domain.model.CommonResponse
import com.app.koshpal.app.domain.repository.BudgetRepo
import com.app.koshpal.core.data.entities.BudgetAllocationEntity
import com.app.koshpal.core.data.entities.BudgetHistoryEntity
import com.app.koshpal.core.data.local.source.BudgetLocalDataSource
import com.app.koshpal.core.data.local.source.CategoryLocalDataSource
import com.app.koshpal.core.data.networking.safeDatabaseCall
import com.app.koshpal.core.data.remote.dto.BudgetDto
import com.app.koshpal.core.data.remote.dto.CommonResponseDto
import com.app.koshpal.core.domain.util.DatabaseCallError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.app.koshpal.core.domain.util.Result
import com.app.koshpal.core.data.remote.source.BudgetDataSource
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class BudgetRepoImpl(
    private val localDataSource: BudgetLocalDataSource,
    private val categoryLocalDataSource: CategoryLocalDataSource,
    private val remoteDataSource: BudgetDataSource,
    private val userPreferences: UserPreferences,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BudgetRepo {

    override fun getAllBudgets(): Flow<List<Budget>> {
        return localDataSource.getAllBudgets().map { entities ->
            entities.map { it.toBudget() }
        }
    }

    override fun getBudgetsInRange(
        fromDate: String,
        toDate: String
    ): Flow<List<Budget>> {
        return localDataSource.getBudgetsInRange(fromDate, toDate).map { entities ->
            entities.map { it.toBudget() }
        }
    }

    override fun getBudgetsForMonth(
        month: Int,
        year: Int
    ): Flow<List<Budget>> {
        return localDataSource.getBudgetsForMonth(month, year).map { entities ->
            entities.map { it.toBudget() }
        }
    }

    override fun getBudgetsForYearly(
        year: Int
    ): Flow<List<Budget>> {
        return localDataSource.getBudgetsForYearly(year).map { entities ->
            entities.map { it.toBudget() }
        }
    }

    override fun getAllBudgetsWithDetails(): Flow<List<Budget>> {
        return localDataSource.getAllBudgetsWithDetails().map { entities ->
            entities.map { it.toBudget() }
        }
    }

    override suspend fun getBudgetById(id: String): Result<Budget?, DatabaseCallError> {
        return safeDatabaseCall {
            val details = localDataSource.getBudgetById(id)
            details?.toBudget()
        }
    }

    override suspend fun insertBudget(budget: Budget): Result<Unit, DatabaseCallError> {
        return safeDatabaseCall {
            budget.allocations.forEach { allocation ->
                allocation.category?.let { category ->
                    val existing = categoryLocalDataSource.getCategoryById(category.id)
                    if (existing == null) {
                        categoryLocalDataSource.insertCategory(category.toCategoryEntity())
                    }
                }
            }

            localDataSource.insertBudget(budget.toBudgetEntity())
            val allocationEntities = budget.allocations.map { domainAlloc ->
                BudgetAllocationEntity(
                    id = domainAlloc.id,
                    budgetId = budget.id,
                    categoryId = domainAlloc.categoryId,
                    allocatedAmount = domainAlloc.allocatedAmount
                )
            }
            if (allocationEntities.isNotEmpty()) {
                localDataSource.insertAllocations(allocationEntities)
            }

            // Standardized Sync Push
            syncRemoteBudget(budget = budget)
        }
    }

    override suspend fun updateBudget(budget: Budget): Result<Unit, DatabaseCallError> {
        return safeDatabaseCall {
            localDataSource.updateBudget(budget.toBudgetEntity())
            localDataSource.deleteAllocationsForBudget(budget.id)
            val allocationEntities = budget.allocations.map { domainAlloc ->
                BudgetAllocationEntity(
                    id = domainAlloc.id,
                    budgetId = budget.id,
                    categoryId = domainAlloc.categoryId,
                    allocatedAmount = domainAlloc.allocatedAmount
                )
            }
            if (allocationEntities.isNotEmpty()) {
                localDataSource.insertAllocations(allocationEntities)
            }

            // Standardized Sync Push
            updateRemoteBudget(budget = budget)
        }
    }

    override suspend fun deleteBudget(budget: Budget): Result<Unit, DatabaseCallError> {
        return safeDatabaseCall {
            localDataSource.deleteBudget(budget.toBudgetEntity())
        }
    }

    override suspend fun deleteAllBudgets(): Result<Unit, DatabaseCallError> {
        return safeDatabaseCall {
            localDataSource.deleteAllBudgets()
        }
    }

    override suspend fun archiveBudget(budgetId: String) {
        val history = BudgetHistoryEntity(budgetId = budgetId, archivedTimestamp = System.currentTimeMillis())
        localDataSource.archiveBudget(history)
    }

    override fun getArchivedBudgets(): Flow<List<Budget>> {
        return localDataSource.getArchivedBudgets().map { list ->
            list.map { it.toBudget() }
        }
    }


    override suspend fun syncBudgets(budgets: List<Budget>): Result<Unit, DatabaseCallError> {
        return safeDatabaseCall {
            budgets.forEach { budget ->
                val existing = localDataSource.getBudgetById(budget.id)

                if (existing == null) {
                    insertBudget(budget)
                }
            }
        }
    }
    private suspend fun executeRemoteSync(
        budget: Budget,
        remoteCall: suspend (String, BudgetDto) -> Result<CommonResponseDto, NetworkError>
    ): Result<CommonResponse, NetworkError> {
        return withContext(defaultDispatcher){
            if (budget.isSynced) return@withContext Result.Success(CommonResponse("success", "Already synced"))

            val token = userPreferences.accessToken.first()
            if (!userPreferences.isGuestUser.first() && token != null) {
                val dtoResult = remoteCall(token, budget.toBudgetDto())
                if (dtoResult is Result.Success) {
                    localDataSource.updateBudget(budget.toBudgetEntity().copy(isSynced = true))
                }
                dtoResult.map { it.toCommonResponse() }
            } else {
                Result.Success(CommonResponse("success", "Offline saved"))
            }
        }
    }

    override suspend fun syncRemoteBudget(budget: Budget) = executeRemoteSync(budget, remoteDataSource::createBudget)
    override suspend fun updateRemoteBudget(budget: Budget) = executeRemoteSync(budget, remoteDataSource::updateBudget)



}
