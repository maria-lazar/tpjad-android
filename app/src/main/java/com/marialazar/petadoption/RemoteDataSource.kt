package com.marialazar.petadoption

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okio.ByteString

data class WsMessage(val type: String, val payload: Payload)

data class Payload(val token: String?)

object RemoteDataSource {
    val eventChannel = Channel<String>()
    private var webSocket: WebSocket? = null
    private val request = Request.Builder().url("ws://192.168.0.248:8080/websocket").build()

    fun createWebSocket() {
        webSocket = OkHttpClient().newWebSocket(
            request,
            MyWebSocketListener()
        )
    }

    fun destroyWebSocket() {
        webSocket?.close(1000, null)
    }

    private class MyWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d("WebSocket", "onOpen")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d("WebSocket", "onMessage$text")
            runBlocking { eventChannel.send(text) }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.d("WebSocket", "onMessage bytes")
            output("Receiving bytes : " + bytes!!.hex())
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            Log.d("WebSocket", "onClosed")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e("WebSocket", "onFailure", t)
            t.printStackTrace()
        }

        private fun output(txt: String) {
            Log.d("WebSocket", txt)
        }
    }
}
