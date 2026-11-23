package com.example.cc17_projik

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cc17_projik.databinding.ActivityMarketBinding

class MarketActivity : AppCompatActivity() {

    // ViewBinding provides access to views in activity_market.xml.
    private lateinit var binding: ActivityMarketBinding

    // Adapter responsible for displaying individual market listings.
    private val listingAdapter = ListingAdapter { listing ->
        openMarketListing(listing)
    }

    // The category currently selected in the chip group (null = show everything).
    private var selectedCategory: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityMarketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply system bar insets to the main content.
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        setupRecyclerView()
        setupFilters()
        binding.etSearch.doAfterTextChanged { refreshListings() }
        setupBottomNav()

        // Open the navigation drawer.
        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        // Display notification message.
        binding.btnBell.setOnClickListener {
            Toast.makeText(this, R.string.ui_notifications, Toast.LENGTH_SHORT).show()
        }

        // Log out
        binding.drawerLogout.setOnClickListener {
            binding.drawerLayout.closeDrawers()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }
    }

    // Reload the listings every time this screen comes back into view,
    // so newly posted items show up right away.
    override fun onResume() {
        super.onResume()
        refreshListings()
    }

    // ---------------------------------------------------------
    // RECYCLERVIEW SETUP
    // ---------------------------------------------------------

    private fun setupRecyclerView() {
        binding.rvMarket.apply {
            layoutManager = GridLayoutManager(this@MarketActivity, 2)
            adapter = listingAdapter
        }
        // The list itself is loaded in onResume() -> refreshListings().
    }

    // ---------------------------------------------------------
    // CATEGORY FILTERING
    // ---------------------------------------------------------

    private fun setupFilters() {
        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->

            selectedCategory = when (checkedIds.firstOrNull()) {
                R.id.Others -> Category.OTHERS
                R.id.Phones -> Category.PHONES
                R.id.PcParts -> Category.PC_PARTS
                R.id.Laptops -> Category.LAPTOPS
                R.id.Audio -> Category.AUDIO
                else -> null // nothing selected -> show all listings
            }

            refreshListings()
        }
    }

    // Reads the latest listings from the repository, applies the
    // selected category filter (if any), and updates the RecyclerView.
    private fun refreshListings() {
        val query = binding.etSearch.text.toString().trim()
        val all = LocalStore.get(this).data.listings.filter {
            it.available && (query.isEmpty() || it.title.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true))
        }
        val shown = selectedCategory?.let { category ->
            all.filter { it.category == category }
        } ?: all

        listingAdapter.submitList(shown)
    }

    // ---------------------------------------------------------
    // BOTTOM NAVIGATION
    // ---------------------------------------------------------

    private fun setupBottomNav() = setupBottomNav(binding.bottomNav)

    private fun openMarketListing(listing: Listing) = openProduct(listing.id)

}