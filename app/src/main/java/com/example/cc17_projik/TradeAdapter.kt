package com.example.cc17_projik

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cc17_projik.databinding.ItemTradeBinding

class TradeAdapter(
    private val onClick: (TradeItem) -> Unit
) : ListAdapter<TradeItem, TradeAdapter.ViewHolder>(Diff) {

    class ViewHolder(val binding: ItemTradeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTradeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            imgItem.setImageResource(item.imageRes)
            tvItemName.text = item.name

            // Green link when there are offers, gray text when there are none.
            if (item.proposalCount > 0) {
                tvTradeStatus.text = "View proposed trade >"
                tvTradeStatus.setTextColor(
                    ContextCompat.getColor(root.context, R.color.brand_green)
                )
            } else {
                tvTradeStatus.text = "No proposed trades yet"
                tvTradeStatus.setTextColor(
                    ContextCompat.getColor(root.context, R.color.gray_text)
                )
            }

            root.setOnClickListener { onClick(item) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<TradeItem>() {
        override fun areItemsTheSame(a: TradeItem, b: TradeItem) = a.id == b.id
        override fun areContentsTheSame(a: TradeItem, b: TradeItem) = a == b
    }
}