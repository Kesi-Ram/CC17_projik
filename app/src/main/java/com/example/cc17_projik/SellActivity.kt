package com.example.cc17_projik

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.MaterialAutoCompleteTextView

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
            val checkedId = findViewById<ChipGroup>(R.id.chipGroupCondition).checkedChipId
            val condition = if (checkedId == View.NO_ID) null
            else findViewById<Chip>(checkedId).text.toString()
            Toast.makeText(this, "Condition: $condition", Toast.LENGTH_SHORT).show()
            // TODO: validate and save
        }

        findViewById<MaterialButton>(R.id.btnDraft).setOnClickListener {
            // TODO: save draft
        }

        findViewById<View>(R.id.btnAddPhotos).setOnClickListener {
            // TODO: launch photo picker
        }
    }
}