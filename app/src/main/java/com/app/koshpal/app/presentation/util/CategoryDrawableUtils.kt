package com.app.koshpal.app.presentation.util

import com.app.koshpal.R

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
