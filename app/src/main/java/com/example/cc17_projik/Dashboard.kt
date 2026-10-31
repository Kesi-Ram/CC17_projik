package com.example.cc17_projik

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cc17_projik.databinding.ActivityDashboardBinding
import com.example.cc17_projik.databinding.ItemStepBinding
import androidx.activity.addCallback
import android.content.Intent

class Dashboard : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val listingAdapter = ListingAdapter { listing -> openListing(listing) }

    // TODO: swap ic_home for your real photos, e.g. R.drawable.macbook_pro
    private val listings = listOf(
        Listing(1, "Macbook Pro", 430, Condition.GOOD, R.drawable.ic_home),
        Listing(2, "Iphone 13 Pro Max", 320, Condition.USED, R.drawable.ic_home),
        Listing(3, "Iphone 11 64gb", 150, Condition.GOOD, R.drawable.ic_home)
    )

    private val tradingSteps = listOf(
        "Browse & Find an Item" to "Scroll through the marketplace on the Home screen. Use category filters or search to find an item you want.",
        "Open Item Details" to "Tap on any listing card to view the full item details — price, condition, description, and seller info.",
        "Tap \"Propose a Trade\"" to "On the item detail page, tap the purple \"Propose a Trade\" button at the bottom of the screen.",
        "Fill in Your Item Details" to "Enter your item's title, description, and condition. This is the item you are offering in exchange.",
        "Take or Upload a Photo" to "Tap the photo area to take a picture of your item or upload one from your gallery. A clear photo helps your proposal get accepted.",
        "Publish Your Proposal" to "Once all fields are filled, tap \"Publish Trade Proposal\". Your offer is sent directly to the seller, who will be notified and can accept or decline."
    )

    // Placeholder text: replace with your team's real selling steps.
    private val sellingSteps = listOf(
        "Tap Sell" to "Open the Sell tab from the bottom navigation bar.",
        "Add Item Details" to "Enter a title, description, price, and condition for your gadget.",
        "Add Photos" to "Take or upload clear photos so buyers can see what they are getting.",
        "Publish Your Listing" to "Tap Publish and your item goes live on the marketplace."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupFeatured(listings.first())
        setupGrid(listings.drop(1))
        setupGuide()
        setupBottomNav()

        binding.btnViewMore.setOnClickListener {
            startActivity(Intent(this, MarketActivity::class.java))
        }

        // Open drawer when menu button is tapped
        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
        }

        // Log out closes the drawer (add real logic later)
        binding.drawerLogout.setOnClickListener {
            binding.drawerLayout.closeDrawers()
        }

        // Back button closes drawer if it's open
        onBackPressedDispatcher.addCallback(this) {
            if (binding.drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            } else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setupFeatured(item: Listing) = with(binding) {
        imgFeatured.setImageResource(item.imageRes)
        tvFeaturedPrice.text = "$${item.price}"
        tvFeaturedTitle.text = item.title
        tvFeaturedCondition.bindCondition(item.condition)
        featuredCard.setOnClickListener { openListing(item) }
    }

    private fun setupGrid(items: List<Listing>) {
        binding.rvListings.adapter = listingAdapter
        listingAdapter.submitList(items)
    }

    private fun setupGuide() {
        showSteps(tradingSteps)
        binding.guideToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                showSteps(if (checkedId == R.id.btnTrading) tradingSteps else sellingSteps)
            }
        }
    }

    private fun showSteps(steps: List<Pair<String, String>>) {
        val container = binding.stepsContainer
        container.removeAllViews()
        steps.forEachIndexed { index, (title, desc) ->
            val row = ItemStepBinding.inflate(layoutInflater, container, false)
            row.tvNumber.text = (index + 1).toString()
            row.tvStepTitle.text = title
            row.tvStepDesc.text = desc
            container.addView(row.root)
        }
    }

    private fun setupBottomNav() {
        val icons = listOf(
            binding.navHomeIcon,
            binding.navMarketIcon,
            binding.navSellIcon,
            binding.navTradeIcon
        )

        fun select(index: Int) {
            icons.forEachIndexed { i, icon ->
                if (i == index) icon.setBackgroundResource(R.drawable.bg_nav_active)
                else icon.background = null
            }
        }

        binding.navHome.setOnClickListener { select(0) }
        binding.navMarket.setOnClickListener {
            startActivity(Intent(this, MarketActivity::class.java))
        }
        binding.navSell.setOnClickListener { select(2); toast("Sell screen goes here") }
        binding.navTrade.setOnClickListener { select(3); toast("Trade screen goes here") }

        select(0)
    }

    private fun openListing(listing: Listing) {
        // TODO: open your item detail screen with listing.id
        toast("Open details for ${listing.title}")
    }

    private fun toast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}