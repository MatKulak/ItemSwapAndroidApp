package com.mateusz.itemswap.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mateusz.itemswap.R
import com.mateusz.itemswap.adapters.AdvertisementAdapter
import com.mateusz.itemswap.data.advertisement.AdvertisementWithFileResponse
import com.mateusz.itemswap.helpers.PreferencesHelper
import com.mateusz.itemswap.network.APIAdvertisement
import com.mateusz.itemswap.utils.RetrofitClient
import com.mateusz.itemswap.ztest.Page
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FindFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var advertisementAdapter: AdvertisementAdapter
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var apiAdvertisement: APIAdvertisement

    private var currentPage = 0
    private var isLoading = false
    private var isLastPage = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scrollable_list, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        preferencesHelper = PreferencesHelper(requireContext())
        apiAdvertisement = RetrofitClient.getService(APIAdvertisement::class.java, preferencesHelper)

        advertisementAdapter = AdvertisementAdapter(mutableListOf(), apiAdvertisement)
        recyclerView.adapter = advertisementAdapter

        setupScrollListener()
        fetchAdvertisementsFromBackend(currentPage)
        return view
    }

    private fun setupScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= 10
                    ) {
                        fetchAdvertisementsFromBackend(currentPage)
                    }
                }
            }
        })
    }

    private fun fetchAdvertisementsFromBackend(page: Int) {
        isLoading = true

        apiAdvertisement.getAllAdvertisements(page, 10).enqueue(object : Callback<Page<AdvertisementWithFileResponse>> {
            override fun onResponse(
                call: Call<Page<AdvertisementWithFileResponse>>,
                response: Response<Page<AdvertisementWithFileResponse>>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    val pageResponse = response.body()
                    val newAdvertisements = pageResponse?.content ?: emptyList()

                    if (newAdvertisements.isNotEmpty()) {
                        advertisementAdapter.addItems(newAdvertisements)
                        currentPage++
                    }

                    if (pageResponse?.totalPages == currentPage) {
                        isLastPage = true
                    }
                }
            }

            override fun onFailure(call: Call<Page<AdvertisementWithFileResponse>>, t: Throwable) {
                isLoading = false
            }
        })
    }
}

