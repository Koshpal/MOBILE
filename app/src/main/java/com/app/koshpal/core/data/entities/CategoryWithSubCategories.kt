package com.app.koshpal.core.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithSubCategories(
    @Embedded val parentCategory: CategoryEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "parentCategoryId"
    )
    val subCategories: List<CategoryEntity>
)