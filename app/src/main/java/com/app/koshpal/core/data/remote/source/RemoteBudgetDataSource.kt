package com.app.koshpal.core.data.remote.source

import com.app.koshpal.core.data.networking.constructUrl
import com.app.koshpal.core.data.networking.safeCall
import com.app.koshpal.core.data.remote.dto.BudgetDto
import com.app.koshpal.core.data.remote.dto.CommonResponseDto
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class RemoteBudgetDataSource(
    private val httpClient: HttpClient
) : BudgetDataSource {
    override suspend fun getBudgets(accessToken: String): Result<List<BudgetDto>, NetworkError> {
        return safeCall<List<BudgetDto>> {
            httpClient.get(urlString = constructUrl("/budgets/")) {
                bearerAuth(accessToken)
            }
        }
    }

    override suspend fun createBudget(
        accessToken: String,
        budget: BudgetDto
    ): Result<CommonResponseDto, NetworkError> {
        return safeCall<CommonResponseDto> {
            httpClient.post(urlString = constructUrl("/budgets/")) {
                bearerAuth(accessToken)
                contentType(ContentType.Application.Json)
                setBody(budget)
            }
        }
    }

    override suspend fun updateBudget(
        accessToken: String,
        budget: BudgetDto
    ): Result<CommonResponseDto, NetworkError> {
        return safeCall<CommonResponseDto> {
            httpClient.post(urlString = constructUrl("/budgets/${budget.id}")) {
                bearerAuth(accessToken)
                contentType(ContentType.Application.Json)
                setBody(budget)
            }
        }
    }


}