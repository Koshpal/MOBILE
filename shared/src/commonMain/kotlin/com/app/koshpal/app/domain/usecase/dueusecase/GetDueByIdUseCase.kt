package com.app.koshpal.app.domain.usecase.dueusecase

import com.app.koshpal.app.domain.repository.DueRepo

class GetDueByIdUseCase(private val repository: DueRepo) {
    suspend operator fun invoke(id: String) = repository.getDueById(id)
}
