package com.app.koshpal.app.domain.usecase.categoriesusecase

import com.app.koshpal.app.domain.model.Category
import com.app.koshpal.app.domain.repository.CategoryRepo
import kotlinx.coroutines.flow.Flow


class GetMainCategoriesUseCase(
    private val repository: CategoryRepo
) {
    operator fun invoke(): Flow<List<Category>> {
        return repository.getMainCategories()
    }
}