package com.app.koshpal.core.data.remote.source

import com.app.koshpal.core.data.networking.safeCall
import com.app.koshpal.core.data.networking.constructUrl
import com.app.koshpal.core.data.remote.dto.CommonResponseDto
import com.app.koshpal.core.data.remote.dto.TransactionsDto
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get


class RemoteTransactionsDataSource(
    val httpClient: HttpClient
): TransactionsDataSource {

    override suspend fun createTransactions(
        accessToken: String,
        transaction: TransactionsDto
    ): Result<CommonResponseDto, NetworkError>  {
        return safeCall<CommonResponseDto> {
            httpClient.post(
                urlString = constructUrl("/transactions/bulk")
            ) {
                contentType(ContentType.Application.Json)
                setBody(transaction)
                bearerAuth(accessToken)
            }
        }
    }

    override suspend fun getTransactions(accessToken: String): Result<TransactionsDto, NetworkError> {
        return safeCall<TransactionsDto> {
            httpClient.get(
                urlString = constructUrl("/transactions")
            ) {
                bearerAuth(accessToken)
            }
        }
    }

    override suspend fun deleteTransactions(accessToken: String, transaction: TransactionsDto): Result<CommonResponseDto, NetworkError> {
        return safeCall<CommonResponseDto> {
            httpClient.delete(
                urlString = constructUrl("/transactions")
            ) {
                contentType(ContentType.Application.Json)
                setBody(transaction)
                bearerAuth(accessToken)
            }
        }
    }

}