package com.app.koshpal.app.domain.usecase.transactionsusecase

import com.app.koshpal.app.data.mapper.toParsedTransaction
import com.app.koshpal.app.data.mapper.toTransaction
import com.app.koshpal.app.domain.model.Notification
import com.app.koshpal.app.domain.model.Transactions
import com.app.koshpal.app.domain.repository.TransactionsRepo
import com.app.koshpal.app.domain.usecase.notificationusecase.NotificationUseCases
import com.app.koshpal.core.data.entities.enums.NotificationType
import com.app.koshpal.core.data.entities.enums.TransactionType
import com.app.koshpal.core.notification.NotificationHelper
import com.app.koshpal.core.sms.dedup.DuplicateDetector
import com.app.koshpal.core.sms.filter.SmsFilter
import com.app.koshpal.core.sms.model.SmsMessage
import com.app.koshpal.core.sms.parser.TransactionSmsParser
import com.app.koshpal.core.sms.util.ContactResolver
import com.app.koshpal.core.sms.validate.TransactionValidator
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.util.UUID

class ProcessIncomingSmsUseCase(
    private val repository: TransactionsRepo,
    private val parser: TransactionSmsParser,
    private val validator: TransactionValidator,
    private val filter: SmsFilter,
    private val duplicateDetector: DuplicateDetector,
    private val notificationHelper: NotificationHelper,
    private val contactResolver: ContactResolver,
    private val notificationUseCases: NotificationUseCases,
) {
    suspend operator fun invoke(sms: SmsMessage) {
        Timber.tag("KoshpalSMS").d("Processing SMS from ${sms.sender}")
        if (filter.filter(listOf(sms)).isEmpty()) {
            Timber.tag("KoshpalSMS").d("Filtered out")
            return
        }

        val parsed = parser.parse(sms)
        if (parsed == null) {
            Timber.tag("KoshpalSMS").d("Parser failed")
            return
        }
        
        if (!validator.validate(parsed)) {
            Timber.tag("KoshpalSMS").d("Validator failed")
            return
        }

        val existingTransactions = repository.getAllLocalTransactions().first()
        val existingParsed = existingTransactions.transactions.map { it.toParsedTransaction() }
        
        val unique = duplicateDetector.removeDuplicates(existingParsed, listOf(parsed))
        if (unique.isEmpty()) {
            Timber.tag("KoshpalSMS").d("Duplicate detected")
            return
        }

        val transaction = unique.first().toTransaction().let {
            val partyName = if (it.type == TransactionType.INCOME) it.senderName else it.receiverName
            val savedName = contactResolver.resolveContactName(partyName)
            it.copy(contactName = savedName)
        }
        repository.saveLocalTransactions(Transactions(listOf(transaction)))
        val partyName = if (transaction.type == TransactionType.INCOME) transaction.senderName else transaction.receiverName
        val displayReceiver = transaction.contactName ?: partyName

        notificationUseCases.insertNotification(
            Notification(
                id = UUID.randomUUID().toString(),
                type = NotificationType.TRANSACTION_ALERT,
                title = "New Transaction",
                message = "Paid ₹${transaction.amount} to $displayReceiver. Tap to classify.",
                timestamp = System.currentTimeMillis(),
                featureId = transaction.id,
                iconResId = "account_balance"
            )
        )

        Timber.tag("KoshpalSMS").d("Showing notification for ${transaction.id}")
        notificationHelper.showClassifyNotification(
            transactionId = transaction.id,
            amount = transaction.amount,
            partyName = displayReceiver
        )
    }
}
