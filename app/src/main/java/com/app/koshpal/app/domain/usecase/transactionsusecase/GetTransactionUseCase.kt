package com.app.koshpal.app.domain.usecase.transactionsusecase

import com.app.koshpal.app.domain.model.Transactions
import com.app.koshpal.app.domain.repository.TransactionsRepo
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result


class GetTransactionUseCase(
    private val transactionsRepo: TransactionsRepo
) {
    suspend operator fun invoke(accessToken: String): Result<Transactions, NetworkError> {
        return transactionsRepo.getTransactions(accessToken = accessToken)
    }
}
