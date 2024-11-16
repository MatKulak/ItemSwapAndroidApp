package com.mateusz.itemswap.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.chip.Chip
import com.mateusz.itemswap.R
import com.mateusz.itemswap.adapters.ImagePagerAdapter
import com.mateusz.itemswap.data.advertisement.DetailedAdvertisementResponse
import com.mateusz.itemswap.helpers.PreferencesHelper
import com.mateusz.itemswap.network.APIAdvertisement
import com.mateusz.itemswap.others.Constants.CONNECTION_ERROR
import com.mateusz.itemswap.others.Constants.SERVER_ERROR
import com.mateusz.itemswap.utils.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdvertisementActivity : AppCompatActivity() {

    private lateinit var response: DetailedAdvertisementResponse
    private lateinit var files: List<String>
    private lateinit var apiAdvertisement: APIAdvertisement
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var titleTextView: TextView
    private lateinit var viewPager: ViewPager2
    private lateinit var conditionChip: Chip
    private lateinit var categoryChip: Chip
    private lateinit var descriptionTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var phoneNumberTextView: TextView
    private lateinit var localizationTextView: TextView
    private lateinit var sendMessageButton: Button
    private lateinit var followImageButton: ImageButton
    private lateinit var callButton: Button
    private lateinit var editButton: Button

    private var personalAdvertisement = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advertisement)
        preferencesHelper = PreferencesHelper(this)
        apiAdvertisement = RetrofitClient.getService(APIAdvertisement::class.java, preferencesHelper)
        titleTextView = findViewById(R.id.titleTextView)
        viewPager = findViewById(R.id.viewPager)
        conditionChip = findViewById(R.id.conditionChip)
        categoryChip = findViewById(R.id.categoryChip)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        usernameTextView = findViewById(R.id.usernameTextView)
        phoneNumberTextView = findViewById(R.id.phoneNumberTextView)
        localizationTextView = findViewById(R.id.locationTextView)
        sendMessageButton = findViewById(R.id.sendMessageButton)
        followImageButton = findViewById(R.id.followImageButton)
        callButton = findViewById(R.id.callButton)
        editButton = findViewById(R.id.editButton)

        personalAdvertisement = intent.getBooleanExtra("personalAdvertisement", false)
        sendMessageButton.visibility = if (!personalAdvertisement) View.VISIBLE else View.GONE
        followImageButton.visibility = if (!personalAdvertisement) View.VISIBLE else View.GONE
        callButton.visibility = if (!personalAdvertisement) View.VISIBLE else View.GONE
        editButton.visibility = if (!personalAdvertisement) View.GONE else View.VISIBLE

        setDetailedAdvertisementResponse()
        setDataFromResponse()
        setInitialFollowStatus()
        loadFiles()

        followImageButton.setOnClickListener {
            updateFollowStatus()
        }

        sendMessageButton.setOnClickListener {
            sendMessage()
        }

        callButton.setOnClickListener {
            call()
        }

        editButton.setOnClickListener {
            edit()
        }
    }

    private fun edit() {
        val intent = Intent(this, AddActivity::class.java)
        intent.putExtra("advertisementId", response.id.toString())
        startActivity(intent)
    }

    private fun call() {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${response.phoneNumber}")
        startActivity(intent)
    }

    private fun sendMessage() {
        val intent = Intent(this, MessageActivity::class.java)
        intent.putExtra("advertisementDetails", response)
        startActivity(intent)
    }

    private fun setInitialFollowStatus() {
        if (response.isFollowed)
            followImageButton.setImageResource(R.drawable.ic_heart_red)
        else
            followImageButton.setImageResource(R.drawable.ic_heart_gray)
    }

    private fun setDataFromResponse() {
        val localizationString = response.localizationResponse.city + ", " +
                response.localizationResponse.street + ", " +
                response.localizationResponse.postalCode
        titleTextView.text = response.title
        conditionChip.text = response.condition
        categoryChip.text = response.category
        descriptionTextView.text = response.description
        usernameTextView.text = response.userResponse.username
        phoneNumberTextView.text = response.phoneNumber
        localizationTextView.text = localizationString
    }

    private fun setDetailedAdvertisementResponse() {
        val advertisementDetails: DetailedAdvertisementResponse? =
            intent.getParcelableExtra("advertisementDetails")

        advertisementDetails?.let {
            response = it
        }
    }

    private fun loadFiles() {
        apiAdvertisement.getAdvertisementFiles(response.id).enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val fileList = response.body()
                    fileList?.let {
                        files = it
                        setupViewPager()
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

    private fun updateFollowStatus() {
        apiAdvertisement.updateAdvertisementFollowers(response.id).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    val isFollowed = response.body()

                    isFollowed?.let {
                        if (it) {
                            followImageButton.setImageResource(R.drawable.ic_heart_red)
                        } else {
                            followImageButton.setImageResource(R.drawable.ic_heart_gray)
                        }
                    }
                } else {
                    showToast(SERVER_ERROR)
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                showToast(CONNECTION_ERROR)
            }
        })

    }

    private fun setupViewPager() {
        val adapter = ImagePagerAdapter(files)
        viewPager.adapter = adapter
    }

    private fun showToast(message: String) {
        Toast.makeText(this@AdvertisementActivity, message, Toast.LENGTH_SHORT).show()
    }
}
