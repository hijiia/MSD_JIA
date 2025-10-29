package com.fancycamera.routing

import com.fancycamera.repository.PhotoRepository
import com.fancycamera.repository.UserRepository
import com.fancycamera.service.JwtService
import com.fancycamera.service.UserService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    jwtService: JwtService,
    userService: UserService,
    photoRepository: PhotoRepository,
    userRepository: UserRepository
) {
    routing {
        route("/api/auth") {
            authRoute(jwtService)
        }

        route("/api/user") {
            userRoute(userService)
        }

        authenticate {
            route("/api/photos") {
                photoRoute(photoRepository, userService)
            }
        }
    }
}

fun extractPrincipalUsername(call: ApplicationCall): String? =
    call.principal<JWTPrincipal>()
        ?.payload
        ?.getClaim("username")
        ?.asString()
