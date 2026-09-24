package com.app.koshpal.app.domain.usecase.budgetusecase

import com.app.koshpal.app.domain.repository.BudgetRepo

class GetArchivedBudgetsUseCase(private val repository: BudgetRepo) {
    operator fun invoke() = repository.getArchivedBudgets()
}