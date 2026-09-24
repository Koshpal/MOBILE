package com.app.koshpal.app.domain.usecase.transactionsusecase

import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result

interface SmsTransactionSyncer {
    suspend fun sync(): Result<Int, NetworkError>
}
