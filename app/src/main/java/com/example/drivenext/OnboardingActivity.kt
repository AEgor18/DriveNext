package com.example.drivenext

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class OnboardingActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var titleText: TextView
    private lateinit var subtitleText: TextView
    private lateinit var nextButton: Button
    private lateinit var skipTopText: TextView
    private lateinit var dots: Array<Button>

    private val images = arrayOf(
        R.drawable.onboarding1,
        R.drawable.onboarding2,
        R.drawable.onboarding3
    )

    private val titles = arrayOf(
        R.string.onboarding1,
        R.string.onboarding2,
        R.string.onboarding3
    )

    private val taglines = arrayOf(
        R.string.onboarding1_tagline,
        R.string.onboarding2_tagline,
        R.string.onboarding3_tagline
    )

    private var index = 0
    private val STATE_INDEX = "onboarding_index"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding)

        imageView = findViewById(R.id.imageView5)
        titleText = findViewById(R.id.textView6)
        subtitleText = findViewById(R.id.textView7)
        nextButton = findViewById(R.id.button2)
        skipTopText = findViewById(R.id.textView5)

        dots = arrayOf(
            findViewById(R.id.button3),
            findViewById(R.id.button6),
            findViewById(R.id.button8)
        )

        if (savedInstanceState != null) {
            index = savedInstanceState.getInt(STATE_INDEX, 0)
        }

        updateUi()

        nextButton.setOnClickListener {
            if (index < images.size - 1) {
                index++
                updateUi()
            } else {
                finish()
            }
        }

        skipTopText.setOnClickListener { finish() }

        dots.forEachIndexed { i, btn ->
            btn.setOnClickListener {
                index = i
                updateUi()
            }
        }
    }

    private fun updateUi() {
        imageView.setImageResource(images[index])
        titleText.setText(titles[index])
        subtitleText.setText(taglines[index])

        nextButton.setText(
            if (index == images.size - 1) R.string.go
            else R.string.continue1
        )

        dots.forEachIndexed { i, btn ->
            val params = btn.layoutParams
            if (i == index) {
                params.width = 40.dp
                btn.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.primary)
            } else {
                params.width = 25.dp
                btn.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.carousel_button)
            }
            btn.layoutParams = params
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_INDEX, index)
    }

    private val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()
}
