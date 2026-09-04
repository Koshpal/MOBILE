package com.app.koshpal.app.domain.model

import com.app.koshpal.R
import java.util.UUID

data class Category(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val iconResId: String?,
    val colorHex: String,
    val parentCategoryId: String? = null,
    val lastModifiedTimeStamp: Long = System.currentTimeMillis()
)

private fun createSubCategory(
    id: String,
    name: String,
    icon: String,
    parentId: String
): Category {
    val parentColor = defaultDialogCategories.find { it.id == parentId }?.colorHex ?: "0xFF455A64"
    return Category(
        id = id,
        title = name,
        iconResId = icon,
        colorHex = parentColor,
        parentCategoryId = parentId
    )
}

fun String.toColorLong(): Long {
    return try {
        val cleanHex = this.removePrefix("0x").removePrefix("#").trim()
        if (cleanHex.length == 6) {
            "FF$cleanHex".toLong(16)
        } else {
            cleanHex.toLong(16)
        }
    } catch (_: Exception) {
        0xFF9E9E9E
    }
}

fun String.toDrawableResId(): Int? = when (this.lowercase()) {
    "none" -> null
    "cart", "shopping_cart", "shopping" -> R.drawable.shopping_bag_24px
    "ramen_dining" -> R.drawable.ramen_dining_24px
    "apartment" -> R.drawable.apartment_24px
    "storefront" -> R.drawable.storefront_24px
    "flight" -> R.drawable.flight_24px
    "movie" -> R.drawable.movie_24px
    "money" -> R.drawable.money_24px
    "category" -> R.drawable.category_24px
    "restaurant", "eating out", "eating_out", "food" -> R.drawable.restaurant_24px
    "confirmation" -> R.drawable.confirmation_number_24px
    "pie_chart" -> R.drawable.pie_chart_24px
    "favorite_border", "favorite", "family" -> R.drawable.favorite_24px
    "grid_view" -> R.drawable.grid_view_24px
    "bolt", "electricity" -> R.drawable.bolt_24px
    "wifi", "internet", "language" -> R.drawable.wifi_24px
    "phone" -> R.drawable.call_24px
    "build" -> R.drawable.build_24px
    "local_cafe" -> R.drawable.local_cafe_24px
    "fast_food", "fastfood" -> R.drawable.fastfood_24px
    "local_bar" -> R.drawable.local_bar_24px
    "directions_car" -> R.drawable.directions_car_24px
    "hotel" -> R.drawable.hotel_24px
    "checkroom" -> R.drawable.checkroom_24px
    "devices" -> R.drawable.devices_24px
    "credit_card" -> R.drawable.credit_card_24px
    "payments", "salary" -> R.drawable.payments_24px
    "swap_horiz", "money_transfer" -> R.drawable.swap_horiz_24px
    "subscriptions", "subscription" -> R.drawable.subscriptions_24px
    "video_library" -> R.drawable.video_library_24px
    "chat" -> R.drawable.chat_24px
    "water_drop", "water" -> R.drawable.water_drop_24px
    "notifications" -> R.drawable.notifications_24px
    "home", "rent" -> R.drawable.home_24px_2
    "receipt", "bill", "bills" -> R.drawable.receipt_24px
    else -> null
}


val defaultDialogCategories = listOf(
    Category("1", "Bills", "receipt", "0xFFC45100"),
    Category("2", "Cash", "money", "0xFF2E7D32"),
    Category("3", "Eating out", "restaurant", "0xFF2A52BE"),
    Category("4", "Entertainment", "confirmation", "0xFF00796B"),
    Category("5", "Expenses", "pie_chart", "0xFF0277BD"),
    Category("6", "Family", "favorite_border", "0xFFC2185B"),
    Category("7", "General", "grid_view", "0xFF455A64"),
    Category("8", "Groceries", "shopping_cart", "0xFFD32F2F"),
    Category("9", "Shopping", "storefront", "0xFF00695C"),
    Category("10", "Travel", "flight", "0xFFE65100")
)

val defaultSubCategories = listOf(
    createSubCategory(id = "sub_1", name = "Rent", icon = "home", parentId = "1"),
    createSubCategory(id = "sub_2", name = "Electricity", icon = "bolt", parentId = "1"),
    createSubCategory(id = "sub_3", name = "Internet", icon = "wifi", parentId = "1"),
    createSubCategory(id = "sub_4", name = "Telephone", icon = "phone", parentId = "1"),
    createSubCategory(id = "sub_5", name = "Maintenance", icon = "build", parentId = "1"),
    createSubCategory(id = "sub_6", name = "Cafe & Coffee", icon = "local_cafe", parentId = "3"),
    createSubCategory(id = "sub_7", name = "Fast Food", icon = "fastfood", parentId = "3"),
    createSubCategory(id = "sub_8", name = "Bars & Drinks", icon = "local_bar", parentId = "3"),
    createSubCategory(id = "sub_9", name = "Supermarket", icon = "shopping_cart", parentId = "8"),
    createSubCategory(id = "sub_10", name = "Fruits & Veggies", icon = "restaurant", parentId = "8"),
    createSubCategory(id = "sub_11", name = "Cabs & Transit", icon = "directions_car", parentId = "10"),
    createSubCategory(id = "sub_12", name = "Hotels & Stays", icon = "hotel", parentId = "10"),
    createSubCategory(id = "sub_13", name = "Flights", icon = "flight", parentId = "10"),
    createSubCategory(id = "sub_14", name = "Clothes & Apparel", icon = "checkroom", parentId = "9"),
    createSubCategory(id = "sub_15", name = "Electronics", icon = "devices", parentId = "9"),
    createSubCategory(id = "sub_16", name = "Movies & Cinema", icon = "movie", parentId = "4"),
    createSubCategory(id = "sub_17", name = "Streaming Services", icon = "devices", parentId = "4")
)

val availableCategoryColors = listOf(
    "0xFFC45100",
    "0xFF2E7D32",
    "0xFF2A52BE",
    "0xFF00796B",
    "0xFF0277BD",
    "0xFFC2185B",
    "0xFF455A64",
    "0xFFD32F2F",
    "0xFF00695C",
    "0xFFE65100",
    "0xFF512DA8",
    "0xFF303F9F",
    "0xFF7B1FA2",
    "0xFF827717",
    "0xFF5D4037",
    "0xFF37474F"
)

val availableCategoryIcons = listOf(
    "none",
    "category",
    "receipt",
    "money",
    "shopping_cart",
    "restaurant",
    "fast_food",
    "local_cafe",
    "local_bar",
    "home",
    "bolt",
    "wifi",
    "phone",
    "build",
    "directions_car",
    "flight",
    "hotel",
    "checkroom",
    "devices",
    "credit_card",
    "payments",
    "swap_horiz",
    "subscriptions",
    "video_library",
    "chat",
    "confirmation",
    "pie_chart",
    "favorite_border",
    "grid_view"
)


fun String.getInitials(): String {
    val stopWords = setOf("and", "of", "the","for", "with", "in", "on", "at", "to", "a", "an", "&", "/", "@")
    return this.split(" ")
        .filter { it.lowercase() !in stopWords }
        .take(2)
        .map { it.first() }
        .joinToString("")
        .uppercase()
}