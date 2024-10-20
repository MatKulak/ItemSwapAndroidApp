package com.mateusz.itemswap.activities

import android.os.Bundle
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
    private lateinit var detailedAdvertisementResponse: DetailedAdvertisementResponse
    private lateinit var files: List<String>
    private lateinit var apiAdvertisement: APIAdvertisement
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advertisement)

        preferencesHelper = PreferencesHelper(this)
        apiAdvertisement = RetrofitClient.getService(APIAdvertisement::class.java, preferencesHelper)

        viewPager = findViewById(R.id.viewPager)

        setDetailedAdvertisementResponse()
        loadFiles()
    }

    private fun setDetailedAdvertisementResponse() {
        val advertisementDetails: DetailedAdvertisementResponse? =
            intent.getParcelableExtra("advertisement_details")

        advertisementDetails?.let {
            detailedAdvertisementResponse = it
        } ?: run {
            TODO()
        }
    }

    private fun loadFiles() {
        apiAdvertisement.getAdvertisementFiles(detailedAdvertisementResponse.id).enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {

                if (response.isSuccessful) {
                    val fileList = response.body()
                    fileList?.let {
                        files = it
                        setupViewPager() // Initialize the ViewPager once files are loaded
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

    private fun setupViewPager() {
        val adapter = ImagePagerAdapter(files)
        viewPager.adapter = adapter
    }
}