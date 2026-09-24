package com.app.koshpal.core.sms.parser

import com.app.koshpal.core.sms.model.ParsedTransaction
import com.app.koshpal.core.sms.model.SmsMessage
import com.app.koshpal.core.util.logDebug

class TransactionSmsParser(
    private val parsers: List<BankIdentityParser>
) {

    fun parse(sms: SmsMessage): ParsedTransaction? {
        val matchedParser = parsers.firstOrNull { it.isParsable(sms) }

        if (matchedParser == null) {
            logDebug("KoshpalSMS: No parser claimed sender: ${sms.sender}")
            return null
        }

        logDebug("KoshpalSMS: ${matchedParser::class.simpleName} matched sender: ${sms.sender}")
        val result = matchedParser.parse(sms)

        if (result == null) {
            logDebug("KoshpalSMS: Parser ${matchedParser::class.simpleName} claimed sender but failed extraction: ${sms.body}")
        } else {
            logDebug("KoshpalSMS: Successfully parsed: $result")
        }

        return result
    }

    fun parseAll(messages: List<SmsMessage>): List<ParsedTransaction> =
        messages.mapNotNull { parse(it) }
}
