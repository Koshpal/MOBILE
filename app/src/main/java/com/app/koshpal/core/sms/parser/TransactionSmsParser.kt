package com.app.koshpal.core.sms.parser

import com.app.koshpal.core.sms.model.ParsedTransaction
import com.app.koshpal.core.sms.model.SmsMessage
import timber.log.Timber

class TransactionSmsParser(
    private val parsers: List<BankIdentityParser>
) {

    fun parse(sms: SmsMessage): ParsedTransaction? {
        val matchedParser = parsers.firstOrNull { it.isParsable(sms) }

        if (matchedParser == null) {
            Timber.tag("KoshpalSMS").d("No parser claimed sender: ${sms.sender}")
            return null
        }

        Timber.tag("KoshpalSMS")
            .d("${matchedParser::class.simpleName} matched sender: ${sms.sender}")
        val result = matchedParser.parse(sms)

        if (result == null) {
            Timber.tag("KoshpalSMS")
                .d("Parser ${matchedParser::class.simpleName} claimed sender but failed extraction: ${sms.body}")
        } else {
            Timber.tag("KoshpalSMS").d("Successfully parsed: $result")
        }

        return result
    }

    fun parseAll(messages: List<SmsMessage>): List<ParsedTransaction> =
        messages.mapNotNull { parse(it) }
}