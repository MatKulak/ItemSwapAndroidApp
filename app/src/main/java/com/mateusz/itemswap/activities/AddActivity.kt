package com.mateusz.itemswap.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.mateusz.itemswap.R
import com.mateusz.itemswap.data.advertisement.AddAdvertisementRequest
import com.mateusz.itemswap.data.advertisement.DetailedAdvertisementResponse
import com.mateusz.itemswap.enums.Condition
import com.mateusz.itemswap.helpers.PreferencesHelper
import com.mateusz.itemswap.network.APIAdvertisement
import com.mateusz.itemswap.network.APICategory
import com.mateusz.itemswap.others.Constants.ADVERTISEMENT_ADD_SUCCESS
import com.mateusz.itemswap.others.Constants.ADVERTISEMENT_UPDATE_SUCCESS
import com.mateusz.itemswap.others.Constants.CITY_VALIDATION_ERROR
import com.mateusz.itemswap.others.Constants.CONNECTION_ERROR
import com.mateusz.itemswap.others.Constants.DESCRIPTION_VALIDATION_ERROR
import com.mateusz.itemswap.others.Constants.IMAGE_VALIDATION_ERROR
import com.mateusz.itemswap.others.Constants.INVALID_FORM
import com.mateusz.itemswap.others.Constants.INVALID_PHONE_NUMBER
import com.mateusz.itemswap.others.Constants.POSTAL_CODE_VALIDATION_ERROR
import com.mateusz.itemswap.others.Constants.REQUIRED_FIELD
import com.mateusz.itemswap.others.Constants.SERVER_ERROR
import com.mateusz.itemswap.others.Constants.STREET_VALIDATION_ERROR
import com.mateusz.itemswap.others.Constants.TITLE_VALIDATION_ERROR
import com.mateusz.itemswap.utils.RetrofitClient
import com.mateusz.itemswap.utils.Utils.base64ToUri
import com.mateusz.itemswap.utils.Utils.getTextFieldStringValue
import com.mateusz.itemswap.utils.Utils.isTextFieldValid
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.source
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream
import java.util.UUID

class AddActivity : AppCompatActivity() {

    private lateinit var imageSelectionBox: LinearLayout
    private lateinit var addImageIcon: ImageView
    private lateinit var titleTextField: TextInputLayout
    private lateinit var categorySpinner: Spinner
    private lateinit var conditionSpinner: Spinner
    private lateinit var descriptionTextField: TextInputLayout
    private lateinit var cityTextField: TextInputLayout
    private lateinit var streetTextField: TextInputLayout
    private lateinit var postalCodeTextField: TextInputLayout
    private lateinit var phoneNumberTextField: TextInputLayout
    private lateinit var editTextView: TextView
    private lateinit var addTextView: TextView
    private lateinit var addButton: Button
    private lateinit var editButton: Button
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var apiAdvertisement: APIAdvertisement
    private lateinit var apiCategory: APICategory
    private var selectedImages: MutableList<Uri> = mutableListOf()
    private var edit: Boolean = false
    private var advertisementId: UUID? = null
    private var advertisementDetails: DetailedAdvertisementResponse? = null

    companion object {
        private const val REQUEST_CODE_SELECT_IMAGES = 1
        private const val REQUEST_CODE_READ_MEDIA_IMAGES = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        preferencesHelper = PreferencesHelper(this)
        apiAdvertisement = RetrofitClient.getService(APIAdvertisement::class.java, preferencesHelper)
        apiCategory = RetrofitClient.getService(APICategory::class.java, preferencesHelper)
        imageSelectionBox = findViewById(R.id.imageSelectionBox)
        addImageIcon = findViewById(R.id.addImageIcon)
        categorySpinner = findViewById(R.id.categorySpinner)
        conditionSpinner = findViewById(R.id.conditionSpinner)
        titleTextField = findViewById(R.id.titleTextField)
        descriptionTextField = findViewById(R.id.descriptionTextField)
        cityTextField = findViewById(R.id.cityTextField)
        streetTextField = findViewById(R.id.streetTextField)
        postalCodeTextField = findViewById(R.id.postalCodeTextField)
        phoneNumberTextField = findViewById(R.id.phoneNumberTextField)
        addButton = findViewById(R.id.addButton)
        editButton = findViewById(R.id.editButton)
        editTextView = findViewById(R.id.editTextView)
        addTextView = findViewById(R.id.addTextView)
        updateEditMode()
        determineActivityTitleAndButton()
        if (!edit) populateSpinnerCategories()
        if (!edit) populateSpinnerConditions()
        patchForm()

        imageSelectionBox.setOnClickListener {
            if (selectedImages.isNotEmpty()) {
                selectedImages.clear()
                resetToAddIcon()
            }
            checkPermissionsAndOpenGallery()
        }

        addButton.setOnClickListener {
            addAdvertisement()
        }

        editButton.setOnClickListener {
            addAdvertisement()
        }

        watchSimpleTextFieldChange(titleTextField, TITLE_VALIDATION_ERROR)
        watchSimpleTextFieldChange(descriptionTextField, DESCRIPTION_VALIDATION_ERROR)
        watchSimpleTextFieldChange(cityTextField, CITY_VALIDATION_ERROR)
        watchSimpleTextFieldChange(streetTextField, STREET_VALIDATION_ERROR)
        watchPhoneNumberChange()
        watchPostalCodeChange()
        watchPostalCodeChange()
    }

    private fun determineActivityTitleAndButton() {
        if (edit) {
            addTextView.visibility = View.GONE
            editTextView.visibility = View.VISIBLE
            addButton.visibility = View.GONE
            editButton.visibility = View.VISIBLE
        } else {
            addTextView.visibility = View.VISIBLE
            editTextView.visibility = View.GONE
            addButton.visibility = View.VISIBLE
            editButton.visibility = View.GONE
        }
    }

    private fun updateEditMode() {
        val id = intent.getStringExtra("advertisementId")
        if (id != null) {
            edit = true
            advertisementId = UUID.fromString(id)
        }
    }

    private fun patchForm() {
        if (edit) {
            getAdvertisementDetails(advertisementId!!)
        }
    }

    private fun getAdvertisementDetails(advertisementId: UUID) {
        apiAdvertisement.getAdvertisementById(advertisementId)
            .enqueue(object : Callback<DetailedAdvertisementResponse> {
                override fun onResponse(
                    call: Call<DetailedAdvertisementResponse>,
                    response: Response<DetailedAdvertisementResponse>
                ) {
                    if (response.isSuccessful) {
                        val detailedAdvertisementResponse = response.body()
                        if (detailedAdvertisementResponse != null) {
                            advertisementDetails = detailedAdvertisementResponse
                            patchTextFields()
                            loadFiles()
                        }
                        populateSpinnerConditions()
                        populateSpinnerCategories()
                    } else {
                        showToast(SERVER_ERROR)
                    }
                }

                override fun onFailure(call: Call<DetailedAdvertisementResponse>, t: Throwable) {
                    showToast(CONNECTION_ERROR)
                }
            })
    }

    private fun patchTextFields() {
        titleTextField.editText?.setText(advertisementDetails?.title)
        descriptionTextField.editText?.setText(advertisementDetails?.description)
        cityTextField.editText?.setText(advertisementDetails?.localizationResponse?.city)
        streetTextField.editText?.setText(advertisementDetails?.localizationResponse?.street)
        postalCodeTextField.editText?.setText(advertisementDetails?.localizationResponse?.postalCode)
        phoneNumberTextField.editText?.setText(advertisementDetails?.phoneNumber)
    }

    private fun loadFiles() {
        apiAdvertisement.getAdvertisementFiles(advertisementDetails?.id!!).enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val fileList = response.body()
                    fileList?.let {
                        displayLoadedImages(it)
                    }
                } else {
                    showToast(SERVER_ERROR)
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                showToast(CONNECTION_ERROR)
            }
        })
    }

    private fun displayLoadedImages(fileList: List<String>) {
        val uriList = fileList.mapNotNull { base64ToUri(this@AddActivity, it) }
        selectedImages.clear()
        selectedImages.addAll(uriList)
        displaySelectedImages()
    }

    private fun checkPermissionsAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(
                this,
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES
                else Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES
                    else Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                REQUEST_CODE_READ_MEDIA_IMAGES
            )
        } else {
            openGalleryForImages()
        }
    }

    private fun openGalleryForImages() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGES)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_MEDIA_IMAGES && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            openGalleryForImages()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGES && resultCode == Activity.RESULT_OK) {
            if (data?.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count.coerceAtMost(5)) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    selectedImages.add(imageUri)
                }
            } else if (data?.data != null) {
                val imageUri = data.data!!
                selectedImages.add(imageUri)
            }
            displaySelectedImages()
        }
    }

    private fun displaySelectedImages() {
        imageSelectionBox.removeAllViews()

        val imageSize = if (selectedImages.size > 1) {
            200
        } else {
            LinearLayout.LayoutParams.WRAP_CONTENT
        }

        for (imageUri in selectedImages) {
            val imageView = ImageView(this).apply {
                layoutParams =
                    LinearLayout.LayoutParams(imageSize, LinearLayout.LayoutParams.MATCH_PARENT)
                        .apply {
                            setMargins(0, 0, 0, 0)
                        }
                setImageURI(imageUri)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            imageSelectionBox.addView(imageView)
        }
    }

    private fun resetToAddIcon() {
        imageSelectionBox.removeAllViews()
        imageSelectionBox.addView(addImageIcon)
    }

    private fun addAdvertisement() {
        listOf(titleTextField, descriptionTextField, cityTextField, streetTextField, postalCodeTextField).forEach { field ->
            if (getTextFieldStringValue(field).isEmpty()) field.error = REQUIRED_FIELD
        }

        if (!(isTextFieldValid(titleTextField) &&
                    isTextFieldValid(descriptionTextField) &&
                    isTextFieldValid(cityTextField) &&
                    isTextFieldValid(streetTextField) &&
                    isTextFieldValid(postalCodeTextField) &&
                    isTextFieldValid(phoneNumberTextField))) {
            showToast(INVALID_FORM)
            return
        }

        if (selectedImages.size < 1) {
            showToast(IMAGE_VALIDATION_ERROR)
            return
        }

        val addAdvertisementRequest = AddAdvertisementRequest(
            categorySpinner.selectedItem.toString(),
            conditionSpinner.selectedItem.toString(),
            getTextFieldStringValue(titleTextField),
            getTextFieldStringValue(descriptionTextField),
            getTextFieldStringValue(cityTextField),
            getTextFieldStringValue(streetTextField),
            getTextFieldStringValue(postalCodeTextField),
            getTextFieldStringValue(phoneNumberTextField)
        )

        uploadAdvertisementWithImages(this@AddActivity, selectedImages, addAdvertisementRequest)
    }

    private fun uploadAdvertisementWithImages(
        context: Context,
        uris: MutableList<Uri>,
        addAdvertisementRequest: AddAdvertisementRequest
    ) {
        val fileParts = uris.map { uri -> prepareFilePart(context, uri) }
        val jsonRequestBody = createJsonRequestBody(addAdvertisementRequest)

        if(!edit) apiAdvertisement.addAdvertisement(fileParts, jsonRequestBody)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        val intent = Intent(this@AddActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                        showToast(ADVERTISEMENT_ADD_SUCCESS)
                    } else {
                        showToast(SERVER_ERROR)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    showToast(CONNECTION_ERROR)
                }
            })
        else apiAdvertisement.updateAdvertisement(advertisementDetails?.id!!, fileParts, jsonRequestBody)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        val intent = Intent(this@AddActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                        showToast(ADVERTISEMENT_UPDATE_SUCCESS)
                    } else {
                        showToast(SERVER_ERROR)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    showToast(CONNECTION_ERROR)
                }
            })
    }

    private fun prepareFilePart(
        context: Context,
        fileUri: Uri
    ): MultipartBody.Part {
        val inputStream: InputStream? = context.contentResolver.openInputStream(fileUri)
        val fileName = getFileName(context, fileUri)

        val requestFile = object : RequestBody() {
            override fun contentType(): MediaType? {
                return "image/*".toMediaTypeOrNull()
            }

            override fun writeTo(sink: okio.BufferedSink) {
                inputStream?.use { sink.writeAll(it.source()) }
            }
        }

        return MultipartBody.Part.createFormData("files", fileName, requestFile)
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                    result = it.getString(nameIndex)
                }
            }
        }
        if (result == null) {
            result = uri.lastPathSegment
        }
        return result ?: "unknown"
    }

    private fun createJsonRequestBody(addAdvertisementRequest: AddAdvertisementRequest): RequestBody {
        val gson = Gson()
        val jsonString = gson.toJson(addAdvertisementRequest)
        return RequestBody.create("application/json".toMediaTypeOrNull(), jsonString)
    }

    private fun populateSpinnerCategories() {
        apiCategory.getAll().enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val categories = response.body() ?: emptyList()
                    if (categories.isNotEmpty()) {
                        val adapter = ArrayAdapter(
                            this@AddActivity,
                            android.R.layout.simple_spinner_item,
                            categories
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        categorySpinner.adapter = adapter

                        if (edit) {
                            val position = categories.indexOf(advertisementDetails?.category)
                            if (position >= 0) categorySpinner.setSelection(position)
                        }
                    }
                } else {
                    showToast(SERVER_ERROR)
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                showToast(CONNECTION_ERROR)
            }
        })
    }

    private fun populateSpinnerConditions() {
        val conditions = Condition.entries.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, conditions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        conditionSpinner.adapter = adapter

        if (edit) {
            val position = conditions.indexOf(advertisementDetails?.condition)
            if (position >= 0) conditionSpinner.setSelection(position)
        }
    }

    private fun watchSimpleTextFieldChange(
        inputLayout: TextInputLayout,
        validationMessage: String
    ) {
        inputLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val inputLength = s?.length ?: 0
                if (inputLength < 3) inputLayout.error = validationMessage
                else inputLayout.error = null
            }
        })
    }

    private fun watchPostalCodeChange() {
        postalCodeTextField.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val postalCodeRegex = Regex("\\d{2}-\\d{3}")
                val input = s?.toString() ?: ""
                if (!postalCodeRegex.matches(input)) postalCodeTextField.error =
                    POSTAL_CODE_VALIDATION_ERROR
                else postalCodeTextField.error = null
            }
        })
    }

    private fun watchPhoneNumberChange() {
        phoneNumberTextField.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val phoneNumber = s?.toString()

                if (phoneNumber.isNullOrEmpty() || phoneNumber.length == 9) {
                    phoneNumberTextField.error = null
                } else {
                    phoneNumberTextField.error = INVALID_PHONE_NUMBER
                }

            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this@AddActivity, message, Toast.LENGTH_SHORT).show()
    }
}
