package com.mateusz.itemswap.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.mateusz.itemswap.R
import com.mateusz.itemswap.adapters.ImagePagerAdapter
import com.mateusz.itemswap.data.advertisement.DetailedAdvertisementResponse
import com.mateusz.itemswap.helpers.PreferencesHelper
import com.mateusz.itemswap.network.APIAdvertisement
import com.mateusz.itemswap.utils.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdvertisementActivity : AppCompatActivity() {
    private lateinit var response: DetailedAdvertisementResponse
    private lateinit var files: List<String>
    private lateinit var apiAdvertisement: APIAdvertisement
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var closeImageButton: ImageButton
    private lateinit var titleTextView: TextView
    private lateinit var viewPager: ViewPager2
    private lateinit var conditionTextView: TextView
    private lateinit var categoryTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var phoneNumberTextView: TextView
    private lateinit var localizationTextView: TextView
    private lateinit var sendMessageButton: Button
    private lateinit var followImageButton: ImageButton
    private lateinit var callButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advertisement)
        preferencesHelper = PreferencesHelper(this)
        apiAdvertisement = RetrofitClient.getService(APIAdvertisement::class.java, preferencesHelper)
        closeImageButton = findViewById(R.id.closeImageButton)
        titleTextView = findViewById(R.id.titleTextView)
        viewPager = findViewById(R.id.viewPager)
        conditionTextView = findViewById(R.id.conditionTextView)
        categoryTextView = findViewById(R.id.categoryTextView)//
        descriptionTextView = findViewById(R.id.descriptionTextView)
        usernameTextView = findViewById(R.id.usernameTextView)
        phoneNumberTextView = findViewById(R.id.phoneNumberTextView)
        localizationTextView = findViewById(R.id.locationTextView)//
        sendMessageButton = findViewById(R.id.sendMessageButton)
        followImageButton = findViewById(R.id.followImageButton)
        callButton = findViewById(R.id.callButton)
        setDetailedAdvertisementResponse()
        setDataFromResponse()
        setInitialFollowStatus()
        loadFiles()
        followImageButton.setOnClickListener {
            updateFollowStatus()
        }
        closeImageButton.setOnClickListener {
            goBackToList()
        }
        sendMessageButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun sendMessage() {

    }

    private fun goBackToList() {
        val intent = Intent(this@AdvertisementActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setInitialFollowStatus() {
        if (response.isFollowed)
            followImageButton.setImageResource(R.drawable.ic_heart_red)
        else
            followImageButton.setImageResource(R.drawable.ic_heart_gray)
    }

    private fun setDataFromResponse() {
        val localizationString = response.localizationResponse.city + ", " + response.localizationResponse.street
        val conditionString = response.condition
        val categoryString = "ELECTRONICS"
        titleTextView.text = response.title
        conditionTextView.text = conditionString
        categoryTextView.text = categoryString
        descriptionTextView.text = response.description
        usernameTextView.text = response.userResponse.username
        phoneNumberTextView.text = response.userResponse.phoneNumber//
        localizationTextView.text = localizationString
    }

    private fun setDetailedAdvertisementResponse() {
        val advertisementDetails: DetailedAdvertisementResponse? =
            intent.getParcelableExtra("advertisement_details")

        advertisementDetails?.let {
            response = it
        } ?: run {
            TODO()
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
                    } ?: run {
                        TODO()
                    }
                } else {
                    TODO()
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                TODO()
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
                    } ?: run {
                        TODO()
                    }
                } else {
                    TODO()
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                TODO()
            }
        })

    }

    private fun setupViewPager() {
        val adapter = ImagePagerAdapter(files)
        viewPager.adapter = adapter
    }
}
