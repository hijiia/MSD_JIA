package com.fancycamera.repository

import com.fancycamera.model.Photo
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PhotoRepositoryTest {

    @Test
    fun `save should store photo and return it`() {
        val repository = PhotoRepository()
        val userId = UUID.randomUUID()
        val photo = Photo(
            id = UUID.randomUUID(),
            filename = "test.jpg",
            userId = userId,
            uploadedAt = System.currentTimeMillis()
        )

        val saved = repository.save(photo)

        assertEquals(photo.id, saved.id)
        assertEquals(photo.filename, saved.filename)
        assertEquals(photo.userId, saved.userId)
    }

    @Test
    fun `findById should return photo when it exists`() {
        val repository = PhotoRepository()
        val photo = Photo(
            id = UUID.randomUUID(),
            filename = "test.jpg",
            userId = UUID.randomUUID(),
            uploadedAt = System.currentTimeMillis()
        )
        repository.save(photo)

        val found = repository.findById(photo.id)

        assertNotNull(found)
        assertEquals(photo.id, found.id)
    }

    @Test
    fun `findById should return null when photo does not exist`() {
        val repository = PhotoRepository()

        val found = repository.findById(UUID.randomUUID())

        assertNull(found)
    }

    @Test
    fun `findByUserId should return only user's photos`() {
        val repository = PhotoRepository()
        val user1Id = UUID.randomUUID()
        val user2Id = UUID.randomUUID()

        // User 1 photos
        repository.save(Photo(UUID.randomUUID(), "photo1.jpg", user1Id, System.currentTimeMillis()))
        repository.save(Photo(UUID.randomUUID(), "photo2.jpg", user1Id, System.currentTimeMillis()))

        // User 2 photos
        repository.save(Photo(UUID.randomUUID(), "photo3.jpg", user2Id, System.currentTimeMillis()))

        val user1Photos = repository.findByUserId(user1Id)

        assertEquals(2, user1Photos.size)
        assertTrue(user1Photos.all { it.userId == user1Id })
    }

    @Test
    fun `findByUserIdAndFilename should return correct photo`() {
        val repository = PhotoRepository()
        val userId = UUID.randomUUID()
        val filename = "test.jpg"

        repository.save(Photo(UUID.randomUUID(), filename, userId, System.currentTimeMillis()))
        repository.save(Photo(UUID.randomUUID(), "other.jpg", userId, System.currentTimeMillis()))

        val found = repository.findByUserIdAndFilename(userId, filename)

        assertNotNull(found)
        assertEquals(filename, found.filename)
        assertEquals(userId, found.userId)
    }

    @Test
    fun `findByUserIdAndFilename should return null when photo does not belong to user`() {
        val repository = PhotoRepository()
        val user1Id = UUID.randomUUID()
        val user2Id = UUID.randomUUID()

        repository.save(Photo(UUID.randomUUID(), "test.jpg", user1Id, System.currentTimeMillis()))

        val found = repository.findByUserIdAndFilename(user2Id, "test.jpg")

        assertNull(found)
    }

    @Test
    fun `deleteById should remove photo and return true`() {
        val repository = PhotoRepository()
        val photo = Photo(UUID.randomUUID(), "test.jpg", UUID.randomUUID(), System.currentTimeMillis())
        repository.save(photo)

        val deleted = repository.deleteById(photo.id)

        assertTrue(deleted)
        assertNull(repository.findById(photo.id))
    }
}
