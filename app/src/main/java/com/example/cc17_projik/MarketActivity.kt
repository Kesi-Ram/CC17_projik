package com.example.cc17_projik

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cc17_projik.databinding.ActivityMarketBinding
import com.google.android.material.chip.Chip

class MarketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMarketBinding

    private val listingAdapter = ListingAdapter { listing -> openMarketListing(listing)
    }
    private val marketListings = listOf(
        Listing(1, "Macbook Pro", 430, Condition.GOOD, R.drawable.ic_home),
        Listing(2, "Iphone 13 Pro Max", 320, Condition.USED, R.drawable.ic_home),
        Listing(3, "Iphone 11 64gb", 150, Condition.GOOD, R.drawable.ic_home),
                Listing(2, "Iphone 13 Pro Max", 320, Condition.USED, R.drawable.ic_home),
        Listing(2, "Samsung Fold 8", 500, Condition.NEW, R.drawable.ic_home),
        Listing(2, "Iphone duo 512gb", 1000, Condition.NEW, R.drawable.ic_home),
        Listing(2, "JBL speaker", 200, Condition.USED, R.drawable.ic_home),
        Listing(2, "Washing Machine", 70, Condition.USED, R.drawable.ic_home),
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMarketBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupChips()
        setupBottomNav()

        binding.btnMenu.setOnClickListener { toast("Menu: open the drawer here") }
        binding.btnBell.setOnClickListener { toast("Notifications") }

        // Open drawer when menu button is tapped
        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
        }

        // Log out closes the drawer (add real logic later)
        binding.drawerLogout.setOnClickListener {
            binding.drawerLayout.closeDrawers()
        }

        binding.rvMarket.apply {
            layoutManager = GridLayoutManager(this@MarketActivity, 2)
            adapter = listingAdapter
        }

        listingAdapter.submitList(marketListings)
    }



    private fun setupChips() {
        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val chip = group.findViewById<Chip>(checkedIds.first())
            toast("Category: ${chip.text}")   // filtering comes in Part B
        }
    }

    private fun setupBottomNav() {
        binding.navHome.setOnClickListener { finish() }  // back to the dashboard
        binding.navMarket.setOnClickListener { /* already here */ }
        binding.navSell.setOnClickListener { toast("Sell screen goes here") }
        binding.navTrade.setOnClickListener { toast("Trade screen goes here") }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    private fun openMarketListing(listing: Listing) {
        Toast.makeText(
            this,
            "You selected ${listing.title} in Market",
            Toast.LENGTH_SHORT
        ).show()
    }
}