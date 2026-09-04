package com.app.koshpal.core.data.remote.source

import com.app.koshpal.core.data.remote.dto.GoalDto
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result
import com.app.koshpal.core.data.remote.dto.CommonResponseDto

interface GoalDataSource {
    suspend fun getGoals(accessToken: String): Result<List<GoalDto>, NetworkError>
    suspend fun createGoal(accessToken: String, goal: GoalDto): Result<CommonResponseDto, NetworkError>
    suspend fun updateGoal(accessToken: String, goal: GoalDto): Result<CommonResponseDto, NetworkError>
}
