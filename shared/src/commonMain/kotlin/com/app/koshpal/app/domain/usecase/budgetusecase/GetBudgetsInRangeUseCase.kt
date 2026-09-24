package com.app.koshpal.app.domain.usecase.budgetusecase

import com.app.koshpal.app.domain.model.Budget
import com.app.koshpal.app.domain.repository.BudgetRepo
import kotlinx.coroutines.flow.Flow

class GetBudgetsInRangeUseCase(
    private val repository: BudgetRepo
) {
    operator fun invoke(
        fromDate: String,
        toDate: String
    ): Flow<List<Budget>> {
        if (fromDate.isBlank() || toDate.isBlank()) {
            throw IllegalArgumentException("Start and end dates cannot be empty")
        }

        if (fromDate > toDate) {
            throw IllegalArgumentException("Start date cannot be later than end date")
        }

        return repository.getBudgetsInRange(fromDate, toDate)
    }
}