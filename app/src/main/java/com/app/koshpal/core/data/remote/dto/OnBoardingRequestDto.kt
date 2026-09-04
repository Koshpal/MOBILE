package com.app.koshpal.core.data.remote.dto

data class OnBoardingRequestDto(
    val improvementGoals: List<String>?,
    val moneyChallenges: List<String>?,
    val moneyManagementStyles: List<String>?,
    val helpNeeded: List<String>?
)

