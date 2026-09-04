package com.app.koshpal.core.sms.validate

import com.app.koshpal.core.sms.model.ParsedTransaction

interface TransactionValidator {
    fun validate(transaction: ParsedTransaction): Boolean
}