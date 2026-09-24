package com.app.koshpal.core.data.remote.dto

import com.app.koshpal.core.data.entities.enums.TransactionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    val id: String? = null,
    val accountId: String? = null,
    val amount: Double? = 0.0,
    val type: TransactionType? = TransactionType.UNKNOWN,
    val category: String? = "Uncategorized",
    val subCategory: String? = "",
    @SerialName("origin")
    val origin: String? = "MANUAL",
    val mode: String? = null,
    val description: String? = "",
    val notes: String? = null,
    val transactionDate: String? = null,
    val senderName: String? = null,
    val receiverName: String? = null,
    val bank: String? = "",
    val maskedAccountNo: String? = "XXXX0000",
    val provider: String? = "SMS",
    val isBookmarked: Boolean? = false,
    val isCash: Boolean? = false,
    val hasReceipt: Boolean? = false,
    val isExcludedFromCashFlow: Boolean? = false,
    val isSynced: Boolean? = false,
)
