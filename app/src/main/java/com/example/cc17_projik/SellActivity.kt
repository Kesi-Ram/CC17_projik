package com.example.cc17_projik

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat


class SellActivity : AppCompatActivity() {

    private val categories = listOf("Phones", "Laptops", "Tablets", "Accessories", "Other")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sell)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        findViewById<MaterialAutoCompleteTextView>(R.id.actvCategory).setAdapter(
            ArrayAdapter(this, android.R.layout.simple_list_item_1, categories)
        )

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<MaterialButton>(R.id.btnCancel).setOnClickListener { finish() }

        findViewById<MaterialButton>(R.id.btnPublish).setOnClickListener {
            publishListing()
        }

        findViewById<MaterialButton>(R.id.btnDraft).setOnClickListener {
            // TODO: save draft
        }

        findViewById<View>(R.id.btnAddPhotos).setOnClickListener {
            // TODO: launch photo picker
        }
    }

    // Validates the form, saves the listing, then shows the "posted" dialog.
    private fun publishListing() {
        // NOTE: change etTitle / etPrice if your layout uses different IDs.
        val title = findViewById<EditText>(R.id.etTitle).text.toString().trim()
        val priceText = findViewById<EditText>(R.id.etPrice).text.toString().trim()
        val categoryText = findViewById<MaterialAutoCompleteTextView>(R.id.actvCategory)
            .text.toString().trim()
        val checkedId = findViewById<ChipGroup>(R.id.chipGroupCondition).checkedChipId

        if (title.isEmpty()) {
            toast("Please enter a title")
            return
        }

        val price = priceText.toIntOrNull()
        if (price == null || price <= 0) {
            toast("Please enter a valid price")
            return
        }

        if (categoryText.isEmpty()) {
            toast("Please choose a category")
            return
        }

        if (checkedId == View.NO_ID) {
            toast("Please select the condition")
            return
        }

        val conditionText = findViewById<Chip>(checkedId).text.toString()

        val newId = (allListings.maxOfOrNull { it.id } ?: 0) + 1
        allListings.add(
            0,
            Listing(
                id = newId,
                title = title,
                price = price,
                condition = toCondition(conditionText),
                category = toCategory(categoryText),
                imageRes = R.drawable.ic_home
            )
        )
        notifyProductPosted(title)
        showPostedDialog()
    }

    // Converts the selected chip text into a Condition enum value.
    private fun toCondition(text: String): Condition = when {
        text.contains("new", ignoreCase = true) -> Condition.NEW
        text.contains("good", ignoreCase = true) -> Condition.GOOD
        else -> Condition.USED
    }

    // Converts the dropdown text into a Category enum value.
    // Anything without a matching category (like "Tablets") becomes Others.
    private fun toCategory(text: String): Category =
        Category.values().firstOrNull { it.label.equals(text, ignoreCase = true) }
            ?: Category.OTHERS

    // Shows a confirmation dialog after the item is posted.
    private fun showPostedDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Item posted!")
            .setMessage("Your item has been posted to the marketplace.")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()

                notifyProductPosted(
                    findViewById<EditText>(R.id.etTitle).text.toString().trim()
                )

                finish() // back to the previous screen
            }
            .setNegativeButton("View Market") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(this, MarketActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
            .show()
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            if (isGranted) {
                NotificationHelper.showProductPostedNotification(
                    this,
                    pendingProductTitle
                )
            }
        }
    /**
     * Checks notification permission before displaying
     * the product posting notification.
     */
    private fun notifyProductPosted(productTitle: String) {

        if (Build.VERSION.SDK_INT >= 33) {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                pendingProductTitle = productTitle

                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )

                return
            }
        }

        NotificationHelper.showProductPostedNotification(
            this,
            productTitle
        )
    }

    private var pendingProductTitle = ""

    private fun toast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}