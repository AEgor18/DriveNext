package com.example.drivenext

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SignInActivity : AppCompatActivity() {
    private lateinit var signUpText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin)

        signUpText = findViewById<TextView>(R.id.textView17)

        signUpText.setOnClickListener {
            startActivity(Intent(this, CreateAccount1Activity::class.java))
        }
    }
}