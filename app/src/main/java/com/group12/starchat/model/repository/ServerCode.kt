package com.group12.starchat.model.repository

import androidx.compose.ui.tooling.preview.Preview
import io.socket.client.IO

class ServerCode {

    fun ServerStuff(): String {
        val socket = IO.socket("http://127.0.0.1:4400/emulators")

        var message = ""

        socket.connect()

        // Testing sending and receiving
        socket.emit("send_message", "Hello, server!")
        socket.on("receive_message") { args ->
            message = args[0] as String
        }

        return message
    }

}

