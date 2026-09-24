package com.app.koshpal.app.domain.usecase.transactionsusecase

import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result

class IosSmsTransactionSyncer : SmsTransactionSyncer {
    override suspend fun sync(): Result<Int, NetworkError> {
        return Result.Error(NetworkError.UNKNOWN, "Not supported on iOS")
    }
}
