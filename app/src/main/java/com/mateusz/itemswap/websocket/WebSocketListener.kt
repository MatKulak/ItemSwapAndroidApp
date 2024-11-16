package com.mateusz.itemswap.websocket

interface WebSocketListener {
    fun onMessageReceived(message: String)
    fun onConnectionOpened()
    fun onConnectionClosed()
}