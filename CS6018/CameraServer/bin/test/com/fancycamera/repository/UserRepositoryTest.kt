package com.fancycamera.repository

import com.fancycamera.model.User
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserRepositoryTest {

    @Test
    fun `save should store user and return it`() {
        val repository = UserRepository()
        val user = User(
            id = UUID.randomUUID(),
            username = "testuser",
            hashedPassword = "hashedpassword"
        )

        val saved = repository.save(user)

        assertNotNull(saved)
        assertEquals(user.id, saved!!.id)
        assertEquals(user.username, saved.username)
        assertEquals(user.hashedPassword, saved.hashedPassword)
    }

    @Test
    fun `findByUsername should return user when it exists`() {
        val repository = UserRepository()
        val user = User(UUID.randomUUID(), "testuser", "hashedpassword")
        repository.save(user)

        val found = repository.findByUsername("testuser")

        assertNotNull(found)
        assertEquals("testuser", found!!.username)
    }

    @Test
    fun `findByUsername should return null when user does not exist`() {
        val repository = UserRepository()

        val found = repository.findByUsername("nonexistent")

        assertNull(found)
    }

    @Test
    fun `findById should return user when it exists`() {
        val repository = UserRepository()
        val user = User(UUID.randomUUID(), "testuser", "hashedpassword")
        repository.save(user)

        val found = repository.findById(user.id)

        assertNotNull(found)
        assertEquals(user.id, found.id)
    }

    @Test
    fun `user repository should handle multiple users`() {
        val repository = UserRepository()
        val user1 = User(UUID.randomUUID(), "user1", "hash1")
        val user2 = User(UUID.randomUUID(), "user2", "hash2")

        repository.save(user1)
        repository.save(user2)

        val found1 = repository.findByUsername("user1")
        val found2 = repository.findByUsername("user2")

        assertNotNull(found1)
        assertNotNull(found2)
        assertEquals("user1", found1!!.username)
        assertEquals("user2", found2!!.username)
    }
}
