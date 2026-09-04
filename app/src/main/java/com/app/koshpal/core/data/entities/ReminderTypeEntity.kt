package com.app.koshpal.core.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "reminder_types")
data class ReminderTypeEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val iconResId: String?,
    val colorHex: String,
    val lastModifiedTimeStamp: Long = System.currentTimeMillis()
)
