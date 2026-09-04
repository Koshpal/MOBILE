package com.app.koshpal.app.domain.usecase.budgetusecase

import com.app.koshpal.app.data.mapper.toLocalBudget
import com.app.koshpal.app.data.UserPreferences
import com.app.koshpal.app.domain.repository.BudgetRepo
import com.app.koshpal.core.data.remote.source.BudgetDataSource
import com.app.koshpal.core.domain.util.CallError
import com.app.koshpal.core.domain.util.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class SyncBudgetsUseCase(
    private val budgetRepo: BudgetRepo,
    private val budgetDataSource: BudgetDataSource,
    private val userPreferences: UserPreferences,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend operator fun invoke(): Result<Int, CallError> {
        return withContext(defaultDispatcher) {
            if (userPreferences.isGuestUser.first()) return@withContext Result.Success(0)
            val token = userPreferences.accessToken.first() ?: return@withContext Result.Success(0)

            val localBudgets = budgetRepo.getAllBudgets().first()
            if (localBudgets.isNotEmpty() && localBudgets.all { it.isSynced }) {
                return@withContext Result.Success(localBudgets.size, "Budgets already synced")
            }

            return@withContext when (val apiResult = budgetDataSource.getBudgets(token)) {
                is Result.Success -> {
                    val domainBudgets = apiResult.data.map { it.toLocalBudget() }
                    budgetRepo.syncBudgets(domainBudgets)
                    Result.Success(domainBudgets.size, "Synced ${domainBudgets.size} budgets")
                }

                is Result.Error -> {
                    Result.Error(apiResult.error, apiResult.message)
                }

                else -> Result.Success(0)
            }
        }
    }
}
