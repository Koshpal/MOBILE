package com.app.koshpal.app.data.mapper

import com.app.koshpal.app.domain.model.Transaction
import com.app.koshpal.app.domain.model.Transactions
import com.app.koshpal.core.data.entities.TransactionEntity
import com.app.koshpal.core.data.entities.enums.TransactionType
import com.app.koshpal.core.data.entities.enums.toBankDisplayName
import com.app.koshpal.core.data.remote.dto.TransactionDto
import com.app.koshpal.core.data.remote.dto.TransactionsDto
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun Transactions.toTransactionsDto(): TransactionsDto {
    return TransactionsDto(
        transactions = transactions.map { it.toTransactionsDto() },
        page = page,
        hasMore = hasMore
    )
}

fun TransactionsDto.toLocalTransactions(): Transactions {
    return Transactions(
        transactions = transactions.map { it.toTransaction() },
        page = page,
        hasMore = hasMore
    )
}

@OptIn(ExperimentalUuidApi::class)
fun TransactionDto.toTransaction(): Transaction {
    val dateString = transactionDate ?: ""
    val millis = try {
        Instant.parse(dateString).toEpochMilliseconds()
    } catch (_: Exception) {
        try {
            val date = LocalDate.parse(dateString)
            Instant.parse("${date}T00:00:00Z").toEpochMilliseconds()
        } catch (_: Exception) {
            Clock.System.now().toEpochMilliseconds()
        }
    }
    val accountNoInt = (maskedAccountNo ?: "").takeLast(4).toIntOrNull() ?: 0

    return Transaction(
        id = id ?: Uuid.random().toString(),
        accountId = accountId ?: "",
        amount = amount ?: 0.0,
        type = type ?: TransactionType.EXPENSE,
        category = category ?: "Uncategorized",
        subCategory = subCategory ?: "",
        source = origin ?: "MANUAL",
        description = description ?: "",
        transactionDate = millis,
        senderName = senderName ?: "",
        receiverName = receiverName ?: "",
        bank = (bank ?: "").toBankDisplayName(),
        maskedAccountNo = accountNoInt,
        provider = provider ?: "SMS",
        isSynced = true,
        notes = notes,
        isBookmarked = isBookmarked ?: false,
        isCash = isCash ?: false,
        hasReceipt = hasReceipt ?: false,
        isExcludedFromCashFlow = isExcludedFromCashFlow ?: false,
        mode = mode,
    )
}

fun Transaction.toTransactionsDto(): TransactionDto {
    val dateStr = try {
        Instant.fromEpochMilliseconds(transactionDate).toString()
    } catch (_: Exception) {
        "2026-08-20T00:00:00.000Z"
    }
    val accountNoStr = if (maskedAccountNo == 0) "XXXX0000" else "XXXX" + maskedAccountNo.toString().padStart(4, '0')

    return TransactionDto(
        id = id.ifBlank { null },
        accountId = accountId.ifBlank { null },
        amount = amount,
        type = type,
        category = category ?: "Uncategorized",
        subCategory = subCategory ?: "",
        origin = source.ifBlank { "MANUAL" },
        mode = mode,
        description = description ?: "",
        notes = notes,
        transactionDate = dateStr,
        senderName = senderName.ifBlank { null },
        receiverName = receiverName.ifBlank { null },
        bank = bank,
        maskedAccountNo = accountNoStr,
        provider = provider?.ifBlank { "SMS" } ?: "SMS",
        isBookmarked = isBookmarked,
        isCash = isCash,
        hasReceipt = hasReceipt,
        isExcludedFromCashFlow = isExcludedFromCashFlow,
        isSynced = isSynced,
    )
}

fun List<TransactionEntity>.toUiTransactions(): Transactions {
    return Transactions(
        transactions = this.map { it.toTransaction() },
    )
}

fun TransactionEntity.toTransaction(): Transaction {
    return Transaction(
        id = id,
        accountId = accountId,
        amount = amount,
        type = type,
        category = category,
        subCategory = subCategory,
        source = source,
        description = description,
        transactionDate = transactionDate,
        senderName = senderName,
        receiverName = receiverName,
        bank = bank.toBankDisplayName(),
        maskedAccountNo = maskedAccountNo,
        provider = provider,
        isSynced = isSynced,
        budgetId = budgetId,
        categoryId = categoryId,
        tagIds = tagIds,
        referenceNumber = referenceNumber,
        contactName = contactName,
        notes = notes,
        isBookmarked = isBookmarked,
        isCash = isCash,
        hasReceipt = hasReceipt,
        isExcludedFromCashFlow = isExcludedFromCashFlow,
        mode = mode,
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        accountId = accountId,
        amount = amount,
        type = type,
        category = category ?: "Uncategorized",
        subCategory = subCategory ?: "",
        source = source,
        description = description ?: "",
        transactionDate = transactionDate,
        senderName = senderName,
        receiverName = receiverName,
        bank = bank,
        maskedAccountNo = maskedAccountNo,
        provider = provider ?: "SMS",
        isSynced = isSynced,
        budgetId = budgetId,
        categoryId = categoryId,
        tagIds = tagIds,
        referenceNumber = referenceNumber,
        contactName = contactName,
        notes = notes,
        isBookmarked = isBookmarked,
        isCash = isCash,
        hasReceipt = hasReceipt,
        isExcludedFromCashFlow = isExcludedFromCashFlow,
        mode = mode,
    )
}
