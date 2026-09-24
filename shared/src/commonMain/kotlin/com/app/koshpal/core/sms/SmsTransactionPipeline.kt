package com.app.koshpal.core.sms

import com.app.koshpal.app.domain.repository.TransactionsRepo
import com.app.koshpal.core.sms.dedup.DuplicateDetector
import com.app.koshpal.core.sms.filter.SmsFilter
import com.app.koshpal.core.sms.model.ParsedTransaction
import com.app.koshpal.core.sms.model.SmsMessage
import com.app.koshpal.core.sms.parser.TransactionSmsParser
import com.app.koshpal.core.sms.reader.SmsReader
import com.app.koshpal.core.sms.validate.TransactionValidator

class SmsTransactionPipeline(
    private val smsReader: SmsReader,
    private val smsFilter: SmsFilter,
    private val transactionParser: TransactionSmsParser,
    private val validator: TransactionValidator,
    private val duplicateDetector: DuplicateDetector,
    private val transactionsRepo: TransactionsRepo,
) {

    suspend fun processMessages(
        messages: List<SmsMessage>,
        existingTransactions: List<ParsedTransaction> = emptyList()
    ): List<ParsedTransaction> {
        val candidates = smsFilter.filter(messages)
        val parsed = transactionParser.parseAll(candidates)
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

    suspend fun run(existingTransactions: List<ParsedTransaction> = emptyList()): List<ParsedTransaction> {
        val allMessages = smsReader.readInboxMessages()
        return processMessages(allMessages, existingTransactions)
    }
}
