package com.mateusz.itemswap.zztest

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketListener : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
    }
}