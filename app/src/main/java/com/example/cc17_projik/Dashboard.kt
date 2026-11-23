package com.example.cc17_projik

import android.os.Bundle
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

    private val store by lazy { LocalStore.get(this) }

    private val tradingSteps = listOf(
        R.string.guide_trade_step_1_title to R.string.guide_trade_step_1_description,
        R.string.guide_trade_step_2_title to R.string.guide_trade_step_2_description,
        R.string.guide_trade_step_3_title to R.string.guide_trade_step_3_description,
        R.string.guide_trade_step_4_title to R.string.guide_trade_step_4_description,
        R.string.guide_trade_step_5_title to R.string.guide_trade_step_5_description,
        R.string.guide_trade_step_6_title to R.string.guide_trade_step_6_description
    )

    private val sellingSteps = listOf(
        R.string.guide_sell_step_1_title to R.string.guide_sell_step_1_description,
        R.string.guide_sell_step_2_title to R.string.guide_sell_step_2_description,
        R.string.guide_sell_step_3_title to R.string.guide_sell_step_3_description,
        R.string.guide_sell_step_4_title to R.string.guide_sell_step_4_description
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

        setupGuide()
        setupBottomNav()

        binding.btnViewMore.setOnClickListener {
            startActivity(Intent(this, MarketActivity::class.java))
        }

        // Open drawer when menu button is tapped
        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
        }

        // Log out closes the drawer
        binding.drawerLogout.setOnClickListener {

            // Close the navigation drawer
            binding.drawerLayout.closeDrawers()

            // Go back to LoginActivity
            val intent = Intent(this, MainActivity::class.java)

            // Clear dashboard from the back stack
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            finish()
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
        imgFeatured.showItemPhoto(item.imageUri, item.imageRes)
        tvFeaturedPrice.text = getString(R.string.price_value, item.price)
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

    private fun showSteps(steps: List<Pair<Int, Int>>) {
        val container = binding.stepsContainer
        container.removeAllViews()
        steps.forEachIndexed { index, (title, desc) ->
            val row = ItemStepBinding.inflate(layoutInflater, container, false)
            row.tvNumber.text = getString(R.string.step_number, index + 1)
            row.tvStepTitle.setText(title)
            row.tvStepDesc.setText(desc)
            container.addView(row.root)
        }
    }

    override fun onResume() {
        super.onResume()
        val items = store.data.listings.filter { it.available }
        binding.featuredCard.visibility = if (items.isEmpty()) android.view.View.GONE else android.view.View.VISIBLE
        items.firstOrNull()?.let { setupFeatured(it) }
        setupGrid(items.drop(1).take(4))
        binding.drawerName.text = store.profileName
    }

    private fun setupBottomNav() = setupBottomNav(binding.bottomNav)

    private fun openListing(listing: Listing) = openProduct(listing.id)
}
