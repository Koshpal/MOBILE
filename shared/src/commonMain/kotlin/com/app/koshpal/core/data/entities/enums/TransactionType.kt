package com.app.koshpal.core.data.entities.enums

import kotlinx.serialization.Serializable

@Serializable
enum class TransactionType {
    EXPENSE,
    INCOME,
    UNKNOWN
}