package com.app.koshpal.core.data.entities.enums

import kotlinx.serialization.Serializable

@Serializable
enum class BudgetType {
    RECURRING,
    ONE_TIME
}

fun BudgetType.toReadableString(): String = when(this) {
    BudgetType.RECURRING -> "Recurring"
    BudgetType.ONE_TIME -> "One Time"
}
