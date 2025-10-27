package com.example.camera.server.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String
)