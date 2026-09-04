package com.app.koshpal.core.sms

import com.app.koshpal.app.domain.repository.TransactionsRepo
import com.app.koshpal.core.sms.dedup.DuplicateDetector
import com.app.koshpal.core.sms.filter.SmsFilter
import com.app.koshpal.core.sms.model.ParsedTransaction
import com.app.koshpal.core.sms.parser.TransactionSmsParser
import com.app.koshpal.core.sms.reader.SmsReader
import com.app.koshpal.core.sms.validate.TransactionValidator
import timber.log.Timber

class SmsTransactionPipeline(
    private val smsReader: SmsReader,
    private val smsFilter: SmsFilter,
    private val transactionParser: TransactionSmsParser,
    private val validator: TransactionValidator,
    private val duplicateDetector: DuplicateDetector,
    private val transactionsRepo: TransactionsRepo,
) {

    suspend fun run(existingTransactions: List<ParsedTransaction> = emptyList()): List<ParsedTransaction> {
        val allMessages = smsReader.readInboxMessages()
        Timber.d("Pipeline: read ${allMessages.size} total inbox messages")

        val candidates = smsFilter.filter(allMessages)
        Timber.d("Pipeline: ${candidates.size} passed transactional filter")

        val parsed = transactionParser.parseAll(candidates)
        Timber.d("Pipeline: ${parsed.size} successfully parsed into transactions")

        val validTransactions = parsed.filter { validator.validate(it) }

        val resolvedTransactions = validTransactions.map { txn ->
            var updatedTxn = txn
            
            txn.senderName?.let { sender ->
                if (sender != "Me") {
                    transactionsRepo.getContactNameByIdentifier(sender)?.let { resolved ->
                        updatedTxn = updatedTxn.copy(senderName = resolved)
                    }
                }
            }
            
            txn.receiverName?.let { receiver ->
                transactionsRepo.getContactNameByIdentifier(receiver)?.let { resolved ->
                    updatedTxn = updatedTxn.copy(receiverName = resolved)
                }
            }
            
            updatedTxn
        }

        return duplicateDetector.removeDuplicates(existingTransactions, resolvedTransactions)
    }
}
