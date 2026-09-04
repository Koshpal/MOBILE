package com.app.koshpal.app.domain.usecase.budgetusecase

import com.app.koshpal.app.domain.repository.BudgetRepo

class ArchiveBudgetUseCase(private val repository: BudgetRepo) {
    suspend operator fun invoke(budgetId: String) = repository.archiveBudget(budgetId)
}