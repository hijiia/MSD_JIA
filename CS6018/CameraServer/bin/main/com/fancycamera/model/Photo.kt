package com.fancycamera.model

import kotlinx.serialization.Serializable
import java.util.UUID

data class Photo(
    val id: UUID,
    val filename: String,
    val userId: UUID,
    val uploadedAt: Long
)

@Serializable
data class PhotoInfo(
    val filename: String,
    val uploadedAt: Long
)
