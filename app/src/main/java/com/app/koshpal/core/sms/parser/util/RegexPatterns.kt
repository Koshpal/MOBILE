package com.app.koshpal.core.sms.parser.util

object RegexPatterns {

    val AMOUNT = Regex(
        """(?:Rs\.?|INR|₹)\s?([0-9,]+(?:\.[0-9]{1,2})?)""",
        RegexOption.IGNORE_CASE,
    )

    val ACCOUNT_SUFFIX = Regex(
        """(?:a/?c|acct|account|card|ending)\s?(?:no\.?\s?)?\D{0,15}(?:x{2,}|x+|\*{2,}|\*+)?(\d{3,6})""",
        RegexOption.IGNORE_CASE
    )
    val REFERENCE_NUMBER = Regex(
        """(?:ref(?:erence)?\.?\s?(?:no\.?)?|rrn|txn\.?\s?id|utr|upi/|upi\s+ref|transaction\s+id|txn\s+ref)[:\s]*([A-Za-z0-9]{6,25})""",
        RegexOption.IGNORE_CASE
    )

    val VPA_PATTERN = Regex(
        """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+""",
        RegexOption.IGNORE_CASE
    )

    val PARTY_CONTEXTUAL = Regex(
        """(?<!linked )(?:to VPA|from|by|to|at)\s+(?!Rs\.?|INR|UPI|₹|[\d,.])([A-Za-z0-9._@\s\-]{3,50})(?=\s*\(|\s+via|\s+Ref|\.(?=\s|$)|\$|-|\s+on|\s+using|\s+at|\s+in|\s+is|\s+with|\s+account|\s+a/c|\s+towards|\s+trf|Avl|Bal|Balance|$)""",
        RegexOption.IGNORE_CASE
    )

    val YOUR_VPA = Regex(
        """(?:your VPA|ur VPA)\s+([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+)""",
        RegexOption.IGNORE_CASE
    )

    val FROM_VPA = Regex(
        """from\s+([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+|[0-9]{10}\s+[a-z]+)""",
        RegexOption.IGNORE_CASE
    )

    val BY_VPA = Regex(
        """by\s+([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+)""",
        RegexOption.IGNORE_CASE
    )

    val CREDIT_BY_AMOUNT = Regex("""credited by\s+(?:rs\.?|inr|₹)\s?[\d,.]+""", RegexOption.IGNORE_CASE)

    val TRANSACTION_AMOUNT = Regex(
        """(?:Rs\.?|INR|₹)\s*([0-9,]+(?:\.[0-9]{1,2})?)\s*(?:debited|credited|withdrawn|deposited|received|paid|sent|transferred|is credited|has been credited)|""" +
                """(?:debited|credited|withdrawn|deposited|received|paid|sent|transferred|is credited|has been credited)\s*(?:by|for|of|with)?\s*(?:Rs\.?|INR|₹)\s*([0-9,]+(?:\.[0-9]{1,2})?)|""" +
                """(?:Rs\.?|INR|₹)\s*([0-9,]+(?:\.[0-9]{1,2})?)\s+(?:is|was|has been|have been)\s+(?:debited|credited)|""" +
                """(?:debited|credited)\s+by\s+([0-9]+(?:\.[0-9]{1,2})?)\b""",
        RegexOption.IGNORE_CASE
    )

    fun extractAmount(text: String): Double? =
        AMOUNT.find(text)
            ?.groupValues?.get(1)
            ?.replace(",", "")
            ?.toDoubleOrNull()

    fun extractAccountSuffix(text: String): String? =
        ACCOUNT_SUFFIX.find(text)?.groupValues?.get(1)

    fun extractReferenceNumber(text: String): String? =
        REFERENCE_NUMBER.find(text)?.groupValues?.get(1)

    fun extractTransactionAmount(text: String): Double? {
        val match = TRANSACTION_AMOUNT.find(text) ?: return null
        val raw = match.groupValues[1].ifBlank { match.groupValues[2] }
            .ifBlank { match.groupValues[3] }.ifBlank { match.groupValues[4] }
        return raw.replace(",", "").toDoubleOrNull()
    }
}
