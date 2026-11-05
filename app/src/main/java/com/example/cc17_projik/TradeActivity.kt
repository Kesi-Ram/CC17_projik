package com.example.cc17_projik

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cc17_projik.databinding.ActivityTradeBinding

class TradeActivity : AppCompatActivity() {

    // ViewBinding gives access to the views in activity_trade.xml.
    private lateinit var binding: ActivityTradeBinding

    // Adapter that fills each card (item_trade.xml). Clicking a card calls openTrade().
    private val tradeAdapter = TradeAdapter { item -> openTrade(item) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTradeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Keep content clear of the status bar and navigation bar.
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        // Back arrow returns to the previous screen.
        binding.btnBack.setOnClickListener { finish() }

        // Connect the adapter to the list and give it the items to show.
        binding.rvTradeItems.adapter = tradeAdapter
        tradeAdapter.submitList(myTradeItems)
    }

    private fun openTrade(item: TradeItem) {
        // TODO: open the proposed trades screen for this item
        val message = if (item.proposalCount > 0) {
            "Open proposed trades for ${item.name}"
        } else {
            "${item.name} has no proposed trades yet"
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}