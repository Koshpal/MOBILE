package com.app.koshpal.app.domain.usecase.dueusecase

import com.app.koshpal.app.domain.model.Due
import com.app.koshpal.app.domain.repository.DueRepo
import kotlinx.coroutines.flow.Flow

class GetAllDuesUseCase(private val repository: DueRepo) {
    operator fun invoke(): Flow<List<Due>> = repository.getAllDues()
}
