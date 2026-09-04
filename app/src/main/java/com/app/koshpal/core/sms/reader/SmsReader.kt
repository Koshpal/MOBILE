package com.app.koshpal.core.sms.reader

import com.app.koshpal.core.sms.model.SmsMessage

interface SmsReader {
    suspend fun readInboxMessages(): List<SmsMessage>
}