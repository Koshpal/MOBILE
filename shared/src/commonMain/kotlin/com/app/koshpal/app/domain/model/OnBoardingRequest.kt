package com.app.koshpal.app.domain.model

data class OnBoardingRequest(
    val improvementGoals: List<String>? = null,
    val moneyChallenges: List<String>? = null,
    val moneyManagementStyles: List<String>? = null,
    val helpNeeded: List<String>? = null
)


