package com.fancycamera.routing

import com.fancycamera.model.Photo
import com.fancycamera.model.PhotoInfo
import com.fancycamera.repository.PhotoRepository
import com.fancycamera.service.UserService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import java.nio.file.Files
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.div

val uploadDir = Path("uploads/")

fun Route.photoRoute(photoRepository: PhotoRepository, userService: UserService) {

    // Get list of user's photos
    get {
        val username = extractPrincipalUsername(call) ?: return@get call.respond(HttpStatusCode.Unauthorized)
        val user = userService.findByUsername(username) ?: return@get call.respond(HttpStatusCode.Unauthorized)

        val photos = photoRepository.findByUserId(user.id)
        val photoInfos = photos.map { PhotoInfo(it.filename, it.uploadedAt) }

        call.respond(photoInfos)
    }

    // Upload a photo
    post("/{filename}") {
        val username = extractPrincipalUsername(call) ?: return@post call.respond(HttpStatusCode.Unauthorized)
        val user = userService.findByUsername(username) ?: return@post call.respond(HttpStatusCode.Unauthorized)
        val filename = call.parameters["filename"] ?: return@post call.respond(HttpStatusCode.BadRequest)

        // Ensure user directory exists
        val userDir = uploadDir / username
        Files.createDirectories(userDir)

        val file = (userDir / filename).toFile()
        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 100) // 100MB limit

        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    part.provider().copyAndClose(file.writeChannel())
                }
                else -> {}
            }
            part.dispose()
        }

        // Save photo metadata
        photoRepository.save(
            Photo(
                id = UUID.randomUUID(),
                filename = filename,
                userId = user.id,
                uploadedAt = System.currentTimeMillis()
            )
        )

        call.respond(HttpStatusCode.Created)
    }

    // Download a photo
    get("/{filename}") {
        val username = extractPrincipalUsername(call) ?: return@get call.respond(HttpStatusCode.Unauthorized)
        val user = userService.findByUsername(username) ?: return@get call.respond(HttpStatusCode.Unauthorized)
        val filename = call.parameters["filename"] ?: return@get call.respond(HttpStatusCode.BadRequest)

        // Verify photo belongs to user
        val photo = photoRepository.findByUserIdAndFilename(user.id, filename)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        val file = (uploadDir / username / filename).toFile()
        if (file.exists()) {
            call.respondFile(file)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}
