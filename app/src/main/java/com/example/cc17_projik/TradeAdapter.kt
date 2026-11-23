package com.example.cc17_projik

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cc17_projik.databinding.ItemTradeBinding

class TradeAdapter(private val onClick: (TradeRow) -> Unit) :
    ListAdapter<TradeRow, TradeAdapter.ViewHolder>(Diff) {
    class ViewHolder(val binding: ItemTradeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemTradeBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val row = getItem(position)
        with(holder.binding) {
            imgItem.showItemPhoto(row.proposal.imageUri)
            tvItemName.text = root.context.getString(R.string.trade_pair, row.proposal.offeredTitle, row.listingTitle)
            tvTradeStatus.text = root.context.getString(
                if (row.incoming) R.string.incoming_status else R.string.outgoing_status,
                row.proposal.status.label, row.proposal.senderName)
            root.setOnClickListener { onClick(row) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<TradeRow>() {
        override fun areItemsTheSame(a: TradeRow, b: TradeRow) = a.proposal.id == b.proposal.id
        override fun areContentsTheSame(a: TradeRow, b: TradeRow) = a == b
    }
}
