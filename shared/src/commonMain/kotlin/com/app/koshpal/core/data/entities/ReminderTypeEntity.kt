package com.app.koshpal.core.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Entity(tableName = "reminder_types")
data class ReminderTypeEntity(
    @PrimaryKey
    val id: String = Uuid.random().toString(),
    val name: String,
    val iconResId: String?,
    val colorHex: String,
    val lastModifiedTimeStamp: Long = Clock.System.now().toEpochMilliseconds()
)
