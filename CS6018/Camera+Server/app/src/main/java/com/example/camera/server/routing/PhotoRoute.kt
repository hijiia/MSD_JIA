package com.example.camera.server.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.isRegularFile
import kotlin.io.path.name

private val uploadDir = Path("uploads/")

fun Route.photoRoute() {

    // GET /api/photos
    get {
        val username = extractPrincipalUsername(call)
        if (username == null) {
            call.respond(HttpStatusCode.Unauthorized)
            return@get
        }
        val files = listUserFiles(username)
        call.respond(files)
    }

    // GET /api/photos/{username}
    get("/{username}") {
        val reqName = call.parameters["username"]
        if (reqName.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }

        val thisUser = extractPrincipalUsername(call)
        if (thisUser == null) {
            call.respond(HttpStatusCode.Unauthorized)
            return@get
        }

        if (thisUser != reqName) {
            // not found
            call.respond(HttpStatusCode.NotFound)
            return@get
        }

        val files = listUserFiles(reqName)
        call.respond(files)
    }
    // GET /api/photos/raw/{filename}
    get("/raw/{filename}") {
        val username = extractPrincipalUsername(call) ?: return@get call.respond(HttpStatusCode.Unauthorized)
        val file = call.parameters["filename"]?.trim().orEmpty()
        if (file.isEmpty()) return@get call.respond(HttpStatusCode.BadRequest)

        val path = (uploadDir / username / file).toFile()
        if (!path.exists() || !path.isFile) return@get call.respond(HttpStatusCode.NotFound)

        val contentType = when {
            file.endsWith(".jpg", true) || file.endsWith(".jpeg", true) -> ContentType.Image.JPEG
            file.endsWith(".png", true) -> ContentType.Image.PNG
            else -> ContentType.Application.OctetStream
        }
        call.response.headers.append(HttpHeaders.CacheControl, "no-store")
        call.respondFile(path)
    }
}

private fun listUserFiles(username: String): List<String> {
    val userDir = uploadDir / username
    if (!Files.exists(userDir)) return emptyList()

    //  DirectoryStream
    val result = mutableListOf<String>()
    Files.newDirectoryStream(userDir).use { ds ->
        for (p in ds) {
            if (p.isRegularFile()) {
                result += p.name
            }
        }
    }
    return result
}