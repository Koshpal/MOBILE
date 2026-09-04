package com.app.koshpal.core.data.entities.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class BudgetPeriod {
    @SerialName("WEEKLY")
    Weekly,
    @SerialName("MONTHLY")
    Monthly,
    @SerialName("YEARLY")
    Yearly
}