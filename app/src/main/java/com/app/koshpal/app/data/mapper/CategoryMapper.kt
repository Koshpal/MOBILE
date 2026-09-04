package com.app.koshpal.app.data.mapper

import com.app.koshpal.app.domain.model.Category
import com.app.koshpal.core.data.entities.CategoryEntity

fun CategoryEntity.toCategory(): Category {
    return Category(
        id = this.id,
        title = this.name.take(20),
        iconResId = this.iconResId,
        colorHex = this.colorHex,
        parentCategoryId = this.parentCategoryId,
        lastModifiedTimeStamp = this.lastModifiedTimeStamp,
    )
}

fun Category.toCategoryEntity(): CategoryEntity {
    return CategoryEntity(
        id = this.id,
        name = this.title.take(20),
        iconResId = this.iconResId,
        colorHex = this.colorHex,
        parentCategoryId = this.parentCategoryId,
        lastModifiedTimeStamp = this.lastModifiedTimeStamp,
    )
}
