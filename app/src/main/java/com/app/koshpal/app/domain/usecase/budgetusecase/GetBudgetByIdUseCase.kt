package com.app.koshpal.app.domain.usecase.budgetusecase

import com.app.koshpal.app.domain.model.Budget
import com.app.koshpal.app.domain.repository.BudgetRepo
import com.app.koshpal.core.domain.util.DatabaseCallError
import com.app.koshpal.core.domain.util.Result

class GetBudgetByIdUseCase(
    private val repository: BudgetRepo
){
    suspend operator fun invoke(id: String): Result<Budget?, DatabaseCallError> {
        return repository.getBudgetById(id)
    }
}