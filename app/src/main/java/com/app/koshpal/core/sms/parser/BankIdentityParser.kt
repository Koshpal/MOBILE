package com.app.koshpal.core.sms.parser

import com.app.koshpal.core.sms.model.ParsedTransaction
import com.app.koshpal.core.sms.model.SmsMessage

interface BankIdentityParser {
    fun isParsable(sms: SmsMessage): Boolean
    fun parse(sms: SmsMessage): ParsedTransaction?
}