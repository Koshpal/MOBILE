package com.app.koshpal.app.domain.model

data class Transactions(
    val transactions: List<Transaction>,
){
    val isSynced: Boolean get() = transactions.all { it.isSynced }
}
