//package com.mateusz.itemswap.activities
//
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.provider.MediaStore
//import android.widget.ArrayAdapter
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageView
//import android.widget.LinearLayout
//import android.widget.Spinner
//import androidx.appcompat.app.AppCompatActivity
//import com.google.gson.Gson
//import com.mateusz.itemswap.R
//import com.mateusz.itemswap.data.AddAdvertisementRequest
//import com.mateusz.itemswap.enums.ProductCategories
//import com.mateusz.itemswap.helpers.PreferencesHelper
//import com.mateusz.itemswap.network.APIAdvertisement
//import com.mateusz.itemswap.utils.RetrofitClient
//import okhttp3.MediaType
//
//import okhttp3.MultipartBody
//import okhttp3.RequestBody
//import okhttp3.ResponseBody
//import retrofit2.Call
//import java.io.File
//
//class AddActivity : AppCompatActivity() {
//
//    private lateinit var imageSelectionBox: LinearLayout
//    private lateinit var addImageIcon: ImageView
//    private lateinit var titleEditText: EditText
//    private lateinit var categorySpinner: Spinner
//    private lateinit var descriptionEditText: EditText
//    private lateinit var localizationEditText: EditText
//    private lateinit var phoneNumberEditText: EditText
//    private lateinit var addButton: Button
//    private lateinit var preferencesHelper: PreferencesHelper
//    private lateinit var apiAdvertisement: APIAdvertisement
//    private var selectedImages: MutableList<Uri> = mutableListOf()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_add)
//
//        preferencesHelper = PreferencesHelper(this)
//        apiAdvertisement = RetrofitClient.getService(APIAdvertisement::class.java, preferencesHelper)
//        imageSelectionBox = findViewById(R.id.imageSelectionBox)
//        addImageIcon = findViewById(R.id.addImageIcon)
//        categorySpinner = findViewById(R.id.categorySpinner)
//        titleEditText = findViewById(R.id.titleEt)
//        descriptionEditText = findViewById(R.id.descriptionEt)
//        localizationEditText = findViewById(R.id.localizationEt)
//        phoneNumberEditText = findViewById(R.id.phoneNumberEt)
//        addButton = findViewById(R.id.addBtn)
//
//        populateSpinnerCategories()
//
//        imageSelectionBox.setOnClickListener {
//            if (selectedImages.isNotEmpty()) {
//                // Clear existing images and reset to add icon
//                selectedImages.clear()
//                resetToAddIcon()
//            }
//            // Open gallery to select new images
//            openGalleryForImages()
//        }
//
//        addButton.setOnClickListener {
//            addAdvertisement()
//        }
//    }
//
//
//
//
//    private fun addAdvertisement() {
//        val addAdvertisementRequest = AddAdvertisementRequest(
//            titleEditText.text.toString(),
//            enumValueOf<ProductCategories>(categorySpinner.selectedItem.toString()),
//            descriptionEditText.text.toString(),
//            localizationEditText.text.toString(),
//            phoneNumberEditText.text.toString()
//        )
//        uploadAdvertisementWithImages(this@AddActivity, selectedImages, addAdvertisementRequest)
//    }
//
//    private fun uploadAdvertisementWithImages(
//        context: Context,
//        uris: MutableList<Uri>,
//        addAdvertisementRequest: AddAdvertisementRequest
//    ) {
//        val fileParts = uris.map { uri -> prepareFilePart(context, "files", uri) }
//        val jsonRequestBody = createJsonRequestBody(addAdvertisementRequest)
//
//        apiAdvertisement.uploadFiles(fileParts, jsonRequestBody).enqueue(object : retrofit2.Callback<ResponseBody> {
//            override fun onResponse(
//                call: Call<ResponseBody>,
//                response: retrofit2.Response<ResponseBody>
//            ) {
//                if (response.isSuccessful) {
//                    // Obsługa sukcesu
//                    println("Upload success!")
//                } else {
//                    // Obsługa błędu
//                    println("Upload failed: ${response.errorBody()?.string()}")
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                // Obsługa niepowodzenia
//                t.printStackTrace()
//                println("Upload failed: ${t.message}")
//            }
//        })
//    }
//
//    private fun prepareFilePart(context: Context, partName: String, fileUri: Uri): MultipartBody.Part {
//        val file = File(getPathFromUri(context, fileUri))
//        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
//        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
//    }
//
//    private fun getPathFromUri(context: Context, contentUri: Uri): String {
//        var result: String? = null
//        val cursor = context.contentResolver.query(contentUri, null, null, null, null)
//        if (cursor != null) {
//            cursor.moveToFirst()
//            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
//            result = cursor.getString(idx)
//            cursor.close()
//        }
//        return result ?: contentUri.path ?: ""
//    }
//
//    private fun createJsonRequestBody(addAdvertisementRequest: AddAdvertisementRequest): RequestBody {
//        val gson = Gson()
//        val jsonString = gson.toJson(addAdvertisementRequest)
//        return RequestBody.create(MediaType.parse("application/json"), jsonString)
//    }
//
//    private fun openGalleryForImages() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        intent.type = "image/*"
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGES)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_CODE_SELECT_IMAGES && resultCode == Activity.RESULT_OK) {
//            if (data?.clipData != null) {
//                val count = data.clipData!!.itemCount
//                for (i in 0 until count.coerceAtMost(5)) {
//                    val imageUri = data.clipData!!.getItemAt(i).uri
//                    selectedImages.add(imageUri)
//                }
//            } else if (data?.data != null) {
//                val imageUri = data.data!!
//                selectedImages.add(imageUri)
//            }
//            displaySelectedImages()
//        }
//    }
//
//    private fun displaySelectedImages() {
//        selectedImages
//        imageSelectionBox.removeAllViews()
//
//        val imageSize = if (selectedImages.size > 1) {
//            200 // Fixed size for multiple images
//        } else {
//            LinearLayout.LayoutParams.WRAP_CONTENT // Dynamic size for single image
//        }
//
//        for (imageUri in selectedImages) {
//            val imageView = ImageView(this).apply {
//                layoutParams = LinearLayout.LayoutParams(imageSize, LinearLayout.LayoutParams.MATCH_PARENT).apply {
//                    setMargins(0, 0, 0, 0)
//                }
//                setImageURI(imageUri)
//                scaleType = ImageView.ScaleType.CENTER_CROP
//            }
//            imageSelectionBox.addView(imageView)
//        }
//    }
//
//
//    private fun resetToAddIcon() {
//        // Clear views and reset the "+" icon
//        imageSelectionBox.removeAllViews()
//        imageSelectionBox.addView(addImageIcon)
//    }
//
//    companion object {
//        private const val REQUEST_CODE_SELECT_IMAGES = 1
//    }
//
//    private fun populateSpinnerCategories() {
//        val categories = ProductCategories.entries.map { it.name }
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        categorySpinner.adapter = adapter
//    }
//}
//
//
//
//


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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.mateusz.itemswap.R
import com.mateusz.itemswap.data.AddAdvertisementRequest
import com.mateusz.itemswap.enums.ProductCategories
import com.mateusz.itemswap.helpers.PreferencesHelper
import com.mateusz.itemswap.network.APIAdvertisement
import com.mateusz.itemswap.utils.RetrofitClient
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.InputStream

class AddActivity : AppCompatActivity() {

    private lateinit var imageSelectionBox: LinearLayout
    private lateinit var addImageIcon: ImageView
    private lateinit var titleEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var descriptionEditText: EditText
    private lateinit var localizationEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var addButton: Button
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
        titleEditText = findViewById(R.id.titleEt)
        descriptionEditText = findViewById(R.id.descriptionEt)
        localizationEditText = findViewById(R.id.localizationEt)
        phoneNumberEditText = findViewById(R.id.phoneNumberEt)
        addButton = findViewById(R.id.addBtn)

        populateSpinnerCategories()

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
    }

    private fun checkPermissionsAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
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
            200 // Fixed size for multiple images
        } else {
            LinearLayout.LayoutParams.WRAP_CONTENT // Dynamic size for single image
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
                    println("Upload success!")
                } else {
                    println("Upload failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                println("Upload failed: ${t.message}")
            }
        })
    }

    private fun prepareFilePart(context: Context, partName: String, fileUri: Uri): MultipartBody.Part {
        val inputStream: InputStream? = context.contentResolver.openInputStream(fileUri)
        val fileName = getFileName(context, fileUri)

        val requestFile = object : RequestBody() {
            override fun contentType(): MediaType? {
                return MediaType.parse("image/*")
            }

            override fun writeTo(sink: okio.BufferedSink) {
                inputStream?.use { sink.writeAll(okio.Okio.source(it)) }
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
        return RequestBody.create(MediaType.parse("application/json"), jsonString)
    }

    private fun populateSpinnerCategories() {
        val categories = ProductCategories.entries.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter
    }
}
