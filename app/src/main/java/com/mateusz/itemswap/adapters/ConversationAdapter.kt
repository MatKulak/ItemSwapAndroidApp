package com.mateusz.itemswap.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mateusz.itemswap.R
import com.mateusz.itemswap.activities.MessageActivity
import com.mateusz.itemswap.data.advertisement.DetailedAdvertisementResponse
import com.mateusz.itemswap.data.conversation.ConversationResponse
import com.mateusz.itemswap.data.conversation.SimpleConversationResponse
import com.mateusz.itemswap.network.APIAdvertisement
import com.mateusz.itemswap.network.APIConversation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class ConversationAdapter(
    private val conversations: MutableList<SimpleConversationResponse>,
    private val apiConversation: APIConversation,
    private val apiAdvertisement: APIAdvertisement
) :
    RecyclerView.Adapter<ConversationAdapter.ViewHolder>() {
    private lateinit var advertisementDetails: DetailedAdvertisementResponse

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val advertisementTitleTextView: TextView = view.findViewById(R.id.advertisementTitleTextView)
        val usernameTextView: TextView = view.findViewById(R.id.usernameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_conversation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val conversation = conversations[position]
        holder.usernameTextView.text = conversation.username
        holder.advertisementTitleTextView.text = conversation.advertisementTitle

        holder.itemView.setOnClickListener {
            getAdvertisementDetails(conversation, holder.itemView.context)
        }
    }

    override fun getItemCount(): Int {
        return conversations.size
    }

    fun addItems(newConversations: List<SimpleConversationResponse>) {
        val startPosition = conversations.size
        conversations.addAll(newConversations)
        notifyItemRangeInserted(startPosition, newConversations.size)
    }

    private fun getAdvertisementDetails(conversation: SimpleConversationResponse, context: Context) {
        apiAdvertisement.getAdvertisementById(conversation.advertisementId)
            .enqueue(object : Callback<DetailedAdvertisementResponse> {
                override fun onResponse(
                    call: Call<DetailedAdvertisementResponse>,
                    response: Response<DetailedAdvertisementResponse>
                ) {
                    if (response.isSuccessful) {
                        val detailedAdvertisementResponse = response.body()
                        if (detailedAdvertisementResponse != null) {
                            advertisementDetails = detailedAdvertisementResponse
                            getConversationDetails(conversation, context)
                        }
                    }
                }

                override fun onFailure(call: Call<DetailedAdvertisementResponse>, t: Throwable) {
                    // Handle failure as needed
                    TODO()
                }
            })
    }

    private fun getConversationDetails(conversation: SimpleConversationResponse, context: Context) {
        apiConversation.getConversationByAdvertisementId(conversation.advertisementId, conversation.participantId)
            .enqueue(object : Callback<ConversationResponse> {
                override fun onResponse(
                    call: Call<ConversationResponse>,
                    response: Response<ConversationResponse>
                ) {
                    if (response.isSuccessful) {
                        val detailedConversation = response.body()
                        detailedConversation?.let {
                            val intent = Intent(context, MessageActivity::class.java).apply {
                                putExtra("conversation", it)
                                putExtra("advertisementDetails", advertisementDetails)
                            }
                            context.startActivity(intent)
                        }
                    }
                }

                override fun onFailure(call: Call<ConversationResponse>, t: Throwable) {
                    // Handle failure as needed
                    TODO()
                }
            })
    }
}