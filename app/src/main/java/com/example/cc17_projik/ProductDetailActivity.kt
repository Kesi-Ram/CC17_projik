package com.example.cc17_projik

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.cc17_projik.databinding.ActivityProductDetailBinding

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding
    private val store by lazy { LocalStore.get(this) }
    private val listingId get() = intent.getIntExtra(EXTRA_LISTING_ID, -1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemInsets()
        binding.btnBack.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        val item = store.data.listings.find { it.id == listingId }
        if (item == null) {
            Toast.makeText(this, R.string.listing_missing, Toast.LENGTH_LONG).show()
            finish()
            return
        }
        with(binding) {
            imgProduct.showItemPhoto(item.imageUri, item.imageRes)
            tvTitle.text = item.title
            tvPrice.text = getString(R.string.price_value, item.price)
            tvCondition.bindCondition(item.condition)
            tvCategory.text = getString(R.string.category_value, item.category.label)
            tvSeller.text = getString(R.string.seller_value, item.sellerName)
            tvLocation.text = getString(R.string.location_value, item.location.ifBlank { getString(R.string.not_specified) })
            tvDescription.text = item.description.ifBlank { getString(R.string.no_description) }
            tvAvailability.setText(if (item.available) R.string.available else R.string.traded)
            val ownListing = item.sellerId == store.profileId
            val pending = store.data.proposals.find {
                it.listingId == listingId && it.senderId == store.profileId && it.status == TradeStatus.PENDING
            }
            btnProposeTrade.isEnabled = item.available || ownListing
            btnProposeTrade.setText(when {
                ownListing -> R.string.view_offers
                !item.available -> R.string.traded
                pending != null -> R.string.view_your_offer
                else -> R.string.propose_trade
            })
            btnProposeTrade.setOnClickListener {
                when {
                    ownListing -> startActivity(Intent(this@ProductDetailActivity, TradeActivity::class.java))
                    pending != null -> startActivity(Intent(this@ProductDetailActivity, TradeDetailActivity::class.java)
                        .putExtra(TradeDetailActivity.EXTRA_PROPOSAL_ID, pending.id))
                    else -> startActivity(Intent(this@ProductDetailActivity, ProposeTradeActivity::class.java)
                        .putExtra(EXTRA_LISTING_ID, listingId))
                }
            }
        }
    }

    companion object { const val EXTRA_LISTING_ID = "listing_id" }
}
