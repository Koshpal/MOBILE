package com.app.koshpal.core.sms.model

data class SmsMessage(
    val id: Long,
    val sender: String,
    val body: String,
    val timestamp: Long
)