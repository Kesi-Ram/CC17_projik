package com.example.cc17_projik

import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

enum class Condition(
    val label: String,
    @DrawableRes val bgRes: Int,
    @ColorRes val textColorRes: Int
) {
    GOOD("GOOD", R.drawable.bg_badge_good, R.color.badge_good_text),
    USED("USED", R.drawable.bg_badge_used, R.color.badge_used_text)
}

data class Listing(
    val id: Int,
    val title: String,
    val price: Int,
    val condition: Condition,
    @DrawableRes val imageRes: Int
)

fun TextView.bindCondition(condition: Condition) {
    text = condition.label
    setBackgroundResource(condition.bgRes)
    setTextColor(ContextCompat.getColor(context, condition.textColorRes))
}