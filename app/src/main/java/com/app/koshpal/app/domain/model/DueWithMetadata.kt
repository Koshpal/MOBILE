package com.app.koshpal.app.domain.model

data class DueWithMetadata(
    val due: Due,
    val daysToGo: Long
)
