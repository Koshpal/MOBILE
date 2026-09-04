package com.app.koshpal.app.domain.usecase.transactionsusecase

import com.app.koshpal.app.domain.model.CommonResponse
import com.app.koshpal.app.domain.model.Transactions
import com.app.koshpal.app.domain.repository.TransactionsRepo
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result

class DeleteTransactionUseCase(
    private val transactionsRepo: TransactionsRepo
) {
    suspend operator fun invoke(accessToken: String, transaction: Transactions): Result<CommonResponse, NetworkError> {
        return transactionsRepo.deleteTransactions(accessToken = accessToken, transaction = transaction)
    }
}
