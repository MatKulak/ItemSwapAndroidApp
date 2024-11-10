package com.mateusz.itemswap.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.mateusz.itemswap.R
import com.mateusz.itemswap.data.advertisement.DetailedAdvertisementResponse
import android.widget.TextView
import com.mateusz.itemswap.data.conversation.ConversationResponse
import com.mateusz.itemswap.helpers.PreferencesHelper
import com.mateusz.itemswap.network.APIConversation
import com.mateusz.itemswap.utils.RetrofitClient
import com.mateusz.itemswap.zztest.WebSocketListener
import com.mateusz.itemswap.zztest.WebSocketManager
//import com.mateusz.itemswap.zztest.WebSocketClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessageActivity : AppCompatActivity(), WebSocketListener {
    private lateinit var advertisementTitleTextView: TextView

    private lateinit var advertisement: DetailedAdvertisementResponse
    private lateinit var conversation: ConversationResponse
    private lateinit var apiConversation: APIConversation
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var webSocketManager: WebSocketManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        advertisementTitleTextView = findViewById(R.id.advertisementTitleTextView)
        preferencesHelper = PreferencesHelper(this)
        apiConversation = RetrofitClient.getService(APIConversation::class.java, preferencesHelper)
        setAdvertisement()
        getInitialConversation()
        setChatTitle()

//        WebSocketClient.connect()
//        WebSocketClient.subscribe("/topic/greetings")
//        sendMessageToServer("hello from android")

        WebSocketManager.setListener(this)
        WebSocketManager.connect()
        sendMessage("hello from android")


    }

//    private fun sendMessageToServer(message: String) {
//        WebSocketClient.sendMessage("/app/hello", message)
//    }

    private fun sendMessage(message: String) {
        WebSocketManager.sendMessage(message)
    }

    private fun setAdvertisement() {
        advertisement = intent.getParcelableExtra("advertisementDetails") ?: return
    }

    private fun setChatTitle() {
        advertisementTitleTextView.text = advertisement.title
    }

    private fun getInitialConversation() {
        apiConversation.getConversationByAdvertisementId(advertisement.id).enqueue(object :
            Callback<ConversationResponse> {
            override fun onResponse(call: Call<ConversationResponse>, response: Response<ConversationResponse>) {
                if (response.isSuccessful) {
                    val response = response.body()

                    response?.let {
                        conversation = response
                    } ?: run {
                    }
                } else {
                    TODO()
                }
            }

            override fun onFailure(call: Call<ConversationResponse>, t: Throwable) {
            }
        })
    }

    override fun onMessageReceived(message: String) {
        println(message)
    }

    override fun onConnectionOpened() {
        println("connected")
    }

    override fun onConnectionClosed() {
    }
}