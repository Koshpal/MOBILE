package com.app.koshpal.app.data.mapper

import com.app.koshpal.app.domain.model.Due
import com.app.koshpal.core.data.entities.DueEntity

fun DueEntity.toDue(): Due {
    return Due(
        id = id,
        title = title.take(20),
        date = date,
        amount = amount,
        status = status,
        frequency = frequency,
        type = type,
        reminderType = reminderType,
        overdueInfo = overdueInfo,
        isCompleted = isCompleted,
        iconResId = iconResId,
        colorHex = colorHex,
        reminderTime = reminderTime,
        customFrequencyDays = customFrequencyDays,
    )
}

fun Due.toDueEntity(): DueEntity {
    return DueEntity(
        id = id,
        title = title.take(20),
        date = date,
        amount = amount,
        status = status,
        frequency = frequency,
        type = type,
        reminderType = reminderType,
        overdueInfo = overdueInfo,
        isCompleted = isCompleted,
        iconResId = iconResId,
        colorHex = colorHex,
        reminderTime = reminderTime,
        customFrequencyDays = customFrequencyDays,
    )
}
