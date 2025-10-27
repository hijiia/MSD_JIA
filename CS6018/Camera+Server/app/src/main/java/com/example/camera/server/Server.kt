// Server.kt
package com.example.camera.server

import android.util.Log
import com.example.camera.server.routing.configureRouting
import com.example.camera.server.service.JwtService
import com.example.camera.server.service.UserService
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

object AppServer {

    @Volatile
    private var engine: ApplicationEngine? = null

    fun start(port: Int = 8080, host: String = "0.0.0.0") {
        if (engine != null) {
            Log.i("AppServer", "start() ignored: already running")
            return
        }

        val jwtService = JwtService()
        val userService = UserService()

        Log.i("AppServer", "Starting Ktor on $host:$port")

        engine = embeddedServer(CIO, port = port, host = host) {
            install(ContentNegotiation) { json() }

            install(Authentication) {
                jwt {
                    verifier(jwtService.getVerifier())
                    validate { cred ->
                        val name = cred.payload.getClaim("username").asString()
                        if (!name.isNullOrBlank()) JWTPrincipal(cred.payload) else null
                    }
                }
            }
            
            routing {
                get("/ping") { call.respondText("pong") }
            }

            // route
            configureRouting(jwtService, userService)
        }.also {
            it.start(wait = false)
            Log.i("AppServer", "Ktor started, isRunning=${isRunning()}")
        }
    }

    fun stop() {
        Log.i("AppServer", "Stopping Ktor")
        engine?.stop(gracePeriodMillis = 1_000, timeoutMillis = 5_000)
        engine = null
        Log.i("AppServer", "Ktor stopped")
    }

    fun isRunning(): Boolean = engine != null
}