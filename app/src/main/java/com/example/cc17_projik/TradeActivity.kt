package com.example.cc17_projik

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cc17_projik.databinding.ActivityTradeBinding

class TradeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTradeBinding
    private lateinit var tradeAdapter: TradeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTradeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTradeRecyclerView()
    }

    private fun setupTradeRecyclerView() {

        tradeAdapter = TradeAdapter { item ->

            Toast.makeText(
                this,
                "Selected: ${item.name}",
                Toast.LENGTH_SHORT
            ).show()

        }

        binding.rvTradeItems.apply {
            layoutManager = LinearLayoutManager(this@TradeActivity)
            adapter = tradeAdapter
        }

        tradeAdapter.submitList(myTradeItems)
    }
}