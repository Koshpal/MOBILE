package com.app.koshpal.app.domain.usecase.dueusecase

import com.app.koshpal.app.domain.repository.DueRepo

class DeleteDuesByIdsUseCase(private val repository: DueRepo) {
    suspend operator fun invoke(ids: List<String>) = repository.deleteDuesByIds(ids)
}
