package com.app.koshpal.app.domain.repository

import com.app.koshpal.app.domain.model.Category
import com.app.koshpal.app.domain.model.CategoryBundle
import com.app.koshpal.core.domain.util.DatabaseCallError
import kotlinx.coroutines.flow.Flow
import com.app.koshpal.core.domain.util.Result

interface CategoryRepo {
    fun getAllCategories(): Flow<List<Category>>
    fun getMainCategories(): Flow<List<Category>>
    fun getSubCategoriesForParent(parentId: String): Flow<List<Category>>
    fun getAllCategoriesWithSubCategories(): Flow<List<CategoryBundle>>
    suspend fun getCategoryById(id: String): Result<Category?, DatabaseCallError>

    suspend fun createCategory(category: Category): Result<Unit, DatabaseCallError>
    suspend fun updateCategory(category: Category): Result<Unit, DatabaseCallError>
    suspend fun deleteCategory(category: Category): Result<Unit, DatabaseCallError>
    suspend fun deleteCategoryById(id: String): Result<Unit, DatabaseCallError>
    suspend fun deleteAllCategories(): Result<Unit, DatabaseCallError>
    suspend fun deleteOrphanedCategories(): Result<Unit, DatabaseCallError>
}