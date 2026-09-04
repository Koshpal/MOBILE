package com.app.koshpal.app.domain.model

import com.app.koshpal.app.domain.model.Tag

data class HomeTagSummary(
    val tag: Tag,
    val transactionCount: Int
)
