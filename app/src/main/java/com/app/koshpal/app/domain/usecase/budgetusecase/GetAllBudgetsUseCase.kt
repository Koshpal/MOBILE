package com.app.koshpal.app.domain.usecase.budgetusecase

import com.app.koshpal.app.domain.model.Budget
import com.app.koshpal.app.domain.repository.BudgetRepo
import kotlinx.coroutines.flow.Flow

class GetAllBudgetsUseCase(
    private val repository: BudgetRepo
) {
    operator fun invoke(): Flow<List<Budget>> {
        return repository.getAllBudgets()
    }
}
