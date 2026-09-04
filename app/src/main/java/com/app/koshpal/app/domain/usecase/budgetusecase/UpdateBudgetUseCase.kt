package com.app.koshpal.app.domain.usecase.budgetusecase

import com.app.koshpal.app.domain.model.Budget
import com.app.koshpal.app.domain.repository.BudgetRepo
import com.app.koshpal.core.domain.util.DatabaseCallError
import com.app.koshpal.core.domain.util.Result

class UpdateBudgetUseCase(
    private val repository: BudgetRepo
) {
    suspend operator fun invoke(budget: Budget): Result<Unit, DatabaseCallError> {
        if (budget.amount <= 0) {
            throw IllegalArgumentException("Updated budget amount must be greater than zero")
        }
        return repository.updateBudget(budget)
    }
}