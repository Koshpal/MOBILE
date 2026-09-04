package com.app.koshpal.core.sms.reader

import android.content.Context
import android.provider.Telephony
import com.app.koshpal.core.sms.model.SmsMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber


class SmsReaderImpl(
    private val context: Context
) : SmsReader {

    override suspend fun readInboxMessages(): List<SmsMessage> =
        withContext(Dispatchers.IO) {
            val messages = mutableListOf<SmsMessage>()

            val projection = arrayOf(
                Telephony.Sms._ID,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY,
                Telephony.Sms.DATE
            )

            val cursor = context.contentResolver.query(
                Telephony.Sms.Inbox.CONTENT_URI,
                projection,
                null,
                null,
                "${Telephony.Sms.DATE} DESC"
            )

            cursor?.use {
                val idIndex = it.getColumnIndexOrThrow(Telephony.Sms._ID)
                val addressIndex = it.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
                val bodyIndex = it.getColumnIndexOrThrow(Telephony.Sms.BODY)
                val dateIndex = it.getColumnIndexOrThrow(Telephony.Sms.DATE)

                while (it.moveToNext()) {
                    val sender = it.getString(addressIndex)
                    val body = it.getString(bodyIndex)

                    if (sender.isNullOrBlank() || body.isNullOrBlank()) continue

                    messages.add(
                        SmsMessage(
                            id = it.getLong(idIndex),
                            sender = sender,
                            body = body,
                            timestamp = it.getLong(dateIndex)
                        )
                    )
                }
            }

            Timber.d("SmsReader: read ${messages.size} inbox messages")
            messages
        }
}
