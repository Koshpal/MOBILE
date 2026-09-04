package com.app.koshpal.core.sms.filter

import com.app.koshpal.core.sms.model.SmsMessage
import timber.log.Timber

class SmsFilterImpl : SmsFilter {

    override fun filter(messages: List<SmsMessage>): List<SmsMessage> =
        messages.filter { isLikelyTransactional(it) }

    private fun isLikelyTransactional(sms: SmsMessage): Boolean {
        if (!hasBusinessSenderFormat(sms.sender)) {
            Timber.d("KoshpalSMS: Rejected: Invalid sender format (${sms.sender})")
            return false
        }

        val body = sms.body.lowercase()

        if (body.contains("http://") || body.contains("https://") || body.contains("www.") ||
            PHISHING_SHORTENERS.any { it in body }
        ) {
            Timber.d("KoshpalSMS: Rejected: Suspicious URL detected")
            return false
        }

        val matchedExclusion = EXCLUSION_KEYWORDS.find { it in body }
        if (matchedExclusion != null) {
            Timber.d("KoshpalSMS: Rejected: Fraud/Non-transactional keyword '$matchedExclusion' found")
            return false
        }

        if (isSuspiciousActionRequest(body)) {
            Timber.d("KoshpalSMS: Rejected: Potential phishing/fraud pattern detected")
            return false
        }

        val hasKeyword = TRANSACTION_KEYWORDS.any { it in body }
        if (!hasKeyword) {
            Timber.d("KoshpalSMS: Rejected: No transaction keywords in body")
        } else {
            Timber.d("KoshpalSMS: Accepted: Legitimate transaction identified")
        }
        
        return hasKeyword
    }

    private fun isSuspiciousActionRequest(body: String): Boolean {
        val hasVerify = body.contains("verify") || body.contains("update") || body.contains("confirm")
        val hasUrgency = body.contains("immediately") || body.contains("act now") || body.contains("urgent")
        val hasThreat = body.contains("blocked") || body.contains("suspended") || body.contains("closure") || body.contains("reversal")

        if (hasVerify && (hasUrgency || hasThreat)) return true

        val isMoneyBait = (body.contains("claim") || body.contains("receive")) &&
                (body.contains("money") || body.contains("reward") || body.contains("cashback"))
        return isMoneyBait && hasUrgency
    }

    private fun hasBusinessSenderFormat(sender: String): Boolean {
        if (sender.startsWith("+") || sender.all { it.isDigit() }) return false
        if (sender.length == 6 && sender.all { it.isLetter() }) return true
        if (DLT_SENDER_REGEX.matches(sender)) return true
        return RESOLVED_BANK_NAME_REGEX.matches(sender.trim())
    }

    companion object {
        private val DLT_SENDER_REGEX =
            Regex("^[A-Z]{2}-[A-Z0-9]{5,6}(-[A-Z])?$", RegexOption.IGNORE_CASE)
        private val RESOLVED_BANK_NAME_REGEX =
            Regex("""^[A-Za-z][A-Za-z\s]*\bBank\b[A-Za-z\s]*$""", RegexOption.IGNORE_CASE)

        private val PHISHING_SHORTENERS = listOf(
            "bit.ly", "tinyurl.com", "t.co", "m.me", "cutt.ly", "is.gd", "v.gd", "bit.do"
        )

        private val TRANSACTION_KEYWORDS = listOf(
            "debited", "credited", "debit", "credit",
            "withdrawn", "deposited", "transferred",
            "sent", "received", "paid",
            "a/c", "acct", "account",
            "upi", "imps", "neft", "rtgs",
            "txn", "transaction",
            "avl bal", "available balance", "avl limit",
            "rs.", "inr", "₹", "amt"
        )

        private val EXCLUSION_KEYWORDS = listOf(
            "otp", "one time password", "verification code",
            "activation", "passcode",
            "offer", "discount", "cashback offer", "sale",
            "loan approved", "pre-approved", "apply now",
            "recharge now", "click here", "download",
            "emi due", "bill due", "reminder", "maintain sufficient balance", "is due on",

            "upi pin", "mpin", "atm pin", "cvv", "netbanking password",
            "transaction pending", "verify account", "update kyc",
            "account will be blocked", "account suspended", "avoid reversal", "avoid closure"
        )
    }
}
