package com.app.koshpal.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CategoryWithAmountDto(
    val id: String? = null,
    val name: String? = "",
    val iconResId: String? = null,
    val colorHex: String? = null,
    val parentCategoryId: String? = null,
    val allottedAmount: Double? = 0.0
)
