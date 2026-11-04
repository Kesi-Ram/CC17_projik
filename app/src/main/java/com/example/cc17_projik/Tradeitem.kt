package com.example.cc17_projik

import androidx.annotation.DrawableRes

// One item the user has put up for trade.
// proposalCount = how many trade offers other users have sent for this item.
data class TradeItem(
    val id: Int,
    val name: String,
    @DrawableRes val imageRes: Int,
    val proposalCount: Int
)

// Sample data for now. Replace the images with your real photos later.
val myTradeItems = listOf(
    TradeItem(1, "Iphone 13 Pro max", R.drawable.ic_home, 1),
    TradeItem(2, "Airpods pro", R.drawable.ic_home, 0),
    TradeItem(3, "Macbook Pro", R.drawable.ic_home, 0)
)