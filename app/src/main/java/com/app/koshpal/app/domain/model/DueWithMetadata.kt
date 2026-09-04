package com.app.koshpal.app.domain.model

import com.app.koshpal.app.domain.model.Due

data class DueWithMetadata(
    val due: Due,
    val daysToGo: Long
)
