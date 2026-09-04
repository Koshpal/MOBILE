package com.app.koshpal.app.domain.usecase.dueusecase

import com.app.koshpal.app.domain.model.Due
import com.app.koshpal.app.domain.repository.DueRepo

class DeleteDueUseCase(private val repository: DueRepo) {
    suspend operator fun invoke(due: Due) = repository.deleteDue(due)
}
