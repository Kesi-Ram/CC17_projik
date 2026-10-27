package com.example.cc17_projik

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


            val emailInput = findViewById<EditText>(R.id.editTextTextEmailAddress)
            val passwordInput = findViewById<EditText>(R.id.editTextTextPassword)
            val loginButton = findViewById<Button>(R.id.button2)

            loginButton.setOnClickListener {
                val email = emailInput.text.toString()
                val password = passwordInput.text.toString()

                // Perform login validation here
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Dashboard::class.java)
                    startActivity(intent)
                }
            }
        }
    }
