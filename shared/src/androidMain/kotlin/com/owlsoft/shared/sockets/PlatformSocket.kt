package com.owlsoft.shared.sockets

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket

internal actual class PlatformSocket actual constructor(
    private val url: String,
    private val authID: String
) {

    private var webSocket: WebSocket? = null

    actual fun openSocket(listener: PlatformSocketListener) {
        val socketRequest = Request.Builder()
            .url(url)
            .addHeader("Authentication", authID)
            .build()

        val webClient = OkHttpClient().newBuilder().build()
        webSocket = webClient.newWebSocket(
            socketRequest,
            object : okhttp3.WebSocketListener() {

                override fun onOpen(webSocket: WebSocket, response: Response) = listener.onOpen()

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) =
                    listener.onFailure(t)

                override fun onMessage(webSocket: WebSocket, text: String) =
                    listener.onMessage(text)

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) =
                    listener.onClosing(code, reason)

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) =
                    listener.onClosed(code, reason)
            }
        )
    }

    actual fun closeSocket(code: Int, reason: String) {
        webSocket?.close(code, reason)
        webSocket = null
    }

    actual fun sendMessage(msg: String) {
        webSocket?.send(msg)
    }
}