package com.app.koshpal.core.sms.model

import com.app.koshpal.core.data.entities.enums.Bank
import com.app.koshpal.core.data.entities.enums.TransactionType

data class ParsedTransaction(
    val amount: Double,
    val type: TransactionType,
    val bank: Bank,
    var senderName: String,
    var receiverName: String,
    val accountNumber: String?,
    val referenceNumber: String?,
    val timestamp: Long,
    val mode: String? = null,
    val isVpaAnchored: Boolean = false
) {
    init {
        senderName = sanitize(senderName)
        receiverName = sanitize(receiverName)
    }

    private fun sanitize(name: String): String {
        if (name == "") return ""
        val trimmed = name.trim()
        val lower = trimmed.lowercase()
        val blacklist = setOf("rs", "rs.", "inr", "upi", "ref", "total", "unknown", "cbol", "remitter")
        
        if (lower in blacklist || trimmed.all { !it.isLetterOrDigit() && it != ' ' }) return ""
        
        // Comprehensive Bank SMS cleaning
        return trimmed.split("@")[0]
            .trim()
            .replace(Regex("""^(?:VPA|A/c|Account)\s+""", RegexOption.IGNORE_CASE), "")
            .split(Regex("""\s+via\b""", RegexOption.IGNORE_CASE))[0]
            .trim()
            .removeSuffix(".")
            .removeSuffix("-")
    }
}
