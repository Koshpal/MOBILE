package com.app.koshpal.app.data.mapper

import com.app.koshpal.app.domain.model.Transaction
import com.app.koshpal.app.domain.model.Transactions
import com.app.koshpal.core.data.entities.TransactionEntity
import com.app.koshpal.core.data.entities.enums.Bank
import com.app.koshpal.core.data.remote.dto.TransactionDto
import com.app.koshpal.core.data.remote.dto.TransactionsDto
import com.app.koshpal.core.sms.model.ParsedTransaction
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

fun Transactions.toTransactionsDto(): TransactionsDto {
    return TransactionsDto(
        transactions = transactions.map { it.toTransactionsDto() },
    )
}

fun TransactionsDto.toLocalTransactions(): Transactions {
    return Transactions(
        transactions = transactions.map { it.toTransaction() },
    )
}

fun TransactionDto.toTransaction(): Transaction {
    val millis = try {
        Instant.parse(transactionDate).toEpochMilli()
    } catch (_: Exception) {
        try {
            LocalDate.parse(transactionDate, DateTimeFormatter.ISO_LOCAL_DATE)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        } catch (_: Exception) {
            System.currentTimeMillis()
        }
    }
    val accountNoInt = maskedAccountNo.takeLast(4).toIntOrNull() ?: 0

    return Transaction(
        accountId = accountId ?: "",
        amount = amount,
        type = type,
        category = category,
        subCategory = subCategory,
        source = origin,
        description = description,
        transactionDate = millis,
        senderName = senderName ?: "",
        receiverName = receiverName ?: "",
        bank = bank,
        maskedAccountNo = accountNoInt,
        provider = provider,
        isSynced = true,
        notes = notes,
        isBookmarked = isBookmarked,
        isCash = isCash,
        hasReceipt = hasReceipt,
        isExcludedFromCashFlow = isExcludedFromCashFlow,
        mode = mode,
    )
}

fun Transaction.toTransactionsDto(): TransactionDto {
    val dateStr = try {
        Instant.ofEpochMilli(transactionDate).toString()
    } catch (_: Exception) {
        "2026-08-20T00:00:00.000Z"
    }
    val accountNoStr = if (maskedAccountNo == 0) "XXXX0000" else "XXXX" + String.format(Locale.ENGLISH, "%04d", maskedAccountNo)

    return TransactionDto(
        accountId = accountId.ifBlank { null },
        amount = amount,
        type = type,
        category = category,
        subCategory = subCategory,
        origin = source.ifBlank { "MANUAL" },
        mode = mode,
        description = description,
        notes = notes,
        transactionDate = dateStr,
        senderName = senderName.ifBlank { null },
        receiverName = receiverName.ifBlank { null },
        bank = bank,
        maskedAccountNo = accountNoStr,
        provider = provider.ifBlank { "SMS" },
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
        bank = bank,
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
        category = category,
        subCategory = subCategory,
        source = source,
        description = description,
        transactionDate = transactionDate,
        senderName = senderName,
        receiverName = receiverName,
        bank = bank,
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

fun Transaction.toParsedTransaction(): ParsedTransaction {
    return ParsedTransaction(
        amount = amount,
        type = type,
        bank = try { Bank.valueOf(bank) } catch (_: Exception) { Bank.UNKNOWN },
        senderName = senderName,
        receiverName = receiverName,
        accountNumber = maskedAccountNo.toString(),
        referenceNumber = referenceNumber,
        timestamp = transactionDate,
        mode = mode,
    )
}

fun List<ParsedTransaction>.toTransactions(): Transactions {
    return Transactions(
        transactions = this.map { it.toTransaction() },
    )
}

fun ParsedTransaction.toTransaction(): Transaction {
    return Transaction(
        id = UUID.randomUUID().toString(),
        accountId = "",
        amount = amount,
        type = type,
        category = "Uncategorized",
        subCategory = "",
        source = "SMS",
        description = "Synced from SMS",
        transactionDate = timestamp,
        senderName = senderName,
        receiverName = receiverName,
        bank = bank.name,
        maskedAccountNo = accountNumber?.takeLast(4)?.toIntOrNull() ?: 0,
        provider = "SMS",
        referenceNumber = referenceNumber,
        contactName = null,
        notes = null,
        isBookmarked = false,
        isCash = false,
        hasReceipt = false,
        isExcludedFromCashFlow = false,
        mode = mode,
    )
}
