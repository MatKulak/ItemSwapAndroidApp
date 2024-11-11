package com.mateusz.itemswap.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mateusz.itemswap.R
import com.mateusz.itemswap.adapters.ConversationAdapter
import com.mateusz.itemswap.data.conversation.SimpleConversationResponse
import com.mateusz.itemswap.helpers.PreferencesHelper
import com.mateusz.itemswap.network.APIAdvertisement
import com.mateusz.itemswap.network.APIConversation
import com.mateusz.itemswap.utils.RetrofitClient
import com.mateusz.itemswap.utils.Utils.createParams
import com.mateusz.itemswap.utils.Utils.updateParams
import com.mateusz.itemswap.ztest.Page
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConversationsListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var conversationAdapter: ConversationAdapter
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var apiConversation: APIConversation
    private lateinit var apiAdvertisement: APIAdvertisement

    private val itemsPerPage = 10
    private var currentPage = 0
    private var isLoading = false
    private var isLastPage = false

    private var params = createParams(
        "page" to "0",
        "size" to "10"
    )

    companion object {
        fun newInstance(params: Map<String, String>): ConversationsListFragment {
            val fragment = ConversationsListFragment()
            val bundle = Bundle()
            bundle.putSerializable("params", HashMap(params))
            fragment.arguments = bundle
            return fragment
        }

        fun newInstance(): ConversationsListFragment {
            return ConversationsListFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val newParams = it.getSerializable("params") as HashMap<String, String>
            params = updateParams(params, newParams)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_conversations_list, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        preferencesHelper = PreferencesHelper(requireContext())
        apiConversation = RetrofitClient.getService(APIConversation::class.java, preferencesHelper)
        apiAdvertisement = RetrofitClient.getService(APIAdvertisement::class.java, preferencesHelper)

        conversationAdapter = ConversationAdapter(mutableListOf(), apiConversation, apiAdvertisement)
        recyclerView.adapter = conversationAdapter

        setupScrollListener()
        fetchConversations(currentPage)
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
                        && totalItemCount >= itemsPerPage
                    ) {
                        fetchConversations(currentPage)
                    }
                }
            }
        })
    }

    private fun fetchConversations(page: Int) {
        isLoading = true
        params = updateParams(params, createParams("page" to page.toString()))

        apiConversation.getAllConversations(params).enqueue(object : Callback<Page<SimpleConversationResponse>> {
            override fun onResponse(
                call: Call<Page<SimpleConversationResponse>>,
                response: Response<Page<SimpleConversationResponse>>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    val pageResponse = response.body()
                    val newAdvertisements = pageResponse?.content ?: emptyList()

                    if (newAdvertisements.isNotEmpty()) {
                        conversationAdapter.addItems(newAdvertisements)
                        currentPage++
                    }

                    if (pageResponse?.totalPages == currentPage) {
                        isLastPage = true
                    }
                }
            }

            override fun onFailure(call: Call<Page<SimpleConversationResponse>>, t: Throwable) {
                isLoading = false
            }
        })
    }
}