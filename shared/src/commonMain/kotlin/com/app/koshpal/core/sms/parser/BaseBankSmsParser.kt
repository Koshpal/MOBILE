package com.app.koshpal.core.sms.parser

import com.app.koshpal.core.data.entities.enums.Bank
import com.app.koshpal.core.data.entities.enums.TransactionType
import com.app.koshpal.core.sms.model.ParsedTransaction
import com.app.koshpal.core.sms.model.SmsMessage
import com.app.koshpal.core.sms.parser.util.RegexPatterns


abstract class BaseBankSmsParser : BankIdentityParser {

    protected abstract val bank: Bank
    protected abstract val senderPattern: Regex
    protected open val displayNameAliases: List<String> = emptyList()

    final override fun isParsable(sms: SmsMessage): Boolean =
        senderPattern.matches(sms.sender) ||
                displayNameAliases.any { it.equals(sms.sender.trim(), ignoreCase = true) }

    final override fun parse(sms: SmsMessage): ParsedTransaction? {
        val amount = extractAmount(sms.body) ?: return null
        val type = detectTransactionType(sms.body)

        if (type == TransactionType.UNKNOWN) return null

        val (sender, receiver) = extractParties(sms.body, type)

        val isVpa = sender?.contains("@") ?: false

        return ParsedTransaction(
            amount = amount,
            type = type,
            bank = bank,
            senderName = sender ?: "",
            receiverName = receiver ?: "",
            accountNumber = extractAccountNumber(sms.body),
            referenceNumber = extractReferenceNumber(sms.body),
            timestamp = sms.timestamp,
            mode = detectMode(sms.body),
            isVpaAnchored = isVpa
        )
    }

    protected open fun detectMode(body: String): String {
        val lower = body.lowercase()
        return when {
            lower.contains("upi") || lower.contains("vpa") || lower.contains("unified payment") -> "UPI"
            lower.contains("atm") || lower.contains("cash wdl") || lower.contains("cash withdrawal") -> "ATM Withdrawal"
            lower.contains("imps") -> "IMPS"
            lower.contains("neft") -> "NEFT"
            lower.contains("rtgs") -> "RTGS"
            lower.contains("card") || lower.contains("ending in") || lower.contains("spent on") || lower.contains("pos ") -> "Card"
            lower.contains("netbanking") || lower.contains("inb") || lower.contains("transfer") || lower.contains("trfr") -> "Netbanking"
            else -> "Other"
        }
    }


    protected open fun extractAmount(body: String): Double? =
        RegexPatterns.extractTransactionAmount(body)

    protected open fun extractParties(body: String, type: TransactionType): Pair<String?, String?> {
        var sender: String?
        var receiver: String?

        if (type == TransactionType.INCOME) {
            receiver = "Me"
            sender = RegexPatterns.BY_VPA.find(body)?.groupValues?.get(1) 
                ?: RegexPatterns.FROM_VPA.find(body)?.groupValues?.get(1)

            if (sender == null) {
                sender = RegexPatterns.PARTY_CONTEXTUAL.findAll(body)
                    .filter { it.value.lowercase().startsWith("from") || it.value.lowercase().startsWith("by") }
                    .map { it.groupValues[1] }
                    .firstOrNull()
            }
        } else {
            sender = "Me"
            val matches = RegexPatterns.PARTY_CONTEXTUAL.findAll(body)
            receiver = matches.find { it.value.lowercase().startsWith("to") }?.groupValues?.get(1)
                ?: matches.firstOrNull()?.groupValues?.get(1)
        }

        return Pair(sender, receiver)
    }



    protected open fun extractAccountNumber(body: String): String? =
        RegexPatterns.extractAccountSuffix(body)

    protected open fun extractReferenceNumber(body: String): String? =
        RegexPatterns.extractReferenceNumber(body)

    protected open fun detectTransactionType(body: String): TransactionType {
        val lower = body.lowercase()
        
        if (lower.contains("is credited") || lower.contains("has been credited")) return TransactionType.INCOME
        if (lower.contains("is debited") || lower.contains("has been debited")) return TransactionType.EXPENSE

        return when {
            DEBIT_KEYWORDS.any { it in lower } -> TransactionType.EXPENSE
            CREDIT_KEYWORDS.any { it in lower } -> TransactionType.INCOME
            else -> TransactionType.UNKNOWN
        }
    }

    companion object {
        private val DEBIT_KEYWORDS = listOf("debited", "withdrawn", "spent", "paid", "sent", "transferred")
        private val CREDIT_KEYWORDS = listOf("credited", "received", "deposited")
    }
}
