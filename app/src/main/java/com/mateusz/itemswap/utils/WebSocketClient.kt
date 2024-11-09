package com.mateusz.itemswap.utils

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString


object WebSocketClient {

    private const val TAG = "CustomWebSocketClient"
    private const val SERVER_URL = "ws://10.0.2.2:8080/gs-guide-websocket"

    private lateinit var webSocket: WebSocket
    private val client = OkHttpClient()

    fun connect() {
        val request = Request.Builder().url(SERVER_URL).build()
        webSocket = client.newWebSocket(request, WebSocketListenerImpl())
    }

    fun disconnect() {
        webSocket.close(1000, "Goodbye")
    }

    // Manually send a STOMP CONNECT frame to initiate the connection
    fun connectStomp() {
        val connectFrame = "CONNECT\naccept-version:1.1,1.2\nhost:yourhost\n\n\u0000"
        webSocket.send(connectFrame)
    }

    // Manually subscribe to a STOMP topic
    fun subscribe(topic: String) {
        val subscribeFrame = "SUBSCRIBE\nid:sub-0\ndestination:$topic\n\n\u0000"
        webSocket.send(subscribeFrame)
    }

    // Manually send a message to the server
    fun sendMessage(destination: String, content: String) {
        val sendFrame = "SEND\ndestination:$destination\ncontent-type:text/plain\n\n$content\u0000"
        webSocket.send(sendFrame)
    }

    // WebSocket listener to handle events
    private class WebSocketListenerImpl : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d(TAG, "WebSocket connected")
            connectStomp() // Send CONNECT frame to initiate STOMP handshake
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d(TAG, "Received: $text")

            // Parse STOMP frames manually here
            if (text.startsWith("MESSAGE")) {
                // Extract and handle message content as per STOMP protocol
                val body = text.substringAfter("\n\n").substringBefore("\u0000")
                Log.d(TAG, "Message Body: $body")
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.d(TAG, "Received bytes: $bytes")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "WebSocket closing: $code / $reason")
            webSocket.close(1000, null)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "WebSocket closed: $code / $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e(TAG, "WebSocket error: ${t.message}", t)
        }
    }
}

//import okhttp3.*
//import okio.ByteString
//import java.util.concurrent.TimeUnit
//
//class WebSocketClient {
//
//    private val client = OkHttpClient.Builder()
//        .readTimeout(0, TimeUnit.MILLISECONDS)
//        .build()
//
//    private var webSocket: WebSocket? = null
//
//    fun connect() {
//        val request = Request.Builder()
//            .url("ws://10.0.2.2:8080/gs-guide-websocket")  // Replace with your WebSocket server URL
//            .build()
//
//        webSocket = client.newWebSocket(request, object : WebSocketListener() {
//
//            override fun onOpen(webSocket: WebSocket, response: Response) {
//                super.onOpen(webSocket, response)
//            }
//
//            override fun onMessage(webSocket: WebSocket, text: String) {
//                super.onMessage(webSocket, text)
//            }
//
//            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
//                super.onMessage(webSocket, bytes)
//            }
//
//            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
//                super.onClosed(webSocket, code, reason)
//            }
//
//            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
//                super.onFailure(webSocket, t, response)
//            }
//        })
//    }
//
//    fun sendMessage(message: String) {
//        webSocket?.send(message)
//    }
//
//    fun close() {
//        webSocket?.close(1000, "Logout")
//    }
//}
