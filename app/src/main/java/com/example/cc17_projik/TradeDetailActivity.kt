package com.example.cc17_projik

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.cc17_projik.databinding.ActivityTradeDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.DateFormat
import java.util.Date

class TradeDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTradeDetailBinding
    private val store by lazy { LocalStore.get(this) }
    private val proposalId get() = intent.getStringExtra(EXTRA_PROPOSAL_ID).orEmpty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTradeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemInsets()
        binding.btnBack.setOnClickListener { finish() }
        binding.btnAccept.setOnClickListener { confirm(TradeStatus.ACCEPTED) }
        binding.btnDecline.setOnClickListener { confirm(TradeStatus.DECLINED) }
        binding.btnCancel.setOnClickListener { confirm(TradeStatus.CANCELLED) }
    }

    override fun onResume() { super.onResume(); render() }

    private fun render() {
        val proposal = store.data.proposals.find { it.id == proposalId }
        val listing = store.data.listings.find { it.id == proposal?.listingId }
        if (proposal == null || listing == null ||
            (proposal.senderId != store.profileId && listing.sellerId != store.profileId)) {
            showError(IllegalArgumentException(getString(R.string.proposal_missing)))
            finish()
            return
        }
        with(binding) {
            tvStatus.text = getString(R.string.status_value, proposal.status.label)
            tvTarget.text = getString(R.string.trade_for_value, listing.title)
            btnViewProduct.setOnClickListener { openProduct(listing.id) }
            imgOffer.showItemPhoto(proposal.imageUri)
            tvOfferTitle.text = proposal.offeredTitle
            tvCondition.bindCondition(proposal.offeredCondition)
            tvDescription.text = proposal.offeredDescription
            tvSender.text = getString(R.string.offered_by_value, proposal.senderName)
            tvDate.text = getString(R.string.proposal_date_value, DateFormat.getDateTimeInstance().format(Date(proposal.createdAt)))
            val pending = proposal.status == TradeStatus.PENDING
            val owner = listing.sellerId == store.profileId
            btnAccept.isVisible = pending && owner && listing.available
            btnDecline.isVisible = pending && owner && listing.available
            btnCancel.isVisible = pending && proposal.senderId == store.profileId
        }
    }

    private fun confirm(status: TradeStatus) {
        MaterialAlertDialogBuilder(this)
            .setTitle(when (status) {
                TradeStatus.ACCEPTED -> R.string.accept_trade
                TradeStatus.DECLINED -> R.string.decline_trade
                else -> R.string.cancel_offer
            })
            .setMessage(if (status == TradeStatus.ACCEPTED) R.string.accept_trade_message else R.string.change_trade_message)
            .setNegativeButton(R.string.keep_offer, null)
            .setPositiveButton(R.string.confirm) { _, _ ->
                runCatching { store.respond(proposalId, status) }
                    .onSuccess { render() }.onFailure { showError(it) }
            }.show()
    }

    companion object { const val EXTRA_PROPOSAL_ID = "proposal_id" }
}
