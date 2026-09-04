package com.app.koshpal.core.data.remote.dto

import com.app.koshpal.core.data.entities.enums.TransactionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    val accountId: String? = null,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val subCategory: String,
    @SerialName("origin")
    val origin: String,
    val mode: String? = null,
    val description: String,
    val notes: String? = null,
    val transactionDate: String,
    val senderName: String? = null,
    val receiverName: String? = null,
    val bank: String,
    val maskedAccountNo: String,
    val provider: String,
    val isBookmarked: Boolean = false,
    val isCash: Boolean = false,
    val hasReceipt: Boolean = false,
    val isExcludedFromCashFlow: Boolean = false,
    val isSynced: Boolean = false,
)
