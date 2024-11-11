package com.mateusz.itemswap.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.mateusz.itemswap.R
import com.mateusz.itemswap.data.advertisement.DetailedAdvertisementResponse
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mateusz.itemswap.adapters.MessageAdapter
import com.mateusz.itemswap.data.conversation.ConversationResponse
import com.mateusz.itemswap.data.message.MessageRequest
import com.mateusz.itemswap.data.message.MessageResponse
import com.mateusz.itemswap.data.message.MessageResponseWithReceivers
import com.mateusz.itemswap.helpers.PreferencesHelper
import com.mateusz.itemswap.network.APIConversation
import com.mateusz.itemswap.utils.RetrofitClient
import com.mateusz.itemswap.utils.Utils.jsonToObject
import com.mateusz.itemswap.utils.Utils.objectToJson
import com.mateusz.itemswap.zztest.WebSocketListener
import com.mateusz.itemswap.zztest.WebSocketManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class MessageActivity : AppCompatActivity(), WebSocketListener {

    private lateinit var advertisementTitleTextView: TextView
    private lateinit var messageEditText: EditText
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var sendMessageButton: Button
    private lateinit var messageAdapter: MessageAdapter

    private lateinit var apiConversation: APIConversation
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var advertisementDetails: DetailedAdvertisementResponse

    private lateinit var userId: UUID
    private var conversation: ConversationResponse? = null
    private var messages: MutableList<MessageResponse> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        advertisementTitleTextView = findViewById(R.id.advertisementTitleTextView)
        sendMessageButton = findViewById(R.id.sendMessageButton)
        messageEditText = findViewById(R.id.messageEditText)
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView)
        preferencesHelper = PreferencesHelper(this)
        apiConversation = RetrofitClient.getService(APIConversation::class.java, preferencesHelper)
        userId = preferencesHelper.getUserContext()?.id!!
        setConversationResponse()
        setDetailedAdvertisementResponse()
//        getConversationResponse()
        messageAdapter = MessageAdapter(messages)
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        messagesRecyclerView.adapter = messageAdapter
        updateConversation()
        setChatTitle()
//        WebSocketManager.setListener(this)
//        WebSocketManager.connect()
        WebSocketManager.addListener(this)
        sendMessageButton.setOnClickListener {
            sendMessage()
        }

    }

    private fun setConversationResponse() {
        val conversationResponse: ConversationResponse? =
            intent.getParcelableExtra("conversation")

        conversationResponse?.let {
            conversation = it
        }
    }

    private fun setDetailedAdvertisementResponse() {
        advertisementDetails = intent.getParcelableExtra("advertisementDetails") ?: return
    }

    private fun sendMessage() {
        preferencesHelper.getUserContext()
        val content = messageEditText.text.toString()
        if (content == "") return

        val messageRequest = MessageRequest(
            conversation?.id,
            advertisementDetails.id,
            userId,
            content
        )
        WebSocketManager.sendMessage(objectToJson(messageRequest))
        messageEditText.setText("")
    }

    private fun setChatTitle() {
        advertisementTitleTextView.text = advertisementDetails.title
    }

    private fun updateConversation() {
        if (conversation == null) {
            getConversationResponse()
        } else {
            runOnUiThread {
                messages.clear()
                conversation?.messages?.let { messages.addAll(it) }
                messageAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun getConversationResponse() {
        apiConversation.getConversationByAdvertisementId(advertisementDetails.id, null).enqueue(object : Callback<ConversationResponse> {
            override fun onResponse(call: Call<ConversationResponse>, response: Response<ConversationResponse>) {
                if (response.isSuccessful) {
                    val conversationResponse = response.body()
                    conversationResponse?.let {
                        conversation = conversationResponse
                        populateInitialMessages()
                    }
                }
            }

            override fun onFailure(call: Call<ConversationResponse>, t: Throwable) {
            }
        })
    }

    private fun populateInitialMessages() {
        conversation?.let {
            runOnUiThread {
                messages.clear()
                messages.addAll(it.messages)
                messageAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onMessageReceived(message: String) {
        val responseWithReceivers = jsonToObject(message, MessageResponseWithReceivers::class.java)

        if (responseWithReceivers.receiverIds.contains(userId)) {
            runOnUiThread {
                messages.add(responseWithReceivers.messageResponse)
                messageAdapter.notifyItemInserted(messages.size - 1)
            }
        }
    }

    override fun onConnectionOpened() {
    }

    override fun onConnectionClosed() {
    }
}