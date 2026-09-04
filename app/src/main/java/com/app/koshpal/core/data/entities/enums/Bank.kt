package com.app.koshpal.core.data.entities.enums

import kotlinx.serialization.Serializable

@Serializable
enum class Bank {
    SBI,
    BOB,
    BANK_OF_INDIA,
    BANK_OF_MAHARASHTRA,
    CANARA,
    CENTRAL_BANK,
    INDIAN_BANK,
    INDIAN_OVERSEAS_BANK,
    PUNJAB_SIND_BANK,
    PNB,
    UCO_BANK,
    UNION_BANK,

    AXIS,
    BANDHAN,
    CSB,
    CITY_UNION,
    DCB,
    DHANLAXMI,
    FEDERAL,
    HDFC,
    ICICI,
    INDUSIND,
    IDFC_FIRST,
    JK_BANK,
    KARNATAKA_BANK,
    KARUR_VYSYA,
    KOTAK,
    NAINITAL,
    RBL,
    SOUTH_INDIAN,
    TAMILNAD_MERCANTILE,
    YES_BANK,
    IDBI,

    AU_SFB,
    CAPITAL_SFB,
    EQUITAS,
    ESAF_SFB,
    SURYODAY_SFB,
    UJJIVAN_SFB,
    UTKARSH_SFB,
    SLICE_SFB,
    JANA_SFB,
    SHIVALIK_SFB,
    UNITY_SFB,

    AIRTEL_PAYMENTS_BANK,
    INDIA_POST_PAYMENTS_BANK,
    FINO_PAYMENTS_BANK,
    PAYTM_PAYMENTS_BANK,
    JIO_PAYMENTS_BANK,
    NSDL_PAYMENTS_BANK,

    UNKNOWN;

    fun toDisplayName(): String = when (this) {
        SBI -> "SBI"
        BOB -> "Bank of Baroda"
        BANK_OF_INDIA -> "Bank of India"
        BANK_OF_MAHARASHTRA -> "Bank of Maharashtra"
        CANARA -> "Canara Bank"
        CENTRAL_BANK -> "Central Bank"
        INDIAN_BANK -> "Indian Bank"
        INDIAN_OVERSEAS_BANK -> "Indian Overseas Bank"
        PUNJAB_SIND_BANK -> "Punjab & Sind Bank"
        PNB -> "PNB"
        UCO_BANK -> "UCO Bank"
        UNION_BANK -> "Union Bank"
        AXIS -> "Axis Bank"
        BANDHAN -> "Bandhan Bank"
        CSB -> "CSB Bank"
        CITY_UNION -> "City Union Bank"
        DCB -> "DCB Bank"
        DHANLAXMI -> "Dhanlaxmi Bank"
        FEDERAL -> "Federal Bank"
        HDFC -> "HDFC Bank"
        ICICI -> "ICICI Bank"
        INDUSIND -> "IndusInd Bank"
        IDFC_FIRST -> "IDFC First Bank"
        JK_BANK -> "J&K Bank"
        KARNATAKA_BANK -> "Karnataka Bank"
        KARUR_VYSYA -> "Karur Vysya Bank"
        KOTAK -> "Kotak Mahindra Bank"
        NAINITAL -> "Nainital Bank"
        RBL -> "RBL Bank"
        SOUTH_INDIAN -> "South Indian Bank"
        TAMILNAD_MERCANTILE -> "Tamilnad Mercantile Bank"
        YES_BANK -> "Yes Bank"
        IDBI -> "IDBI Bank"
        AU_SFB -> "AU Small Finance Bank"
        CAPITAL_SFB -> "Capital Small Finance Bank"
        EQUITAS -> "Equitas Bank"
        ESAF_SFB -> "ESAF Small Finance Bank"
        SURYODAY_SFB -> "Suryoday Small Finance Bank"
        UJJIVAN_SFB -> "Ujjivan Small Finance Bank"
        UTKARSH_SFB -> "Utkarsh Small Finance Bank"
        SLICE_SFB -> "Slice Small Finance Bank"
        JANA_SFB -> "Jana Small Finance Bank"
        SHIVALIK_SFB -> "Shivalik Small Finance Bank"
        UNITY_SFB -> "Unity Small Finance Bank"
        AIRTEL_PAYMENTS_BANK -> "Airtel Payments Bank"
        INDIA_POST_PAYMENTS_BANK -> "India Post Payments Bank"
        FINO_PAYMENTS_BANK -> "Fino Payments Bank"
        PAYTM_PAYMENTS_BANK -> "Paytm Payments Bank"
        JIO_PAYMENTS_BANK -> "Jio Payments Bank"
        NSDL_PAYMENTS_BANK -> "NSDL Payments Bank"
        UNKNOWN -> "Unknown Bank"
    }
}

fun String?.toBankDisplayName(): String {
    if (this == null) return "Unknown Bank"
    return try {
        Bank.valueOf(this).toDisplayName()
    } catch (e: Exception) {
        "Unknown Bank"
    }
}
