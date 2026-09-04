package com.app.koshpal.core.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID


@Entity(
    tableName = "categories",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentCategoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("parentCategoryId")]
)
data class CategoryEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val iconResId: String?,
    val colorHex: String,
    val parentCategoryId: String? = null,
    val lastModifiedTimeStamp: Long = System.currentTimeMillis()
)
