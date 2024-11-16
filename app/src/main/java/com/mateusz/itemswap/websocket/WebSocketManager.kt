package com.mateusz.itemswap.websocket

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

object WebSocketManager {

    private const val WS_URL = "ws://10.0.2.2:8080/ws"

    private val client = OkHttpClient()
    private lateinit var webSocket: WebSocket
    private var isConnected = false
    private val listeners = mutableSetOf<WebSocketListener>()

    fun addListener(listener: WebSocketListener) {
        listeners.add(listener)
        if (isConnected) {
            listener.onConnectionOpened()
        }
    }

    fun removeListener(listener: WebSocketListener) {
        listeners.remove(listener)
    }

    fun connect() {
        if (isConnected) return

        val request = Request.Builder().url(WS_URL).build()
        webSocket = client.newWebSocket(request, WebSocketListenerImpl(this))
        isConnected = true
    }

    fun disconnect() {
        if (isConnected) {
            webSocket.close(1000, "Disconnecting")
            isConnected = false
            notifyOnConnectionClosed()
        }
    }

    fun sendMessage(message: String) {
        if (isConnected) {
            val stompMessage = "SEND\n" +
                    "destination:/ws/message\n" +
                    "\n" +
                    "$message\u0000"
            webSocket.send(stompMessage)
        }
    }

    internal fun notifyOnOpen() {
        listeners.forEach { it.onConnectionOpened() }
        sendConnectFrame()
        subscribeToTopic()
    }

    internal fun notifyOnMessageReceived(message: String) {
        listeners.forEach { it.onMessageReceived(message) }
    }

    internal fun notifyOnConnectionClosed() {
        listeners.forEach { it.onConnectionClosed() }
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
                "destination:/topic/message\n" +
                "\n" +
                "\u0000"
        webSocket.send(subscribeFrame)
    }
}
