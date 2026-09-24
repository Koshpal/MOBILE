package com.app.koshpal.app.domain.model

data class SelectedOptions(
    val title: String,
    val icon: Int,
    val action: () -> Unit
)
