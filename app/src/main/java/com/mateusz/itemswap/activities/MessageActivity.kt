package com.mateusz.itemswap.activities

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.mateusz.itemswap.R
import com.mateusz.itemswap.data.advertisement.DetailedAdvertisementResponse
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
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
import com.mateusz.itemswap.websocket.WebSocketListener
import com.mateusz.itemswap.websocket.WebSocketManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class MessageActivity : AppCompatActivity(), WebSocketListener {

    private lateinit var advertisementTitleTextView: TextView
    private lateinit var advertisementUsernameTitleTextView: TextView
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var sendMessageEditText: EditText
    private lateinit var sendMessageTextField: TextInputLayout

    private lateinit var apiConversation: APIConversation
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var advertisementDetails: DetailedAdvertisementResponse

    private lateinit var userId: UUID
    private var conversation: ConversationResponse? = null
    private var messages: MutableList<MessageResponse> = mutableListOf()
    private var canSend = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        advertisementTitleTextView = findViewById(R.id.advertisementTitleTextView)
        advertisementUsernameTitleTextView = findViewById(R.id.advertisementUsernameTextView)
        sendMessageTextField = findViewById(R.id.sendMessageField)
        sendMessageEditText = findViewById(R.id.sendMessageEditText)
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView)
        preferencesHelper = PreferencesHelper(this)
        apiConversation = RetrofitClient.getService(APIConversation::class.java, preferencesHelper)
        userId = preferencesHelper.getUserContext()?.id!!
        setConversationResponse()
        setDetailedAdvertisementResponse()
        messageAdapter = MessageAdapter(messages, preferencesHelper.getUserContext())
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        messagesRecyclerView.adapter = messageAdapter
        updateConversation()
        setChatTitleAndUsername()

        WebSocketManager.addListener(this)
        sendMessageTextField.setEndIconOnClickListener {
            if (canSend) sendMessage()
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
        if (conversation == null) {
            canSend = false
        }
        preferencesHelper.getUserContext()
        val content = sendMessageEditText.text.toString()
        if (content == "") return

        val messageRequest = MessageRequest(
            conversation?.id,
            advertisementDetails.id,
            userId,
            content
        )
        WebSocketManager.sendMessage(objectToJson(messageRequest))
        sendMessageEditText.setText("")
    }

    private fun setChatTitleAndUsername() {
        advertisementTitleTextView.text = advertisementDetails.title
        advertisementUsernameTitleTextView.text = advertisementDetails.userResponse.username
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
        apiConversation.getConversationByAdvertisementId(advertisementDetails.id).enqueue(object : Callback<ConversationResponse> {
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
                val x = 10
            }
        })
    }

    private fun populateInitialMessages() {
        conversation?.let {
            runOnUiThread {
                messages.clear()
                messages.addAll(it.messages!!)
                messageAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onMessageReceived(message: String) {
        val responseWithReceivers = jsonToObject(message, MessageResponseWithReceivers::class.java)

        if (responseWithReceivers.receiverIds.contains(userId)) {
            if (conversation == null) {
                conversation = ConversationResponse(responseWithReceivers.conversationId, null,
                    null, null)
                canSend = true
            }

            runOnUiThread {
                messages.add(responseWithReceivers.messageResponse)
                messageAdapter.notifyItemInserted(messages.size - 1)
            }
        }
    }

    override fun onConnectionOpened() {}

    override fun onConnectionClosed() {}
}