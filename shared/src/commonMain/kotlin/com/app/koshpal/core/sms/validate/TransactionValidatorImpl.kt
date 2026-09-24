package com.app.koshpal.core.sms.validate

import com.app.koshpal.core.data.entities.enums.TransactionType
import com.app.koshpal.core.sms.model.ParsedTransaction
import kotlinx.datetime.Clock

class TransactionValidatorImpl : TransactionValidator {

    override fun validate(transaction: ParsedTransaction): Boolean {
        val reason = findInvalidReason(transaction)
        if (reason != null) {
            return false
        }
        return true
    }

    private fun findInvalidReason(t: ParsedTransaction): String? {
        if (t.amount <= 0.0) return "amount must be positive, was ${t.amount}"

        if (t.amount > MAX_PLAUSIBLE_AMOUNT) return "amount implausibly large: ${t.amount}"

        if (t.type == TransactionType.UNKNOWN) return "transaction type unresolved"

        if (t.timestamp <= 0L) return "invalid timestamp"

        val now = Clock.System.now().toEpochMilliseconds()
        if (t.timestamp > now + FUTURE_TOLERANCE_MS) {
            return "timestamp is in the future: ${t.timestamp}"
        }

        if (t.timestamp < now - MAX_AGE_MS) {
            return "timestamp too old to be relevant: ${t.timestamp}"
        }

        return null
    }

    companion object {
        private const val MAX_PLAUSIBLE_AMOUNT = 20_00_000.0
        private const val FUTURE_TOLERANCE_MS = 3 * 60 * 1000L
        private const val MAX_AGE_MS = 730 * 24 * 60 * 60 * 1000L
    }
}
