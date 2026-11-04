package com.example.cc17_projik

import androidx.annotation.DrawableRes

data class TradeItem(
    val id: Int,
    val name: String,
    @DrawableRes val imageRes: Int,
    val proposalCount: Int
)

val myTradeItems = listOf(
    TradeItem(1, "Iphone 13 Pro Max", R.drawable.ic_home, 1),
    TradeItem(2, "Airpods Pro", R.drawable.ic_home, 0),
    TradeItem(3, "Macbook Pro", R.drawable.ic_home, 0)
)