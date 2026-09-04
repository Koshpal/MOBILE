package com.app.koshpal.core.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.app.koshpal.app.domain.usecase.transactionsusecase.ProcessIncomingSmsUseCase
import com.app.koshpal.core.sms.model.SmsMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class SmsReceiver : BroadcastReceiver(), KoinComponent {

    private val processIncomingSmsUseCase: ProcessIncomingSmsUseCase by inject()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("KoshpalSMS", "onReceive: ${intent.action}")
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            if (messages.isEmpty()) return

            val pendingResult = goAsync()
            
            val body = StringBuilder()
            for (sms in messages) {
                body.append(sms.displayMessageBody)
            }
            
            val firstSms = messages[0]
            val smsMessage = SmsMessage(
                id = System.currentTimeMillis(),
                sender = firstSms.originatingAddress ?: "Unknown",
                body = body.toString(),
                timestamp = firstSms.timestampMillis
            )
            
            scope.launch {
                try {
                    processIncomingSmsUseCase(smsMessage)
                } catch (e: Exception) {
                    Timber.e(e, "Error processing incoming SMS")
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
