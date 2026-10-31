package com.example.cc17_projik

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cc17_projik.databinding.ActivityMarketBinding
import com.google.android.material.chip.Chip

class MarketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMarketBinding

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
}