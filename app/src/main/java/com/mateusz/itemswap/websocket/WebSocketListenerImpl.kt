package com.mateusz.itemswap.websocket

import com.mateusz.itemswap.utils.Utils.isValidJson
import com.mateusz.itemswap.utils.Utils.parseStompMessageBody
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class WebSocketListenerImpl(private val manager: WebSocketManager) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        manager.notifyOnOpen()
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        val messageBody = parseStompMessageBody(text)
        if (messageBody != null && isValidJson(messageBody)) manager.notifyOnMessageReceived(messageBody)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        manager.notifyOnConnectionClosed()
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        manager.notifyOnConnectionClosed()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        manager.notifyOnConnectionClosed()
    }
}