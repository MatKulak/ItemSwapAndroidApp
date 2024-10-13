package com.mateusz.itemswap.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mateusz.itemswap.adapters.AdvertisementAdapter
import com.mateusz.itemswap.data.AdvertisementWithFilesResponse
import com.mateusz.itemswap.databinding.FragmentFindBinding
import com.mateusz.itemswap.helpers.PreferencesHelper
import com.mateusz.itemswap.network.APIAdvertisement
//import com.mateusz.itemswap.network.RetrofitClient
import com.mateusz.itemswap.utils.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FindFragment : Fragment() {

    private lateinit var binding: FragmentFindBinding
    private lateinit var advertisementAdapter: AdvertisementAdapter
    private lateinit var apiAdvertisement: APIAdvertisement
    private var currentPage = 0
    private var isLoading = false
    private var hasMorePages = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFindBinding.inflate(inflater, container, false)

        // Initialize Retrofit client and API service
        apiAdvertisement = RetrofitClient.getService(APIAdvertisement::class.java, PreferencesHelper(requireContext()))

        advertisementAdapter = AdvertisementAdapter()
        binding.recyclerViewAdvertisements.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = advertisementAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1) && !isLoading && hasMorePages) {
                        // Load next page when user reaches the bottom
                        loadAdvertisements(currentPage + 1)
                    }
                }
            })
        }

        // Initial load
        loadAdvertisements(currentPage)

        return binding.root
    }

    private fun loadAdvertisements(page: Int) {
        isLoading = true

        // Replace the call below with the actual method in your APIAdvertisement to get advertisements
        val filters = mapOf<String, String>() // Add any necessary filters here
        apiAdvertisement.getAllAdvertisements(page, 10, filters).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val advertisementList = parseAdvertisements(response.body()?.string())
                    advertisementAdapter.addAdvertisements(advertisementList)
                    currentPage = page
                    isLoading = false
                    hasMorePages = advertisementList.isNotEmpty()
                } else {
                    // Handle non-successful responses
                    isLoading = false
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle failure
                isLoading = false
            }
        })
    }

    // Dummy function to parse the advertisement data, adjust based on actual response
    private fun parseAdvertisements(response: String?): List<AdvertisementWithFilesResponse> {
        // Parse the response and return a list of AdvertisementWithFilesResponse
        return listOf() // Replace this with your actual parsing logic
    }
}