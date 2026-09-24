package com.app.koshpal.app.data.mapper

import com.app.koshpal.app.domain.model.Tag
import com.app.koshpal.core.data.entities.TagEntity

fun TagEntity.toTag(): Tag {
    return Tag(
        id = id,
        name = name.take(20),
        budgetGoal = budgetGoal,
        colorHex = colorHex,
    )
}

fun Tag.toTagEntity(): TagEntity {
    return TagEntity(
        id = id,
        name = name.take(20),
        budgetGoal = budgetGoal,
        colorHex = colorHex,
    )
}
