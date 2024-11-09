package com.mateusz.itemswap.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.mateusz.itemswap.R
import com.mateusz.itemswap.data.advertisement.AddAdvertisementRequest
import com.mateusz.itemswap.enums.Condition
import com.mateusz.itemswap.enums.ProductCategories
import com.mateusz.itemswap.helpers.PreferencesHelper
import com.mateusz.itemswap.network.APIAdvertisement
import com.mateusz.itemswap.utils.RetrofitClient
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.source
import retrofit2.Call
import java.io.InputStream

class AddActivity : AppCompatActivity() {

    private lateinit var imageSelectionBox: LinearLayout
    private lateinit var addImageIcon: ImageView
    private lateinit var titleEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var conditionSpinner: Spinner
    private lateinit var descriptionEditText: EditText
    private lateinit var localizationEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var addButton: Button
    private lateinit var closeButton: ImageButton
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var apiAdvertisement: APIAdvertisement
    private var selectedImages: MutableList<Uri> = mutableListOf()

    companion object {
        private const val REQUEST_CODE_SELECT_IMAGES = 1
        private const val REQUEST_CODE_READ_MEDIA_IMAGES = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        preferencesHelper = PreferencesHelper(this)
        apiAdvertisement = RetrofitClient.getService(APIAdvertisement::class.java, preferencesHelper)
        imageSelectionBox = findViewById(R.id.imageSelectionBox)
        addImageIcon = findViewById(R.id.addImageIcon)
        categorySpinner = findViewById(R.id.categorySpinner)
        conditionSpinner = findViewById(R.id.conditionSpinner)
        titleEditText = findViewById(R.id.titleEt)
        descriptionEditText = findViewById(R.id.descriptionEt)
        localizationEditText = findViewById(R.id.localizationEt)
        phoneNumberEditText = findViewById(R.id.phoneNumberEt)
        addButton = findViewById(R.id.addBtn)
        closeButton = findViewById(R.id.closeButton)

        populateSpinnerCategories()
        populateSpinnerConditions()

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

        closeButton.setOnClickListener {
            onClose()
        }
    }

    private fun checkPermissionsAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(this, if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_IMAGES
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }),
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
                layoutParams = LinearLayout.LayoutParams(imageSize, LinearLayout.LayoutParams.MATCH_PARENT).apply {
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
        val addAdvertisementRequest = AddAdvertisementRequest(
            titleEditText.text.toString(),
            categorySpinner.selectedItem.toString(),
            descriptionEditText.text.toString(),
            localizationEditText.text.toString(),
            phoneNumberEditText.text.toString()
        )
        uploadAdvertisementWithImages(this@AddActivity, selectedImages, addAdvertisementRequest)
    }

    private fun onClose() {
        val intent = Intent(this@AddActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun uploadAdvertisementWithImages(
        context: Context,
        uris: MutableList<Uri>,
        addAdvertisementRequest: AddAdvertisementRequest
    ) {
        val fileParts = uris.map { uri -> prepareFilePart(context, "files", uri) }
        val jsonRequestBody = createJsonRequestBody(addAdvertisementRequest)

        apiAdvertisement.uploadFiles(fileParts, jsonRequestBody).enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    val intent = Intent(this@AddActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                    runOnUiThread {
                        Toast.makeText(this@AddActivity, "Advertisement added successfully", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@AddActivity, "Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@AddActivity, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun prepareFilePart(context: Context, partName: String, fileUri: Uri): MultipartBody.Part {
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

        return MultipartBody.Part.createFormData(partName, fileName, requestFile)
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
        val categories = ProductCategories.entries.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter
    }

    private fun populateSpinnerConditions() {
        val conditions = Condition.entries.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, conditions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        conditionSpinner.adapter = adapter
    }
}
