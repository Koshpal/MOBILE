package com.app.koshpal.app.domain.usecase.transactionsusecase

import com.app.koshpal.app.domain.model.Transactions
import com.app.koshpal.app.domain.repository.TransactionsRepo
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result

class GetTransactionUseCase(
    private val transactionsRepo: TransactionsRepo
) {
    suspend operator fun invoke(token: String, page: Int = 1, limit: Int = 50): Result<Transactions, NetworkError> {
        return transactionsRepo.getTransactions(accessToken = token, page = page, limit = limit)
    }
}
