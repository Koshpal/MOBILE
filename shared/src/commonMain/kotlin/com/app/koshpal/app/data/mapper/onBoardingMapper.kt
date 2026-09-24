package com.app.koshpal.app.data.mapper

import com.app.koshpal.app.domain.model.OnBoardingRequest
import com.app.koshpal.core.data.remote.dto.OnBoardingRequestDto

fun OnBoardingRequest.toOnBoardingRequestDto() : OnBoardingRequestDto {
    return OnBoardingRequestDto(
        moneyChallenges = moneyChallenges,
        moneyManagementStyles = moneyManagementStyles,
        improvementGoals = improvementGoals,
        helpNeeded = helpNeeded
    )
}

