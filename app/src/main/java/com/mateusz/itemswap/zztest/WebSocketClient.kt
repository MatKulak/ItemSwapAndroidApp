package com.mateusz.itemswap.zztest

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

object WebSocketClient {

    private const val BASE_URL = "ws://10.0.2.2:8080"
    private var webSocket: WebSocket? = null

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    fun connect(listener: WebSocketListener) {
        val request = Request.Builder().url(BASE_URL).build()
        webSocket = client.newWebSocket(request, listener)
    }

    fun getWebSocket(): WebSocket? = webSocket

    fun disconnect() {
        webSocket?.close(1000, "Disconnect")
    }
}