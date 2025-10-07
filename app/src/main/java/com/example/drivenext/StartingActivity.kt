package com.example.drivenext

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class StartingActivity : AppCompatActivity() {

    private lateinit var signInButton: Button
    private lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.starting)

        signInButton = findViewById(R.id.button4)
        signUpButton = findViewById(R.id.button5)

        signInButton.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        signUpButton.setOnClickListener {
            startActivity(Intent(this, CreateAccount1Activity::class.java))
        }
    }
}
