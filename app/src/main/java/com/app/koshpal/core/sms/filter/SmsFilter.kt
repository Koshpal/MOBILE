package com.app.koshpal.core.sms.filter

import com.app.koshpal.core.sms.model.SmsMessage

interface SmsFilter {
    fun filter(messages: List<SmsMessage>): List<SmsMessage>
}