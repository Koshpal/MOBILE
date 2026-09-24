package com.app.koshpal.app.domain.usecase.budgetusecase

import com.app.koshpal.app.domain.repository.BudgetRepo
import com.app.koshpal.core.domain.util.DatabaseCallError
import com.app.koshpal.core.domain.util.Result

class DeleteAllBudgetUseCase(
    private val repository: BudgetRepo
) {
    suspend operator fun invoke(): Result<Unit, DatabaseCallError> {
        return repository.deleteAllBudgets()
    }
}