package com.owlsoft.shared.sockets

//Common
internal actual class PlatformSocket actual constructor(
    url: String,
    authID: String
) {
    actual fun openSocket(listener: PlatformSocketListener) {
        TODO()
    }

    actual fun closeSocket(code: Int, reason: String) {
        TODO()
    }

    actual fun sendMessage(msg: String) {
        TODO()
    }
}