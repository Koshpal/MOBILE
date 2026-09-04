package com.app.koshpal.core.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val budgetGoal: Double,
    val colorHex: String,
    val lastModifiedTimeStamp: Long = System.currentTimeMillis()
)
