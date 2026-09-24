package com.app.koshpal.app.domain.model

data class Transactions(
    val transactions: List<Transaction>,
    val page: Int? = 1 ,
    val hasMore: Boolean? = false
){
    val isSynced: Boolean get() = transactions.all { it.isSynced }
}
