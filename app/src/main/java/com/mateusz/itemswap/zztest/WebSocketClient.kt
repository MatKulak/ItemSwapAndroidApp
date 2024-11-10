package com.mateusz.itemswap.zztest

//import android.util.Log
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.Response
//import okhttp3.WebSocket
//import okhttp3.WebSocketListener
//import okio.ByteString
//
//object WebSocketClient {
//
//    private const val TAG = "CustomWebSocketClient"
//    private const val SERVER_URL = "ws://10.0.2.2:8080/gs-guide-websocket"
//
//    private lateinit var webSocket: WebSocket
//    private val client = OkHttpClient()
//
//    fun connect() {
//        val request = Request.Builder().url(SERVER_URL).build()
//        webSocket = client.newWebSocket(request, WebSocketListenerImpl())
//    }
//
//    fun disconnect() {
//        webSocket.close(1000, "Goodbye")
//    }
//
//    fun connectStomp() {
//        val connectFrame = "CONNECT\naccept-version:1.1,1.2\nhost:yourhost\n\n\u0000"
//        webSocket.send(connectFrame)
//    }
//
//    fun subscribe(topic: String) {
//        val subscribeFrame = "SUBSCRIBE\nid:sub-0\ndestination:$topic\n\n\u0000"
//        webSocket.send(subscribeFrame)
//    }
//
//    fun sendMessage(destination: String, content: String) {
//        val sendFrame = "SEND\ndestination:$destination\ncontent-type:text/plain\n\n$content\u0000"
//        webSocket.send(sendFrame)
//    }
//
//    private class WebSocketListenerImpl : WebSocketListener() {
//        override fun onOpen(webSocket: WebSocket, response: Response) {
//            Log.d(TAG, "WebSocket connected")
//            connectStomp() // Send CONNECT frame to initiate STOMP handshake
//        }
//
//        override fun onMessage(webSocket: WebSocket, text: String) {
//            Log.d(TAG, "Received: $text")
//
//            // Parse STOMP frames manually here
//            if (text.startsWith("MESSAGE")) {
//                // Extract and handle message content as per STOMP protocol
//                val body = text.substringAfter("\n\n").substringBefore("\u0000")
//                Log.d(TAG, "Message Body: $body")
//            }
//        }
//
//        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
//            Log.d(TAG, "Received bytes: $bytes")
//        }
//
//        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
//            Log.d(TAG, "WebSocket closing: $code / $reason")
//            webSocket.close(1000, null)
//        }
//
//        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
//            Log.d(TAG, "WebSocket closed: $code / $reason")
//        }
//
//        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
//            Log.e(TAG, "WebSocket error: ${t.message}", t)
//        }
//    }
//}
