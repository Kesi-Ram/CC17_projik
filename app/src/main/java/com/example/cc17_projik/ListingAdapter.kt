package com.example.cc17_projik

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cc17_projik.databinding.ItemListingBinding

class ListingAdapter(
    private val onClick: (Listing) -> Unit
) : ListAdapter<Listing, ListingAdapter.ViewHolder>(Diff) {

    class ViewHolder(val binding: ItemListingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            imgListing.setImageResource(item.imageRes)
            tvPrice.text = "$${item.price}"
            tvTitle.text = item.title
            tvCondition.bindCondition(item.condition)
            root.setOnClickListener { onClick(item) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<Listing>() {
        override fun areItemsTheSame(a: Listing, b: Listing) = a.id == b.id
        override fun areContentsTheSame(a: Listing, b: Listing) = a == b
    }
}