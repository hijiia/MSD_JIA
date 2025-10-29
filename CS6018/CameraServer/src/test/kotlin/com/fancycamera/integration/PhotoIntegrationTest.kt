package com.fancycamera.integration

import com.fancycamera.module
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PhotoIntegrationTest {

    private suspend fun ApplicationTestBuilder.registerAndLogin(username: String, password: String): String {
        // Register
        client.post("/api/user") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"$username","password":"$password"}""")
        }

        // Login and get token
        val loginResponse = client.post("/api/auth") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"$username","password":"$password"}""")
        }

        val json = Json.parseToJsonElement(loginResponse.bodyAsText()).jsonObject
        return json["token"]?.jsonPrimitive?.content ?: ""
    }

    @Test
    fun `POST photo with valid token should return 201 Created`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "jwt.secret" to "test-secret-key",
                "jwt.issuer" to "http://0.0.0.0:8080/",
                "jwt.audience" to "http://0.0.0.0:8080/api",
                "jwt.realm" to "FancyCamera"
            )
        }
        application {
            module()
        }

        val token = registerAndLogin("photouser1", "password123")

        val response = client.post("/api/photos/test.jpg") {
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("image", "test image content".toByteArray(), Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=\"test.jpg\"")
                        })
                    }
                )
            )
        }

        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    fun `POST photo without token should return 401 Unauthorized`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "jwt.secret" to "test-secret-key",
                "jwt.issuer" to "http://0.0.0.0:8080/",
                "jwt.audience" to "http://0.0.0.0:8080/api",
                "jwt.realm" to "FancyCamera"
            )
        }
        application {
            module()
        }

        val response = client.post("/api/photos/test.jpg") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("image", "test image content".toByteArray())
                    }
                )
            )
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `GET photos should return user's photos only`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "jwt.secret" to "test-secret-key",
                "jwt.issuer" to "http://0.0.0.0:8080/",
                "jwt.audience" to "http://0.0.0.0:8080/api",
                "jwt.realm" to "FancyCamera"
            )
        }
        application {
            module()
        }

        val token = registerAndLogin("photouser2", "password123")

        // Upload a photo
        client.post("/api/photos/myPhoto.jpg") {
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("image", "test image content".toByteArray(), Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                        })
                    }
                )
            )
        }

        // Get photos
        val response = client.get("/api/photos") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val photos = Json.parseToJsonElement(response.bodyAsText()).jsonArray
        assertTrue(photos.size >= 1)

        val firstPhoto = photos[0].jsonObject
        assertEquals("myPhoto.jpg", firstPhoto["filename"]?.jsonPrimitive?.content)
    }

    @Test
    fun `GET photos without token should return 401 Unauthorized`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "jwt.secret" to "test-secret-key",
                "jwt.issuer" to "http://0.0.0.0:8080/",
                "jwt.audience" to "http://0.0.0.0:8080/api",
                "jwt.realm" to "FancyCamera"
            )
        }
        application {
            module()
        }

        val response = client.get("/api/photos")

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `GET specific photo should verify ownership`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "jwt.secret" to "test-secret-key",
                "jwt.issuer" to "http://0.0.0.0:8080/",
                "jwt.audience" to "http://0.0.0.0:8080/api",
                "jwt.realm" to "FancyCamera"
            )
        }
        application {
            module()
        }

        val user1Token = registerAndLogin("owner", "password123")
        val user2Token = registerAndLogin("other", "password123")

        // User 1 uploads photo
        client.post("/api/photos/private.jpg") {
            header(HttpHeaders.Authorization, "Bearer $user1Token")
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("image", "private content".toByteArray(), Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                        })
                    }
                )
            )
        }

        // User 2 cannot download User 1's photo (verifies ownership check)
        val otherResponse = client.get("/api/photos/private.jpg") {
            header(HttpHeaders.Authorization, "Bearer $user2Token")
        }
        assertEquals(HttpStatusCode.NotFound, otherResponse.status)
    }
}
