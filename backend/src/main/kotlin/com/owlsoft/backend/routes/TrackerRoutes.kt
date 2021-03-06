package com.owlsoft.backend.routes

import com.owlsoft.backend.managers.EncountersManager

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.time.Duration

const val trackerPath = "/tracker"

@OptIn(InternalCoroutinesApi::class)
fun Route.encounterTrackerRoute(
    encountersManager: EncountersManager
) {
    webSocket("$trackerPath/{code}") {

        val code = call.parameters["code"] ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@webSocket
        }

        val auth = call.request.headers["Authentication"] ?: kotlin.run {
            call.respond(HttpStatusCode.Unauthorized)
            return@webSocket
        }

        encountersManager.join(code, auth, this)
    }
}