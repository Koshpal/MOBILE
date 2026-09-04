package com.app.koshpal.core.data.remote.source

import com.app.koshpal.core.data.remote.dto.BudgetDto
import com.app.koshpal.core.data.remote.dto.CommonResponseDto
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result

interface BudgetDataSource {
    suspend fun getBudgets(accessToken: String): Result<List<BudgetDto>, NetworkError>
    suspend fun createBudget(accessToken: String, budget: BudgetDto): Result<CommonResponseDto, NetworkError>
    suspend fun updateBudget(accessToken: String, budget: BudgetDto): Result<CommonResponseDto, NetworkError>
}