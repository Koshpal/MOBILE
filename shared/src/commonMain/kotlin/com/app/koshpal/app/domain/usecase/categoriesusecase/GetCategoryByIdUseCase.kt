package com.app.koshpal.app.domain.usecase.categoriesusecase

import com.app.koshpal.app.domain.model.Category
import com.app.koshpal.app.domain.repository.CategoryRepo
import com.app.koshpal.core.domain.util.DatabaseCallError
import com.app.koshpal.core.domain.util.Result


class GetCategoryByIdUseCase(
    private val repository: CategoryRepo
) {
    suspend operator fun invoke(id: String): Result<Category?, DatabaseCallError> {
        return repository.getCategoryById(id)
    }
}