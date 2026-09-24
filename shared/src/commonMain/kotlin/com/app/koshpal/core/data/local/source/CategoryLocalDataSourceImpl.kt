package com.app.koshpal.core.data.local.source


import com.app.koshpal.core.data.entities.CategoryEntity
import com.app.koshpal.core.data.entities.CategoryWithSubCategories
import com.app.koshpal.core.data.local.dao.CategoryDao
import kotlinx.coroutines.flow.Flow

class CategoryLocalDataSourceImpl(
    private val categoryDao: CategoryDao
) : CategoryLocalDataSource {

    override fun getAllCategories(): Flow<List<CategoryEntity>> =
        categoryDao.getAllCategories()

    override fun getMainCategories(): Flow<List<CategoryEntity>> =
        categoryDao.getMainCategories()

    override fun getSubCategoriesForParent(parentId: String): Flow<List<CategoryEntity>> =
        categoryDao.getSubCategoriesForParent(parentId)

    override fun getAllCategoriesWithSubCategories(): Flow<List<CategoryWithSubCategories>> =
        categoryDao.getAllCategoriesWithSubCategories()

    override suspend fun getCategoryById(id: String): CategoryEntity? =
        categoryDao.getCategoryById(id)

    override suspend fun insertCategory(category: CategoryEntity) =
         categoryDao.insertCategory(category)

    override suspend fun updateCategory(category: CategoryEntity) =
        categoryDao.updateCategory(category)

    override suspend fun deleteCategory(category: CategoryEntity) =
        categoryDao.deleteCategory(category)

    override suspend fun deleteCategoryById(id: String) =
        categoryDao.deleteCategoryById(id)

    override suspend fun deleteAllCategories() =
        categoryDao.deleteAllCategories()

    override suspend fun deleteOrphanedCategories() =
        categoryDao.deleteOrphanedCategories()
}