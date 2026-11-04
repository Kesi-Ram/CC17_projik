
package com.example.cc17_projik

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cc17_projik.databinding.ActivityMarketBinding

class MarketActivity : AppCompatActivity() {

    // ViewBinding provides access to views in activity_market.xml.
    private lateinit var binding: ActivityMarketBinding

    // Adapter responsible for displaying individual market listings.
    // Clicking a listing calls openMarketListing().
    private val listingAdapter = ListingAdapter { listing ->
        openMarketListing(listing)
    }

    // Sample market listing data.
    // Each listing has a unique ID, product information,
    // condition, category, and drawable image resource.
    private val marketListings = listOf(

        Listing(
            1,
            "Macbook Pro",
            430,
            Condition.GOOD,
            Category.LAPTOPS,
            R.drawable.ic_home
        ),

        Listing(
            2,
            "Iphone 13 Pro Max",
            320,
            Condition.USED,
            Category.PHONES,
            R.drawable.ic_home
        ),

        Listing(
            3,
            "Iphone 11 64gb",
            150,
            Condition.GOOD,
            Category.PHONES,
            R.drawable.ic_home
        ),

        Listing(
            4,
            "Samsung Fold 8",
            500,
            Condition.NEW,
            Category.PHONES,
            R.drawable.ic_home
        ),

        Listing(
            5,
            "Iphone duo 512gb",
            1000,
            Condition.NEW,
            Category.PHONES,
            R.drawable.ic_home
        ),

        Listing(
            6,
            "JBL speaker",
            200,
            Condition.USED,
            Category.AUDIO,
            R.drawable.ic_home
        ),

        Listing(
            7,
            "Washing Machine",
            70,
            Condition.USED,
            Category.OTHERS,
            R.drawable.ic_home
        )
    )

    // Called when the Market screen is created.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display.
        enableEdgeToEdge()

        // Initialize ViewBinding and set the screen layout.
        binding = ActivityMarketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply system bar insets to the main content.
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        // Initialize the RecyclerView before loading listings.
        setupRecyclerView()

        // Initialize category filtering.
        setupFilters()

        // Initialize bottom navigation.
        setupBottomNav()

        // Open the navigation drawer.
        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        // Display notification message.
        binding.btnBell.setOnClickListener {
            toast("Notifications")
        }

        // log out
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
    }

    // ---------------------------------------------------------
    // RECYCLERVIEW SETUP
    // ---------------------------------------------------------

    // Configures the market listings RecyclerView.
    // Uses a GridLayoutManager to display two products per row.
    private fun setupRecyclerView() {

        binding.rvMarket.apply {

            layoutManager = GridLayoutManager(
                this@MarketActivity,
                2
            )

            adapter = listingAdapter
        }

        // Initially display all market listings.
        listingAdapter.submitList(marketListings)
    }

    // ---------------------------------------------------------
    // CATEGORY FILTERING
    // ---------------------------------------------------------

    // Listens for changes in the selected category chip.
    // Filters the original listing collection and updates
    // the RecyclerView with matching products.
    private fun setupFilters() {

        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->

            // Identify which category the user selected.
            val selectedCategory = when (checkedIds.firstOrNull()) {

                R.id.Others -> Category.OTHERS

                R.id.Phones -> Category.PHONES

                R.id.PcParts -> Category.PC_PARTS

                R.id.Laptops -> Category.LAPTOPS

                R.id.Audio -> Category.AUDIO

                else -> return@setOnCheckedStateChangeListener
            }

            // Filter the original listings based on the selected category.
            val filteredListings = marketListings.filter { listing ->
                listing.category == selectedCategory
            }

            // Update the RecyclerView with the filtered results.
            listingAdapter.submitList(filteredListings)
        }
    }

    // ---------------------------------------------------------
    // BOTTOM NAVIGATION
    // ---------------------------------------------------------

    // Handles navigation button clicks at the bottom of the screen.
    private fun setupBottomNav() {

        // Return to the previous screen.
        binding.navHome.setOnClickListener {
            startActivity(Intent(this, Dashboard::class.java))
        }

        // Market is already the current screen.
        binding.navMarket.setOnClickListener {
            // No action needed.
        }

        // Placeholder for Sell screen navigation.
        binding.navSell.setOnClickListener {
            startActivity(Intent(this, SellActivity::class.java))
        }

        // Placeholder for Trade screen navigation.
        binding.navTrade.setOnClickListener {
            toast("Trade screen goes here")
        }
    }

    // ---------------------------------------------------------
    // LISTING CLICK HANDLER
    // ---------------------------------------------------------

    // Called when a product card is clicked.
    // Receives the selected Listing object.
    private fun openMarketListing(listing: Listing) {

        Toast.makeText(
            this,
            "You selected ${listing.title} in Market",
            Toast.LENGTH_SHORT
        ).show()
    }

    // ---------------------------------------------------------
    // TOAST HELPER
    // ---------------------------------------------------------

    // Displays a short notification message.
    // This avoids repeating Toast.makeText() throughout the Activity.
    private fun toast(msg: String) {

        Toast.makeText(
            this,
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }
}
