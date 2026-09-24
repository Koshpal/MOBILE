package com.app.koshpal.app.domain.usecase.budgetusecase

import com.app.koshpal.app.domain.model.Budget
import com.app.koshpal.app.domain.repository.BudgetRepo
import kotlinx.coroutines.flow.Flow

class GetBudgetsUseCase(
    private val repository: BudgetRepo
) {
    operator fun invoke(
        year: Int,
        month: Int,
        isMonthly: Boolean
    ): Flow<List<Budget>> {
        return if (isMonthly) {
            repository.getBudgetsForMonth(month, year)
        } else {
            repository.getBudgetsForYearly(year)
        }
    }
}


