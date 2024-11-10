package com.mateusz.itemswap.zztest

import android.util.Log
import okhttp3.*

object WebSocketManager {

    private const val TAG = "WebSocketManager"
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
            Log.d(TAG, "Already connected to WebSocket")
            return
        }

        val request = Request.Builder().url(SERVER_URL).build()
        webSocket = client.newWebSocket(request, WebSocketListenerImpl(this))
        isConnected = true
    }

    fun disconnect() {
        if (isConnected) {
            webSocket.close(1000, "Disconnecting")
            client.dispatcher.executorService.shutdown()
            isConnected = false
            Log.d(TAG, "WebSocket disconnected")
            listener?.onConnectionClosed()
        }
    }

    fun sendMessage(message: String) {
        if (isConnected) {
            // Sending a STOMP SEND frame
            val stompMessage = "SEND\n" +
                    "destination:/app/hello\n" +
                    "\n" +
                    "$message\u0000"
            webSocket.send(stompMessage)
            Log.d(TAG, "Message sent: $message")
        } else {
            Log.d(TAG, "WebSocket is not connected, unable to send message")
        }
    }

    // Internal callback methods
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
        // Sends a STOMP CONNECT frame to initiate the STOMP protocol handshake
        val connectFrame = "CONNECT\n" +
                "accept-version:1.1,1.0\n" +
                "heart-beat:10000,10000\n" +
                "\n" +
                "\u0000"
        webSocket.send(connectFrame)
        Log.d(TAG, "STOMP CONNECT frame sent")
    }

    private fun subscribeToTopic() {
        // Sends a STOMP SUBSCRIBE frame to subscribe to the greetings topic
        val subscribeFrame = "SUBSCRIBE\n" +
                "id:sub-0\n" +
                "destination:/topic/greetings\n" +
                "\n" +
                "\u0000"
        webSocket.send(subscribeFrame)
        Log.d(TAG, "Subscribed to /topic/greetings")
    }
}
