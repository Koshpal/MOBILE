package com.app.koshpal.core.data.local.source

import com.app.koshpal.core.data.entities.CategoryEntity
import com.app.koshpal.core.data.entities.CategoryWithSubCategories
import kotlinx.coroutines.flow.Flow

interface CategoryLocalDataSource {
    fun getAllCategories(): Flow<List<CategoryEntity>>
    fun getMainCategories(): Flow<List<CategoryEntity>>
    fun getSubCategoriesForParent(parentId: String): Flow<List<CategoryEntity>>
    fun getAllCategoriesWithSubCategories(): Flow<List<CategoryWithSubCategories>>
    suspend fun getCategoryById(id: String): CategoryEntity?

    suspend fun insertCategory(category: CategoryEntity)
    suspend fun updateCategory(category: CategoryEntity)
    suspend fun deleteCategory(category: CategoryEntity)
    suspend fun deleteCategoryById(id: String)
    suspend fun deleteAllCategories()
    suspend fun deleteOrphanedCategories()
}