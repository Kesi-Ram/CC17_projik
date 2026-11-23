package com.example.cc17_projik

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cc17_projik.databinding.ViewBottomNavBinding

fun TextView.bindCondition(condition: Condition) {
    text = condition.label
    val (background, color) = when (condition) {
        Condition.GOOD -> R.drawable.bg_badge_good to R.color.badge_good_text
        Condition.USED, Condition.NEEDS_REPAIR -> R.drawable.bg_badge_used to R.color.badge_used_text
        Condition.NEW -> R.drawable.bg_badge_new to R.color.badge_new_text
    }
    setBackgroundResource(background)
    setTextColor(ContextCompat.getColor(context, color))
}

fun ImageView.showItemPhoto(imageUri: String?, fallback: Int = 0) {
    setImageDrawable(null)
    if (!imageUri.isNullOrBlank()) {
        try {
            setImageURI(imageUri.toUri())
        } catch (_: SecurityException) {
            // A photo may have been removed or its document permission revoked.
        } catch (_: java.io.FileNotFoundException) {
        }
    }
    if (drawable == null) setImageResource(fallback.takeIf { it != 0 } ?: R.drawable.ic_inventory)
}

fun Activity.applySystemInsets(view: View = findViewById(R.id.main)) {
    val left = view.paddingLeft
    val top = view.paddingTop
    val right = view.paddingRight
    val bottom = view.paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(view) { target, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
        target.setPadding(left + bars.left, top + bars.top, right + bars.right, bottom + bars.bottom)
        insets
    }
}

fun Context.showError(error: Throwable) {
    Toast.makeText(this, error.message ?: getString(R.string.save_failed), Toast.LENGTH_LONG).show()
}

fun Context.openProduct(id: Int) {
    startActivity(Intent(this, ProductDetailActivity::class.java).putExtra(ProductDetailActivity.EXTRA_LISTING_ID, id))
}

fun Activity.setupBottomNav(binding: ViewBottomNavBinding) {
    val destinations = listOf(
        Triple(binding.navHome, binding.navHomeIcon, Dashboard::class.java),
        Triple(binding.navMarket, binding.navMarketIcon, MarketActivity::class.java),
        Triple(binding.navSell, binding.navSellIcon, SellActivity::class.java),
        Triple(binding.navTrade, binding.navTradeIcon, TradeActivity::class.java),
        Triple(binding.navMaps, binding.navMapsIcon, BinLocationsActivity::class.java)
    )
    destinations.forEach { (view, icon, destination) ->
        icon.background = null
        if (javaClass == destination) icon.setBackgroundResource(R.drawable.bg_nav_active)
        view.setOnClickListener {
            if (javaClass != destination) {
                startActivity(Intent(this, destination).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
            }
        }
    }
}

fun Context.openGoogleMaps(bin: BinLocation) {
    val uri = bin.googleMapsUrl().toUri()
    try {
        startActivity(Intent(Intent.ACTION_VIEW, uri).setPackage("com.google.android.apps.maps"))
    } catch (_: ActivityNotFoundException) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, R.string.no_maps_handler, Toast.LENGTH_LONG).show()
        }
    }
}
