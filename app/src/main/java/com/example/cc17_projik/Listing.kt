package com.example.cc17_projik

import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

//condition badges
enum class Condition(
    val label: String,
    @DrawableRes val bgRes: Int,
    @ColorRes val textColorRes: Int
) {
    GOOD("GOOD", R.drawable.bg_badge_good, R.color.badge_good_text),
    USED("USED", R.drawable.bg_badge_used, R.color.badge_used_text),

    NEW("NEW", R.drawable.bg_badge_new, R.color.badge_new_text)
}

//category for filtering
enum class Category(val label: String) {
    OTHERS("Others"),
    PHONES("Phones"),
    PC_PARTS("PC parts"),
    LAPTOPS("Laptops"),
    AUDIO("Audio"),
    ACCESSORIES("Accessories")
}
//listing data class
data class Listing(
    val id: Int,
    val title: String,
    val price: Int,
    val condition: Condition,
    val category: Category,
    @DrawableRes val imageRes: Int
)

fun TextView.bindCondition(condition: Condition) {
    text = condition.label
    setBackgroundResource(condition.bgRes)
    setTextColor(ContextCompat.getColor(context, condition.textColorRes))
}