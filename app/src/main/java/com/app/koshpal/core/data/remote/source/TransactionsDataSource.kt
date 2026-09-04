package com.app.koshpal.core.data.remote.source

import com.app.koshpal.core.data.remote.dto.CommonResponseDto
import com.app.koshpal.core.data.remote.dto.TransactionsDto
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result

interface TransactionsDataSource {
    suspend fun createTransactions(accessToken: String, transaction: TransactionsDto): Result<CommonResponseDto, NetworkError>
    suspend fun getTransactions(accessToken: String): Result<TransactionsDto, NetworkError>
    suspend fun deleteTransactions(accessToken: String, transaction: TransactionsDto): Result<CommonResponseDto, NetworkError>
}