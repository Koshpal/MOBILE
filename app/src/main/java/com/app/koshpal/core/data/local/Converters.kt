package com.app.koshpal.core.data.local

import androidx.room.TypeConverter
import com.app.koshpal.core.data.entities.enums.NotificationType
import com.app.koshpal.core.data.entities.enums.TransactionType
import java.time.LocalDate

object Converters {

    @TypeConverter
    fun fromNotificationType(type: NotificationType): String {
        return type.name
    }

    @TypeConverter
    fun toNotificationType(name: String): NotificationType {
        return NotificationType.valueOf(name)
    }

    @TypeConverter
    fun fromTransactionType(type: TransactionType): String {
        return type.name
    }

    @TypeConverter
    fun toTransactionType(name: String): TransactionType {
        return TransactionType.valueOf(name)
    }


    @TypeConverter
    fun fromLocalDate(date: LocalDate): Long =
        date.toEpochDay()

    @TypeConverter
    fun toLocalDate(epochDay: Long): LocalDate =
        LocalDate.ofEpochDay(epochDay)

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toStringList(data: String): List<String> {
        return if (data.isBlank()) emptyList() else data.split(",")
    }
}
