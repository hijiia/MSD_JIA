package com.example.camera.server.service

import com.example.camera.server.routing.request.LoginRequest
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.jwt.*
import java.util.*

class JwtService {

    // keys
    private val secret = "NRQbNGVtLF4+FZyRr7BbcIfTPWWvdxpfWrcfKrSD8Wg="
    private val issuer = "com.example.camera"
    private val audience = "camera_audience"
    private val expiration = 24 * 60 * 60 * 1000 // 1 day

    private val algorithm = Algorithm.HMAC256(secret)

    fun createJwtToken(loginRequest: LoginRequest): String? {
        val username = loginRequest.username.ifBlank { return null }

        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("username", username)
            .withExpiresAt(Date(System.currentTimeMillis() + expiration))
            .sign(algorithm)
    }

    fun getVerifier(): JWTVerifier =
        JWT.require(algorithm)
            .withAudience(audience)
            .withIssuer(issuer)
            .build()
}
