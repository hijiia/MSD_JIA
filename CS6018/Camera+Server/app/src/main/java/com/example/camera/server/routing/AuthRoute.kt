package com.example.camera.server.routing

import com.example.camera.server.routing.request.LoginRequest
import com.example.camera.server.service.JwtService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * route("/api/auth") { authRoute(jwtService) }
 * use：POST /api/auth
 */
fun Route.authRoute(jwtService: JwtService) {
    post {
        val loginRequest = try {
            call.receive<LoginRequest>()
        } catch (_: Exception) {
            call.respond(HttpStatusCode.BadRequest, "Invalid request body")
            return@post
        }

        val token = try {
            jwtService.createJwtToken(loginRequest)
        } catch (_: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Auth service error")
            return@post
        }

        if (token.isNullOrBlank()) {
            call.respond(HttpStatusCode.Unauthorized)
            return@post
        }

        call.respond(mapOf("token" to token))
    }
}
