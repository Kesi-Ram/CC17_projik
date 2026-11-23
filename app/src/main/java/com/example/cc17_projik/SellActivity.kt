package com.example.cc17_projik

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.cc17_projik.databinding.ActivitySellBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject

class SellActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySellBinding
    private val store by lazy { LocalStore.get(this) }
    private val drafts by lazy { getSharedPreferences("listing_drafts", MODE_PRIVATE) }
    private var photoUri: String? = null
    private var published = false
    private val photoPicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) runCatching {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            photoUri = uri.toString()
            binding.imgPhotoPreview.showItemPhoto(photoUri)
        }.onFailure { showError(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState?.getBoolean("published") == true) { finish(); return }
        enableEdgeToEdge()
        binding = ActivitySellBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemInsets()
        binding.actvCategory.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1,
            Category.entries.map { it.label }))
        binding.btnBack.setOnClickListener { finish() }
        binding.btnCancel.setOnClickListener { finish() }
        binding.btnPublish.setOnClickListener { publishListing() }
        binding.btnAddPhotos.setOnClickListener { photoPicker.launch(arrayOf("image/*")) }
        binding.btnDraft.setOnClickListener { saveDraft() }
        if (savedInstanceState == null) restoreDraft()
        else photoUri = savedInstanceState.getString("photo_uri")
        binding.imgPhotoPreview.showItemPhoto(photoUri)
    }

    private fun selectedCondition(): Condition? = when (binding.chipGroupCondition.checkedChipId) {
        R.id.conditionNew -> Condition.NEW
        R.id.conditionGood -> Condition.GOOD
        R.id.conditionUsed -> Condition.USED
        R.id.conditionRepair -> Condition.NEEDS_REPAIR
        else -> null
    }

    private fun publishListing() {
        if (published) return
        val title = binding.etTitle.text.toString().trim()
        val price = binding.etPrice.text.toString().toIntOrNull()
        val description = binding.etNotes.text.toString().trim()
        val category = Category.entries.find { it.label == binding.actvCategory.text.toString() }
        val condition = selectedCondition()
        when {
            title.isBlank() -> binding.etTitle.error = getString(R.string.title_required)
            price == null || price <= 0 -> binding.etPrice.error = getString(R.string.price_required)
            description.isBlank() -> binding.etNotes.error = getString(R.string.description_required)
            category == null -> binding.actvCategory.error = getString(R.string.category_required)
            condition == null -> Toast.makeText(this, R.string.condition_required, Toast.LENGTH_SHORT).show()
            else -> runCatching {
                store.addListing(Listing(id = 0, title = title, price = price, condition = condition,
                    category = category, description = description, location = binding.etLocation.text.toString().trim(),
                    imageUri = photoUri))
            }.onSuccess {
                published = true
                binding.btnPublish.isEnabled = false
                drafts.edit { remove(store.profileId) }
                MaterialAlertDialogBuilder(this).setTitle(R.string.item_posted)
                    .setMessage(R.string.item_posted_message).setCancelable(false)
                    .setPositiveButton(R.string.view_market) { _, _ ->
                        startActivity(Intent(this, MarketActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
                        finish()
                    }.setNegativeButton(android.R.string.ok) { _, _ -> finish() }.show()
            }.onFailure { showError(it) }
        }
    }

    private fun saveDraft() {
        val value = JSONObject().apply {
            put("title", binding.etTitle.text.toString())
            put("price", binding.etPrice.text.toString())
            put("description", binding.etNotes.text.toString())
            put("location", binding.etLocation.text.toString())
            put("category", binding.actvCategory.text.toString())
            put("condition", selectedCondition()?.name.orEmpty())
            put("photo", photoUri.orEmpty())
        }
        if (drafts.commitChanges { putString(store.profileId, value.toString()) }) {
            Toast.makeText(this, R.string.draft_saved, Toast.LENGTH_SHORT).show()
        } else showError(IllegalStateException(getString(R.string.save_failed)))
    }

    private fun restoreDraft() {
        val raw = drafts.getString(store.profileId, null) ?: return
        runCatching {
            val value = JSONObject(raw)
            binding.etTitle.setText(value.optString("title"))
            binding.etPrice.setText(value.optString("price"))
            binding.etNotes.setText(value.optString("description"))
            binding.etLocation.setText(value.optString("location"))
            binding.actvCategory.setText(value.optString("category"), false)
            binding.chipGroupCondition.check(when (value.optString("condition")) {
                Condition.NEW.name -> R.id.conditionNew
                Condition.GOOD.name -> R.id.conditionGood
                Condition.USED.name -> R.id.conditionUsed
                Condition.NEEDS_REPAIR.name -> R.id.conditionRepair
                else -> View.NO_ID
            })
            photoUri = value.optString("photo").takeIf { it.isNotBlank() }
        }.onFailure { showError(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("published", published)
        outState.putString("photo_uri", photoUri)
        super.onSaveInstanceState(outState)
    }
}
