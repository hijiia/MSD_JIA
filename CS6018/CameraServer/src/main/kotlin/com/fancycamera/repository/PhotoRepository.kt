package com.fancycamera.repository

import com.fancycamera.model.Photo
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.Path
import kotlin.io.path.name
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.isDirectory

class PhotoRepository(private val uploadDir: Path = Path("uploads/")) {
    private val photos = ConcurrentHashMap<UUID, Photo>()

    init {
        // Scan upload directory and rebuild photo metadata
        rebuildPhotoMetadata()
    }

    private fun rebuildPhotoMetadata() {
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir)
                return
            }

            // Scan each user directory
            uploadDir.listDirectoryEntries().forEach { userDir ->
                if (userDir.isDirectory()) {
                    val username = userDir.name

                    // List all photos in user directory
                    userDir.listDirectoryEntries("*.jpg").forEach { photoPath ->
                        val filename = photoPath.name
                        val lastModified = Files.getLastModifiedTime(photoPath).toMillis()

                        // Create photo entry with generated UUID
                        val photo = Photo(
                            id = UUID.randomUUID(),
                            filename = filename,
                            userId = UUID.nameUUIDFromBytes(username.toByteArray()), // Derive UUID from username
                            uploadedAt = lastModified
                        )
                        photos[photo.id] = photo
                    }
                }
            }
            println("PhotoRepository: Rebuilt metadata for ${photos.size} photos")
        } catch (e: Exception) {
            println("PhotoRepository: Error rebuilding metadata: ${e.message}")
        }
    }

    fun save(photo: Photo): Photo {
        photos[photo.id] = photo
        return photo
    }

    fun findById(id: UUID): Photo? = photos[id]

    fun findByUserId(userId: UUID): List<Photo> =
        photos.values.filter { it.userId == userId }

    fun findByUserIdAndFilename(userId: UUID, filename: String): Photo? =
        photos.values.find { it.userId == userId && it.filename == filename }

    fun deleteById(id: UUID): Boolean =
        photos.remove(id) != null
}
