package com.app.koshpal.app.domain.usecase.budgetusecase

import com.app.koshpal.app.domain.model.Budget
import com.app.koshpal.app.domain.repository.BudgetRepo
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result

class GetRemoteBudgetsUseCase(
    private val budgetRepo: BudgetRepo
) {
    suspend operator fun invoke(token: String): Result<List<Budget>, NetworkError> {
        return budgetRepo.getRemoteBudgets(token)
    }
}
