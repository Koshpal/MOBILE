package com.app.koshpal.app.data.repository

import com.app.koshpal.app.data.mapper.toCategory
import com.app.koshpal.app.data.mapper.toCategoryEntity
import com.app.koshpal.app.domain.model.Category
import com.app.koshpal.app.domain.model.CategoryBundle
import com.app.koshpal.app.domain.repository.CategoryRepo
import com.app.koshpal.core.data.local.source.CategoryLocalDataSource
import com.app.koshpal.core.data.networking.safeDatabaseCall
import com.app.koshpal.core.domain.util.DatabaseCallError
import kotlinx.coroutines.flow.Flow
import com.app.koshpal.core.domain.util.Result
import kotlinx.coroutines.flow.map

class CategoryRepoImpl(
    private val localDataSource: CategoryLocalDataSource
) : CategoryRepo {

    override fun getAllCategories(): Flow<List<Category>> {
        return localDataSource.getAllCategories().map { entities ->
            entities.map { it.toCategory() }
        }
    }

    override fun getMainCategories(): Flow<List<Category>> {
        return localDataSource.getMainCategories().map { entities ->
            entities.map { it.toCategory() }
        }
    }

    override fun getSubCategoriesForParent(parentId: String): Flow<List<Category>> {
        return localDataSource.getSubCategoriesForParent(parentId).map { entities ->
            entities.map { it.toCategory() }
        }
    }

    override fun getAllCategoriesWithSubCategories(): Flow<List<CategoryBundle>> {
        return localDataSource.getAllCategoriesWithSubCategories().map { bundles ->
            bundles.map { bundle ->
                CategoryBundle(
                    parentCategory = bundle.parentCategory.toCategory(),
                    subCategories = bundle.subCategories.map { it.toCategory() }
                )
            }
        }
    }

    override suspend fun getCategoryById(id: String): Result<Category?, DatabaseCallError> {
        return safeDatabaseCall {
            localDataSource.getCategoryById(id)?.toCategory()
        }
    }

    override suspend fun createCategory(category: Category): Result<Unit, DatabaseCallError> {
        return safeDatabaseCall {
            localDataSource.insertCategory(category.toCategoryEntity())
        }
    }

    override suspend fun updateCategory(category: Category): Result<Unit, DatabaseCallError> {
        return safeDatabaseCall {
            localDataSource.updateCategory(category.toCategoryEntity())
        }
    }

    override suspend fun deleteCategory(category: Category): Result<Unit, DatabaseCallError> {
        return safeDatabaseCall {
            localDataSource.deleteCategory(category.toCategoryEntity())
        }
    }

    override suspend fun deleteCategoryById(id: String): Result<Unit, DatabaseCallError> {
        return safeDatabaseCall {
            localDataSource.deleteCategoryById(id)
        }
    }

    override suspend fun deleteAllCategories(): Result<Unit, DatabaseCallError> {
        return safeDatabaseCall {
            localDataSource.deleteAllCategories()
        }
    }

    override suspend fun deleteOrphanedCategories(): Result<Unit, DatabaseCallError> {
        return safeDatabaseCall {
            localDataSource.deleteOrphanedCategories()
        }
    }
}