package com.example.camera.server.routing.response

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val username: String
)
