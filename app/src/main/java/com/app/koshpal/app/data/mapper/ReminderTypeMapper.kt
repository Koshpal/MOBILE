package com.app.koshpal.app.data.mapper

import com.app.koshpal.app.domain.model.ReminderType
import com.app.koshpal.core.data.entities.ReminderTypeEntity

fun ReminderTypeEntity.toReminderType(): ReminderType {
    return ReminderType(
        id = id,
        name = name,
        iconResId = iconResId,
        colorHex = colorHex,
        lastModifiedTimeStamp = lastModifiedTimeStamp
    )
}

fun ReminderType.toReminderTypeEntity(): ReminderTypeEntity {
    return ReminderTypeEntity(
        id = id,
        name = name,
        iconResId = iconResId,
        colorHex = colorHex,
        lastModifiedTimeStamp = lastModifiedTimeStamp
    )
}
