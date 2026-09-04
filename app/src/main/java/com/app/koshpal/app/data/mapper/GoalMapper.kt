package com.app.koshpal.app.data.mapper

import com.app.koshpal.app.domain.model.Goal
import com.app.koshpal.core.data.entities.GoalEntity
import com.app.koshpal.core.data.remote.dto.GoalDto

fun GoalEntity.toGoal(): Goal {
    return Goal(
        id = id,
        title = title.take(20),
        targetAmount = targetAmount,
        savedAmount = savedAmount,
        monthlySavings = monthlySavings,
        durationMonths = durationMonths,
        iconResId = iconResId,
        colorHex = colorHex,
        creationDate = creationDate,
        isAchieved = isAchieved,
        tagId = tagId,
        imageUri = imageUri,
        isSynced = isSynced,
    )
}

fun Goal.toGoalEntity(): GoalEntity {
    return GoalEntity(
        id = id,
        title = title.take(20),
        targetAmount = targetAmount,
        savedAmount = savedAmount,
        monthlySavings = monthlySavings,
        durationMonths = durationMonths,
        iconResId = iconResId,
        colorHex = colorHex,
        creationDate = creationDate,
        isAchieved = isAchieved,
        tagId = tagId,
        imageUri = imageUri,
        lastModifiedTimeStamp = System.currentTimeMillis(),
        isSynced = isSynced,
    )
}

fun GoalDto.toGoal(): Goal {
    return Goal(
        id = id,
        title = title.take(20),
        targetAmount = targetAmount,
        savedAmount = savedAmount,
        monthlySavings = monthlySavings,
        durationMonths = durationMonths ?: 1,
        iconResId = iconResId ?: "flag",
        colorHex = colorHex ?: "0xFF4CAF50",
        creationDate = creationDate ?: "2026-01-01",
        isAchieved = isAchieved,
        tagId = tagId,
        imageUri = imageUri,
        isSynced = true,
    )
}

fun Goal.toGoalDto(): GoalDto {
    return GoalDto(
        id = id,
        title = title.take(20),
        targetAmount = targetAmount,
        savedAmount = savedAmount,
        monthlySavings = monthlySavings,
        durationMonths = durationMonths,
        iconResId = iconResId,
        colorHex = colorHex,
        creationDate = creationDate,
        isAchieved = isAchieved,
        tagId = tagId,
        imageUri = imageUri,
    )
}
