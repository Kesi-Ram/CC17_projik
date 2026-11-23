package com.example.cc17_projik

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cc17_projik.databinding.ActivityBinLocationsBinding
import com.example.cc17_projik.databinding.DialogBinBinding
import com.example.cc17_projik.databinding.ItemBinBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class BinLocationsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBinLocationsBinding
    private val store by lazy { LocalStore.get(this) }
    private val adapter = BinAdapter({ openGoogleMaps(it) }, { editBin(it) })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBinLocationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemInsets()
        setupBottomNav(binding.bottomNav)
        binding.btnBack.setOnClickListener { finish() }
        binding.rvBins.adapter = adapter
        binding.etSearch.doAfterTextChanged { refresh() }
        binding.btnAddBin.setOnClickListener { editBin(null) }
    }

    override fun onResume() { super.onResume(); refresh() }

    private fun refresh() {
        val query = binding.etSearch.text.toString().trim()
        val bins = store.data.bins.filter {
            query.isEmpty() || "${it.name} ${it.address} ${it.notes}".contains(query, ignoreCase = true)
        }
        adapter.submitList(bins)
        binding.tvEmpty.isVisible = bins.isEmpty()
        binding.tvEmpty.setText(if (store.data.bins.isEmpty()) R.string.no_bins else R.string.no_bin_matches)
    }

    private fun editBin(bin: BinLocation?) {
        val form = DialogBinBinding.inflate(layoutInflater)
        form.etName.setText(bin?.name)
        form.etAddress.setText(bin?.address)
        form.etMapsLink.setText(bin?.mapsUrl)
        form.etNotes.setText(bin?.notes)
        val builder = MaterialAlertDialogBuilder(this)
            .setTitle(if (bin == null) R.string.add_bin else R.string.edit_bin)
            .setView(form.root)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.save, null)
        if (bin != null) builder.setNeutralButton(R.string.delete) { _, _ ->
            MaterialAlertDialogBuilder(this).setTitle(R.string.delete_bin)
                .setMessage(bin.name).setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete) { _, _ ->
                    runCatching { store.deleteBin(bin.id) }.onSuccess { refresh() }.onFailure { showError(it) }
                }.show()
        }
        val dialog = builder.create()
        dialog.setOnShowListener {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = form.etName.text.toString().trim()
                val address = form.etAddress.text.toString().trim()
                val url = form.etMapsLink.text.toString().trim()
                when {
                    name.isBlank() -> form.etName.error = getString(R.string.bin_name_required)
                    address.isBlank() -> form.etAddress.error = getString(R.string.address_required)
                    url.isNotBlank() && !TradeRules.isGoogleMapsUrl(url) -> form.etMapsLink.error = getString(R.string.invalid_maps_link)
                    else -> runCatching {
                        store.saveBin(BinLocation(bin?.id ?: TradeRules.newId(), name, address, url, form.etNotes.text.toString()))
                    }.onSuccess { refresh(); dialog.dismiss() }.onFailure { showError(it) }
                }
            }
        }
        dialog.show()
    }
}

private class BinAdapter(
    private val onOpen: (BinLocation) -> Unit,
    private val onEdit: (BinLocation) -> Unit
) : ListAdapter<BinLocation, BinAdapter.Holder>(object : DiffUtil.ItemCallback<BinLocation>() {
    override fun areItemsTheSame(old: BinLocation, new: BinLocation) = old.id == new.id
    override fun areContentsTheSame(old: BinLocation, new: BinLocation) = old == new
}) {
    class Holder(val binding: ItemBinBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemBinBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val bin = getItem(position)
        with(holder.binding) {
            tvName.text = bin.name
            tvAddress.text = bin.address
            tvNotes.text = bin.notes
            tvNotes.isVisible = bin.notes.isNotBlank()
            root.setOnClickListener { onOpen(bin) }
            btnOpenMaps.setOnClickListener { onOpen(bin) }
            btnEdit.setOnClickListener { onEdit(bin) }
        }
    }
}
