package com.app.koshpal.app.data.mapper

import com.app.koshpal.app.domain.model.Transaction
import com.app.koshpal.app.domain.model.Transactions
import com.app.koshpal.core.data.entities.enums.Bank
import com.app.koshpal.core.sms.model.ParsedTransaction
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun Transaction.toParsedTransaction(): ParsedTransaction {
    return ParsedTransaction(
        amount = amount,
        type = type,
        bank = try {
            val normalizedBank = bank.replace(" ", "_").uppercase()
            Bank.valueOf(normalizedBank)
        } catch (_: Exception) {
            Bank.entries.find { it.toDisplayName().equals(bank, ignoreCase = true) } ?: Bank.UNKNOWN
        },
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

@OptIn(ExperimentalUuidApi::class)
fun ParsedTransaction.toTransaction(): Transaction {
    return Transaction(
        id = Uuid.random().toString(),
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
        bank = bank.toDisplayName(),
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
