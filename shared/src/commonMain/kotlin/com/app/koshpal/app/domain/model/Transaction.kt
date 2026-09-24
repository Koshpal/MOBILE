package com.app.koshpal.app.domain.model

import com.app.koshpal.core.data.entities.enums.TransactionType
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Transaction(
    val id: String = Uuid.random().toString(),
    val accountId: String = "",
    val amount: Double = 0.0,
    val type: TransactionType = TransactionType.EXPENSE,
    val category: String? = "Uncategorized",
    val subCategory: String? = "",
    val source: String = "MANUAL",
    val description: String? = "",
    val transactionDate: Long = 0L,
    val senderName: String = "",
    val receiverName: String = "",
    val bank: String = "",
    val maskedAccountNo: Int = 0,
    val provider: String? = "SMS",
    val isSynced: Boolean = false,
    val budgetId: String? = null,
    val categoryId: String? = null,
    val tagIds: List<String> = emptyList(),
    val referenceNumber: String? = null,
    val contactName: String? = null,
    val notes: String? = null,
    val isBookmarked: Boolean = false,
    val isCash: Boolean = false,
    val hasReceipt: Boolean = false,
    val isExcludedFromCashFlow: Boolean = false,
    val mode: String? = null,
)

fun Transaction.resolveClassificationName(
    categoryNameFromBudget: String?,
    tagName: String?
): String {
    if (!categoryNameFromBudget.isNullOrBlank() && !categoryNameFromBudget.equals("Uncategorized", ignoreCase = true)) {
        return categoryNameFromBudget
    }
    if (!subCategory.isNullOrBlank() && !subCategory.equals("Uncategorized", ignoreCase = true)) {
        return subCategory
    }
    if (!category.isNullOrBlank() && !category.equals("Uncategorized", ignoreCase = true)) {
        return category
    }
    if (!tagName.isNullOrBlank()) {
        return tagName
    }
    return "UNCATEGORIZED"
}
