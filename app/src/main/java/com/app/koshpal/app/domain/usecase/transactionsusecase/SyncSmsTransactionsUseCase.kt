package com.app.koshpal.app.domain.usecase.transactionsusecase

import com.app.koshpal.app.data.mapper.toParsedTransaction
import com.app.koshpal.app.data.mapper.toTransactions
import com.app.koshpal.app.domain.model.Transactions
import com.app.koshpal.app.domain.repository.TransactionsRepo
import com.app.koshpal.core.data.entities.enums.TransactionType
import com.app.koshpal.core.sms.SmsTransactionPipeline
import com.app.koshpal.core.sms.util.ContactResolver
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result
import kotlinx.coroutines.flow.first

class SyncSmsTransactionsUseCase(
    private val repository: TransactionsRepo,
    private val smsPipeline: SmsTransactionPipeline,
    private val contactResolver: ContactResolver,
) {
    suspend operator fun invoke(): Result<Int, NetworkError> {
        return try {
            val existingTransactions = repository.getAllLocalTransactions().first()
            val existingParsed = existingTransactions.transactions.map { it.toParsedTransaction() }

            val newParsed = smsPipeline.run(existingParsed)

            val newTransactions = newParsed.toTransactions().transactions.map {
                it.let { txn ->
                    val partyName = if (txn.type == TransactionType.INCOME) txn.senderName else txn.receiverName
                    val savedName = contactResolver.resolveContactName(partyName)
                    txn.copy(contactName = savedName)
                }
            }
            repository.saveLocalTransactions(Transactions(newTransactions))
            Result.Success(newTransactions.size, "Synced ${newTransactions.size} transactions")
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN, e.message)
        }
    }
}
