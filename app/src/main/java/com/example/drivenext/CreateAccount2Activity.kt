package com.example.drivenext

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CreateAccount2Activity : AppCompatActivity() {

    private lateinit var backImage: ImageView
    private lateinit var surnameInputLayout: TextInputLayout
    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var patronymicInputLayout: TextInputLayout
    private lateinit var dobInputLayout: TextInputLayout
    private lateinit var surnameEditText: TextInputEditText
    private lateinit var nameEditText: TextInputEditText
    private lateinit var patronymicEditText: TextInputEditText
    private lateinit var dobEditText: TextInputEditText
    private lateinit var radioGroupSex: RadioGroup
    private lateinit var continueButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_account2)

        // Инициализация
        backImage = findViewById(R.id.imageView4)
        surnameInputLayout = findViewById(R.id.surnameInputLayout)
        nameInputLayout = findViewById(R.id.nameInputLayout)
        patronymicInputLayout = findViewById(R.id.patronymicInputLayout)
        dobInputLayout = findViewById(R.id.dobInputLayout)
        surnameEditText = findViewById(R.id.surnameEditText)
        nameEditText = findViewById(R.id.nameEditText)
        patronymicEditText = findViewById(R.id.patronymicEditText)
        dobEditText = findViewById(R.id.dobEditText)
        radioGroupSex = findViewById(R.id.radioGroupSex)
        continueButton = findViewById(R.id.button8)

        backImage.setOnClickListener {
            startActivity(Intent(this, CreateAccount1Activity::class.java))
            finish()
        }

        surnameEditText.setOnFocusChangeListener { _, _ -> surnameInputLayout.error = null }
        nameEditText.setOnFocusChangeListener { _, _ -> nameInputLayout.error = null }
        patronymicEditText.setOnFocusChangeListener { _, _ -> patronymicInputLayout.error = null }
        dobEditText.setOnFocusChangeListener { _, _ -> dobInputLayout.error = null }

        val letterFilter = InputFilter { source, _, _, _, _, _ ->
            if (source.matches(Regex("[a-zA-Zа-яА-ЯёЁ]+"))) source else ""
        }

        fun configureNameField(editText: TextInputEditText) {
            editText.inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS or
                    android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            editText.filters = arrayOf(letterFilter)
        }

        configureNameField(surnameEditText)
        configureNameField(nameEditText)
        configureNameField(patronymicEditText)

        radioGroupSex.setOnCheckedChangeListener { _, checkedId ->
            for (i in 0 until radioGroupSex.childCount) {
                val rb = radioGroupSex.getChildAt(i) as com.google.android.material.radiobutton.MaterialRadioButton
                val isChecked = rb.id == checkedId
                rb.setTextColor(
                    if (isChecked) resources.getColor(R.color.primary)
                    else resources.getColor(R.color.form_gray)
                )
                val buttonColor = if (isChecked) R.color.primary else R.color.form_gray
                rb.buttonTintList = android.content.res.ColorStateList.valueOf(resources.getColor(buttonColor))
            }
        }

        for (i in 0 until radioGroupSex.childCount) {
            val rb = radioGroupSex.getChildAt(i) as com.google.android.material.radiobutton.MaterialRadioButton
            val isChecked = rb.isChecked
            rb.setTextColor(
                if (isChecked) resources.getColor(R.color.primary)
                else resources.getColor(R.color.form_gray)
            )
            val buttonColor = if (isChecked) R.color.primary else R.color.form_gray
            rb.buttonTintList = android.content.res.ColorStateList.valueOf(resources.getColor(buttonColor))
        }

        dobEditText.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        dobEditText.filters = arrayOf(InputFilter.LengthFilter(10))

        dobEditText.addTextChangedListener(object : TextWatcher {
            private var current = ""
            private var isDeleting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                isDeleting = count > after
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s == null) return
                var digits = s.toString().replace("/", "")
                if (digits == current) return

                if (!isDeleting) {
                    val sb = StringBuilder()
                    for (i in digits.indices) {
                        sb.append(digits[i])
                        if ((i == 1 || i == 3) && i != digits.lastIndex) sb.append("/")
                    }
                    current = sb.toString().replace("/", "")
                    dobEditText.removeTextChangedListener(this)
                    dobEditText.setText(sb)
                    dobEditText.setSelection(sb.length)
                    dobEditText.addTextChangedListener(this)
                } else {
                    current = digits
                }
                dobInputLayout.error = null
            }
        })

        continueButton.setOnClickListener {
            if (validateForm()) {
                startActivity(Intent(this, CreateAccount3Activity::class.java))
            }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        val surname = surnameEditText.text.toString().trim()
        val name = nameEditText.text.toString().trim()
        val dob = dobEditText.text.toString().trim()
        val selectedSexId = radioGroupSex.checkedRadioButtonId

        surnameInputLayout.error = null
        nameInputLayout.error = null
        patronymicInputLayout.error = null
        dobInputLayout.error = null

        if (surname.isEmpty()) {
            surnameInputLayout.error = "Введите фамилию"
            isValid = false
        }

        if (name.isEmpty()) {
            nameInputLayout.error = "Введите имя"
            isValid = false
        }

        if (dob.isEmpty()) {
            dobInputLayout.error = "Введите дату рождения"
            isValid = false
        } else if (!isValidDate(dob)) {
            dobInputLayout.error = "Введите корректную дату в формате DD/MM/YYYY"
            isValid = false
        }

        if (selectedSexId == -1) {
            Toast.makeText(this, "Выберите пол", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    private fun isValidDate(dateStr: String): Boolean {
        val parts = dateStr.split("/")
        if (parts.size != 3) return false

        val day = parts[0].toIntOrNull() ?: return false
        val month = parts[1].toIntOrNull() ?: return false
        val year = parts[2].toIntOrNull() ?: return false

        if (day !in 1..31) return false
        if (month !in 1..12) return false
        if (year !in 1900..2025) return false

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.isLenient = false
        return try {
            sdf.parse(dateStr)
            true
        } catch (e: ParseException) {
            false
        }
    }
}
