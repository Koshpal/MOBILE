package com.app.koshpal.app.domain.model

import java.util.UUID

data class ReminderType(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val iconResId: String?,
    val colorHex: String,
    val lastModifiedTimeStamp: Long = System.currentTimeMillis()
)

val defaultReminderTypes = listOf(
    ReminderType("rem_1", "Bill", "receipt", "0xFFC45100"),
    ReminderType("rem_2", "Phone Bill", "phone", "0xFF2A52BE"),
    ReminderType("rem_3", "Card bill", "credit_card", "0xFFC2185B"),
    ReminderType("rem_4", "Salary", "payments", "0xFF2E7D32"),
    ReminderType("rem_5", "Rent", "home", "0xFFE65100"),
    ReminderType("rem_6", "Money Transfer", "swap_horiz", "0xFF00796B"),
    ReminderType("rem_7", "Subscription", "subscriptions", "0xFFE50914"),
    ReminderType("rem_8", "Internet Bill", "language", "0xFF3F51B5"),
    ReminderType("rem_9", "Water Bill", "water_drop", "0xFF03A9F4"),
    ReminderType("rem_10", "Electricity Bill", "bolt", "0xFFFFC107")
)
