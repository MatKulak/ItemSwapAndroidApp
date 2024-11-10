package com.mateusz.itemswap.zztest

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

object WebSocketManager {

    private const val SERVER_URL = "ws://10.0.2.2:8080/gs-guide-websocket"

    private val client = OkHttpClient()
    private lateinit var webSocket: WebSocket
    private var isConnected = false
    private var listener: WebSocketListener? = null

    fun setListener(listener: WebSocketListener) {
        this.listener = listener
    }

    fun connect() {
        if (isConnected) {
            return
        }

        val request = Request.Builder().url(SERVER_URL).build()
        webSocket = client.newWebSocket(request, WebSocketListenerImpl(this))
        isConnected = true
    }

//    fun connect(preferencesHelper: PreferencesHelper) {
//        if (isConnected) return
//
//        val token = preferencesHelper.getJwtToken()
//        val request = Request.Builder()
//            .url(SERVER_URL)
//            .header("Authorization", "Bearer $token")
//            .build()
//
//        webSocket = client.newWebSocket(request, WebSocketListenerImpl(this))
//        isConnected = true
//    }

    fun disconnect() {
        if (isConnected) {
            webSocket.close(1000, "Disconnecting")
            client.dispatcher.executorService.shutdown()
            isConnected = false
            listener?.onConnectionClosed()
        }
    }

    fun sendMessage(message: String) {
        if (isConnected) {
            val stompMessage = "SEND\n" +
                    "destination:/app/hello\n" +
                    "\n" +
                    "$message\u0000"
            webSocket.send(stompMessage)
        }
    }

    internal fun notifyOnOpen() {
        listener?.onConnectionOpened()
        sendConnectFrame()
        subscribeToTopic()
    }

    internal fun notifyOnMessageReceived(message: String) {
        listener?.onMessageReceived(message)
    }

    internal fun notifyOnConnectionClosed() {
        listener?.onConnectionClosed()
    }

    private fun sendConnectFrame() {
        val connectFrame = "CONNECT\n" +
                "accept-version:1.1,1.0\n" +
                "heart-beat:10000,10000\n" +
                "\n" +
                "\u0000"
        webSocket.send(connectFrame)
    }

    private fun subscribeToTopic() {
        val subscribeFrame = "SUBSCRIBE\n" +
                "id:sub-0\n" +
                "destination:/topic/greetings\n" +
                "\n" +
                "\u0000"
        webSocket.send(subscribeFrame)
    }
}
