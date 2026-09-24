package com.app.koshpal.core.sms

import com.app.koshpal.app.domain.usecase.transactionsusecase.SmsTransactionSyncer
import com.app.koshpal.app.domain.usecase.transactionsusecase.SyncSmsTransactionsUseCase
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result

class AndroidSmsTransactionSyncer(
    private val syncSmsTransactionsUseCase: SyncSmsTransactionsUseCase
) : SmsTransactionSyncer {
    override suspend fun sync(): Result<Int, NetworkError> {
        return syncSmsTransactionsUseCase()
    }
}
