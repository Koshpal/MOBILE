package com.app.koshpal.app.domain.usecase.categoriesusecase

import com.app.koshpal.app.domain.model.CategoryBundle
import com.app.koshpal.app.domain.repository.CategoryRepo
import kotlinx.coroutines.flow.Flow

class GetAllCategoriesWithSubCategoriesUseCase(
    private val repository: CategoryRepo
) {
    operator fun invoke(): Flow<List<CategoryBundle>> {
        return repository.getAllCategoriesWithSubCategories()
    }
}