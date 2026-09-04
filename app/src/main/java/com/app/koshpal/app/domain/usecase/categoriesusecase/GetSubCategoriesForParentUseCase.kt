package com.app.koshpal.app.domain.usecase.categoriesusecase

import com.app.koshpal.app.domain.model.Category
import com.app.koshpal.app.domain.repository.CategoryRepo
import kotlinx.coroutines.flow.Flow

class GetSubCategoriesForParentUseCase(
    private val repository: CategoryRepo
) {
    operator fun invoke(parentId: String): Flow<List<Category>> {
        return repository.getSubCategoriesForParent(parentId)
    }
}