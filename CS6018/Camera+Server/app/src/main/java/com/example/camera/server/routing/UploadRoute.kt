package com.example.camera.server.routing

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.nio.file.Files.createDirectories
import kotlin.io.path.Path
import kotlin.io.path.div

/**
 * authenticate { route("/api/upload") { uploadRoute() } }
 *
 * GET  /api/upload/{filename}
 * POST /api/upload/{filename}
 */
private val uploadDir = Path("uploads/")

fun Route.uploadRoute() {

    get("/{filename}") {
        val username = extractPrincipalUsername(call)
        if (username == null) {
            call.respond(HttpStatusCode.Unauthorized)
            return@get
        }

        val filenameParam = call.parameters["filename"]
        if (filenameParam.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }

        val filename = sanitizeName(filenameParam)
        val file = (uploadDir / username / filename).toFile()
        if (!file.exists()) {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }
        call.respondFile(file)
    }

    post("/{filename}") {
        val username = extractPrincipalUsername(call)
        if (username == null) {
            call.respond(HttpStatusCode.Unauthorized)
            return@post
        }

        val filenameParam = call.parameters["filename"]
        if (filenameParam.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val filename = sanitizeName(filenameParam)

        createDirectories(uploadDir / username)
        val target = (uploadDir / username / filename).toFile()

        val multipart = try {
            call.receiveMultipart()
        } catch (_: Exception) {
            call.respond(HttpStatusCode.BadRequest, "Invalid multipart body")
            return@post
        }

        var wrote = false
        multipart.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    part.streamProvider().use { input ->
                        target.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    wrote = true
                }
                else -> Unit
            }
            part.dispose()
        }

        if (!wrote) {
            call.respond(HttpStatusCode.BadRequest, "No file part found")
            return@post
        }

        call.respond(HttpStatusCode.Created)
    }
}

private fun sanitizeName(raw: String): String =
    raw.replace(Regex("""[^A-Za-z0-9._-]"""), "_")
