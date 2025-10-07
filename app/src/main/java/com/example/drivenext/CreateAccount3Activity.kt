package com.example.drivenext

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CreateAccount3Activity : AppCompatActivity() {

    private lateinit var backImage: ImageView
    private lateinit var licenseInputLayout: TextInputLayout
    private lateinit var licenseEditText: TextInputEditText
    private lateinit var dateInputLayout: TextInputLayout
    private lateinit var dateEditText: TextInputEditText
    private lateinit var uploadLicenseButton: ImageView
    private lateinit var uploadPassButton: ImageView
    private lateinit var plusButton: ImageView
    private lateinit var continueButton: Button

    private var licensePhotoUri: Uri? = null
    private var passPhotoUri: Uri? = null
    private var profilePhotoUri: Uri? = null

    private var currentImageButton: ImageView? = null

    private val getImageFromGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri = data?.data
            currentImageButton?.setImageURI(imageUri)
            when (currentImageButton) {
                uploadLicenseButton -> licensePhotoUri = imageUri
                uploadPassButton -> passPhotoUri = imageUri
                plusButton -> profilePhotoUri = imageUri
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_account3)

        backImage = findViewById(R.id.imageView4)
        licenseInputLayout = findViewById(R.id.licenseInputLayout)
        licenseEditText = findViewById(R.id.licenseEditText)
        dateInputLayout = findViewById(R.id.DateInputLayout)
        dateEditText = findViewById(R.id.dateEditText)
        uploadLicenseButton = findViewById(R.id.imageView8)
        uploadPassButton = findViewById(R.id.imageView9)
        plusButton = findViewById(R.id.imageView7)
        continueButton = findViewById(R.id.button8)

        backImage.setOnClickListener { finish() }

        licenseEditText.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        licenseEditText.filters = arrayOf(InputFilter.LengthFilter(10))
        licenseEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                licenseInputLayout.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        dateEditText.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        dateEditText.filters = arrayOf(InputFilter.LengthFilter(10))
        dateEditText.addTextChangedListener(object : TextWatcher {
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
                    dateEditText.removeTextChangedListener(this)
                    dateEditText.setText(sb)
                    dateEditText.setSelection(sb.length)
                    dateEditText.addTextChangedListener(this)
                } else {
                    current = digits
                }
                dateInputLayout.error = null
            }
        })

        val photoClickListener = { imageButton: ImageView ->
            currentImageButton = imageButton
            openImagePicker()
        }

        uploadLicenseButton.setOnClickListener { photoClickListener(uploadLicenseButton) }
        uploadPassButton.setOnClickListener { photoClickListener(uploadPassButton) }
        plusButton.setOnClickListener { photoClickListener(plusButton) }

        continueButton.setOnClickListener {
            if (validateForm()) {
                startActivity(Intent(this, SuccessActivity::class.java))
            }
        }
    }

    private fun openImagePicker() {
        val options = arrayOf<CharSequence>("Камера", "Галерея")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Выберите источник изображения")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
            }
        }
        builder.show()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        getImageFromGallery.launch(intent)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getImageFromGallery.launch(intent)
    }

    private fun validateForm(): Boolean {
        var isValid = true
        val license = licenseEditText.text.toString().trim()
        val date = dateEditText.text.toString().trim()

        licenseInputLayout.error = null
        dateInputLayout.error = null

        if (license.length != 10) {
            licenseInputLayout.error = "Номер должен содержать ровно 10 цифр"
            isValid = false
        }

        if (date.isEmpty()) {
            dateInputLayout.error = "Введите дату"
            isValid = false
        } else if (!isValidDate(date)) {
            dateInputLayout.error = "Введите корректную дату в формате DD/MM/YYYY"
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
