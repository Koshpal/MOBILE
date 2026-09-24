package com.app.koshpal.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TransactionsDto (
    val transactions: List<TransactionDto>,
    val page: Int? = 1,
    val hasMore: Boolean? = false
)
