package com.fancycamera.routing

import com.fancycamera.service.LoginRequest
import com.fancycamera.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoute(userService: UserService) {
    // Create a new user (register) - save to LDAP
    post {
        val userRequest = call.receive<LoginRequest>()
        val success = userService.save(userRequest.username, userRequest.password)

        if (success) {
            call.respond(HttpStatusCode.Created, mapOf("message" to "User created successfully"))
        } else {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "User creation failed - username may already exist"))
        }
    }
}
