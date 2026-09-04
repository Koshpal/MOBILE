package com.app.koshpal.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CategoryWithAmountDto(
    val id: String,
    val name: String,
    val iconResId: String?,
    val colorHex: String,
    val parentCategoryId: String? = null,
    val allottedAmount: Double
)