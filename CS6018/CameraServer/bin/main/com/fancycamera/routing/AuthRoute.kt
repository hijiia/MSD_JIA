package com.fancycamera.routing

import com.fancycamera.service.JwtService
import com.fancycamera.service.LoginRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoute(jwtService: JwtService) {
    post {
        val loginRequest = call.receive<LoginRequest>()
        val token: String? = jwtService.createJwtToken(loginRequest)

        token?.let {
            call.respond(hashMapOf("token" to token))
        } ?: call.respond(HttpStatusCode.Unauthorized)
    }
}
