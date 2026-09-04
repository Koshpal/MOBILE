package com.app.koshpal.core.data.remote.source

import com.app.koshpal.core.data.networking.constructUrl
import com.app.koshpal.core.data.networking.safeCall
import com.app.koshpal.core.data.remote.dto.CommonResponseDto
import com.app.koshpal.core.data.remote.dto.EmployeeGoalsResponseDto
import com.app.koshpal.core.data.remote.dto.GoalDto
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result
import com.app.koshpal.core.domain.util.map
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class RemoteGoalDataSource(
    private val httpClient: HttpClient,
) : GoalDataSource {
    override suspend fun getGoals(accessToken: String): Result<List<GoalDto>, NetworkError> {
        val result = safeCall<EmployeeGoalsResponseDto> {
            httpClient.get(urlString = constructUrl("/employee/goals")) {
                bearerAuth(accessToken)
            }
        }
        return result.map { it.financialGoals }
    }

    override suspend fun createGoal(accessToken: String, goal: GoalDto): Result<CommonResponseDto, NetworkError> {
        return safeCall<CommonResponseDto> {
            httpClient.post(urlString = constructUrl("/employee/goals")) {
                bearerAuth(accessToken)
                contentType(ContentType.Application.Json)
                setBody(goal)
            }
        }
    }

    override suspend fun updateGoal(accessToken: String, goal: GoalDto): Result<CommonResponseDto, NetworkError> {
        return safeCall<CommonResponseDto> {
            httpClient.post(urlString = constructUrl("/employee/goals")) {
                bearerAuth(accessToken)
                contentType(ContentType.Application.Json)
                setBody(goal)
            }
        }
    }
}
