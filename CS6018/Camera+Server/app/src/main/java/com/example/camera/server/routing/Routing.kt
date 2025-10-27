// Routing.kt
package com.example.camera.server.routing

import com.example.camera.server.service.JwtService
import com.example.camera.server.service.UserService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    jwtService: JwtService,
    userService: UserService,
) {
    routing {

        route("/api/auth") { authRoute(jwtService) }   // POST /api/auth
        route("/api/user") { userRoute(userService) }  // POST /api/user

        // only authenticated users can access
        authenticate {
            route("/api/upload") { uploadRoute() }     // POST /api/upload
            route("/api/photos") { photoRoute() }      // GET /api/photos /api/photos/{username} /api/photos/raw/{filename}
        }
    }
}

fun extractPrincipalUsername(call: ApplicationCall): String? =
    call.principal<JWTPrincipal>()
        ?.payload
        ?.getClaim("username")
        ?.asString()