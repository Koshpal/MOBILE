package com.app.koshpal.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.koshpal.core.data.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.Transaction
import com.app.koshpal.core.data.entities.CategoryWithSubCategories

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    suspend fun getCategoryById(id: String): CategoryEntity?

    @Query("DELETE FROM categories")
    suspend fun deleteAllCategories()


    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategoryById(id: String)

    @Query("""
        DELETE FROM categories 
        WHERE id NOT IN (SELECT categoryId FROM budget_allocations)
        AND id NOT IN (
            SELECT parentCategoryId 
            FROM categories 
            WHERE id IN (SELECT categoryId FROM budget_allocations)
            AND parentCategoryId IS NOT NULL
        )
    """)
    suspend fun deleteOrphanedCategories()


    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>


    @Query("SELECT * FROM categories WHERE parentCategoryId IS NULL ORDER BY name ASC")
    fun getMainCategories(): Flow<List<CategoryEntity>>


    @Query("SELECT * FROM categories WHERE parentCategoryId = :parentId ORDER BY name ASC")
    fun getSubCategoriesForParent(parentId: String): Flow<List<CategoryEntity>>

    @Transaction
    @Query("SELECT * FROM categories WHERE parentCategoryId IS NULL ORDER BY name ASC")
    fun getAllCategoriesWithSubCategories(): Flow<List<CategoryWithSubCategories>>
}