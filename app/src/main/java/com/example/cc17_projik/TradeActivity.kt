package com.example.cc17_projik

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.cc17_projik.databinding.ActivityTradeBinding

class TradeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTradeBinding
    private val store by lazy { LocalStore.get(this) }
    private var incoming = true
    private val tradeAdapter = TradeAdapter { row ->
        startActivity(Intent(this, TradeDetailActivity::class.java)
            .putExtra(TradeDetailActivity.EXTRA_PROPOSAL_ID, row.proposal.id))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTradeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemInsets()
        setupBottomNav(binding.bottomNav)
        binding.btnBack.setOnClickListener { finish() }
        binding.rvTradeItems.adapter = tradeAdapter
        incoming = savedInstanceState?.getBoolean("incoming") ?: !intent.getBooleanExtra(EXTRA_SHOW_SENT, false)
        binding.tradeTabs.check(if (incoming) R.id.btnReceived else R.id.btnSent)
        binding.tradeTabs.addOnButtonCheckedListener { _, id, checked ->
            if (checked) { incoming = id == R.id.btnReceived; refresh() }
        }
        binding.tvLocalNotice.text = getString(R.string.local_profile_notice, store.profileName)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        incoming = !intent.getBooleanExtra(EXTRA_SHOW_SENT, false)
        binding.tradeTabs.check(if (incoming) R.id.btnReceived else R.id.btnSent)
        refresh()
    }

    override fun onResume() { super.onResume(); refresh() }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("incoming", incoming)
        super.onSaveInstanceState(outState)
    }

    private fun refresh() {
        val listings = store.data.listings.associateBy { it.id }
        val rows = store.data.proposals.mapNotNull { proposal ->
            val listing = listings[proposal.listingId] ?: return@mapNotNull null
            val belongs = if (incoming) listing.sellerId == store.profileId else proposal.senderId == store.profileId
            if (belongs) TradeRow(proposal, listing.title, incoming) else null
        }
        tradeAdapter.submitList(rows)
        binding.tvEmpty.isVisible = rows.isEmpty()
        binding.tvEmpty.setText(if (incoming) R.string.no_received_offers else R.string.no_sent_offers)
    }

    companion object { const val EXTRA_SHOW_SENT = "show_sent" }
}
