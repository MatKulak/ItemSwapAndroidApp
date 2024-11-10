package com.mateusz.itemswap.zztest

interface WebSocketListener {
    fun onMessageReceived(message: String)
    fun onConnectionOpened()
    fun onConnectionClosed()
}