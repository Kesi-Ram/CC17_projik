package com.example.cc17_projik

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.cc17_projik.databinding.ActivityProposeTradeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProposeTradeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProposeTradeBinding
    private val store by lazy { LocalStore.get(this) }
    private var photoUri: String? = null
    private var published = false
    private val photoPicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                photoUri = uri.toString()
                binding.imgOffer.showItemPhoto(photoUri)
            }.onFailure { showError(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState?.getBoolean("published") == true) { finish(); return }
        enableEdgeToEdge()
        binding = ActivityProposeTradeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemInsets()
        photoUri = savedInstanceState?.getString("photo_uri")
        binding.imgOffer.showItemPhoto(photoUri)
        val listingId = intent.getIntExtra(ProductDetailActivity.EXTRA_LISTING_ID, -1)
        val item = store.data.listings.find { it.id == listingId }
        if (item == null) {
            showError(IllegalArgumentException(getString(R.string.listing_missing)))
            finish()
            return
        }
        binding.tvTarget.text = getString(R.string.trade_for_value, item.title)
        binding.actvCondition.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, Condition.entries.map { it.label }))
        binding.btnBack.setOnClickListener { finish() }
        binding.btnPhoto.setOnClickListener { photoPicker.launch(arrayOf("image/*")) }
        binding.btnPublish.setOnClickListener {
            if (published) return@setOnClickListener
            val title = binding.etOfferTitle.text.toString().trim()
            val description = binding.etOfferDescription.text.toString().trim()
            val condition = Condition.entries.find { it.label == binding.actvCondition.text.toString() }
            when {
                title.isBlank() -> binding.etOfferTitle.error = getString(R.string.title_required)
                description.isBlank() -> binding.etOfferDescription.error = getString(R.string.description_required)
                condition == null -> binding.actvCondition.error = getString(R.string.condition_required)
                else -> runCatching {
                    store.propose(listingId, title, description, condition, photoUri)
                    published = true
                    binding.btnPublish.isEnabled = false
                    MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.proposal_saved)
                        .setMessage(R.string.proposal_saved_message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.view_trades) { _, _ ->
                            startActivity(Intent(this, TradeActivity::class.java)
                                .putExtra(TradeActivity.EXTRA_SHOW_SENT, true)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
                            finish()
                        }.show()
                }.onFailure { showError(it) }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("published", published)
        outState.putString("photo_uri", photoUri)
        super.onSaveInstanceState(outState)
    }
}
