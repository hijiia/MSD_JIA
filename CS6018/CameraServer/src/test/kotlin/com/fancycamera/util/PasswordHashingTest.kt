package com.fancycamera.util

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PasswordHashingTest {

    @Test
    fun `hashPassword should generate hash from password`() {
        val password = "mySecurePassword123"

        val hash = hashPassword(password)

        assertTrue(hash.isNotEmpty())
        assertNotEquals(password, hash)
    }

    @Test
    fun `hashPassword should generate different hashes for same password`() {
        val password = "mySecurePassword123"

        val hash1 = hashPassword(password)
        val hash2 = hashPassword(password)

        assertNotEquals(hash1, hash2) // BCrypt uses salt, so hashes should differ
    }

    @Test
    fun `verifyPassword should return true for correct password`() {
        val password = "mySecurePassword123"
        val hash = hashPassword(password)

        val isValid = verifyPassword(password, hash)

        assertTrue(isValid)
    }

    @Test
    fun `verifyPassword should return false for incorrect password`() {
        val password = "mySecurePassword123"
        val wrongPassword = "wrongPassword"
        val hash = hashPassword(password)

        val isValid = verifyPassword(wrongPassword, hash)

        assertFalse(isValid)
    }

    @Test
    fun `verifyPassword should handle empty passwords`() {
        val password = ""
        val hash = hashPassword(password)

        val isValid = verifyPassword(password, hash)

        assertTrue(isValid)
    }

    @Test
    fun `verifyPassword should return false for invalid hash format`() {
        val password = "mySecurePassword123"
        val invalidHash = "not-a-valid-bcrypt-hash"

        val isValid = verifyPassword(password, invalidHash)

        assertFalse(isValid)
    }
}
