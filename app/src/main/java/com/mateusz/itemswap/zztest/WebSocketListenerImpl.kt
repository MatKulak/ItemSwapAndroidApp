package com.mateusz.itemswap.zztest

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class WebSocketListenerImpl(private val manager: WebSocketManager) : WebSocketListener() {

    private val TAG = "WebSocketListenerImpl"

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d(TAG, "WebSocket connection opened")
        manager.notifyOnOpen()
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d(TAG, "Raw STOMP Message received: $text")
        val messageBody = parseStompMessageBody(text)
        manager.notifyOnMessageReceived(messageBody)
    }

    private fun parseStompMessageBody(stompMessage: String): String {
        // STOMP messages typically have headers followed by a blank line and the message body
        val bodyIndex = stompMessage.indexOf("\n\n") + 2
        return if (bodyIndex != -1) {
            stompMessage.substring(bodyIndex).trimEnd('\u0000') // Remove the null terminator if present
        } else {
            ""
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.d(TAG, "Binary message received: ${bytes.hex()}")
        // Optionally handle binary messages if needed
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d(TAG, "Closing WebSocket: $code / $reason")
        manager.notifyOnConnectionClosed()
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.d(TAG, "WebSocket closed: $code / $reason")
        manager.notifyOnConnectionClosed()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e(TAG, "WebSocket connection error: ${t.message}", t)
        manager.notifyOnConnectionClosed()
    }
}