package com.app.koshpal.app.domain.model

data class CategoryBundle(
    val parentCategory: Category,
    val subCategories: List<Category>
)