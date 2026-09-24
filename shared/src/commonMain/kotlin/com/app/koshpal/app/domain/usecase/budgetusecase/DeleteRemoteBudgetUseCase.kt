package com.app.koshpal.app.domain.usecase.budgetusecase

import com.app.koshpal.app.domain.model.CommonResponse
import com.app.koshpal.app.domain.repository.BudgetRepo
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result

class DeleteRemoteBudgetUseCase(
    private val repository: BudgetRepo
) {
    suspend operator fun invoke(token: String, budgetId: String): Result<CommonResponse, NetworkError> {
        return repository.deleteRemoteBudget(token, budgetId)
    }
}
