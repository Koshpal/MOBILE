package com.app.koshpal.app.domain.model

import com.app.koshpal.core.data.entities.enums.TransactionType
import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val accountId: String,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val subCategory: String,
    val source: String,
    val description: String,
    val transactionDate: Long,
    val senderName: String,
    val receiverName: String,
    val bank: String,
    val maskedAccountNo: Int,
    val provider: String,
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
    if (subCategory.isNotBlank() && !subCategory.equals("Uncategorized", ignoreCase = true)) {
        return subCategory
    }
    if (category.isNotBlank() && !category.equals("Uncategorized", ignoreCase = true)) {
        return category
    }
    if (!tagName.isNullOrBlank()) {
        return tagName
    }
    return "UNCATEGORIZED"
}
