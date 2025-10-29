package com.fancycamera.integration

import com.fancycamera.module
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthenticationIntegrationTest {

    @Test
    fun `POST user registration should return 201 Created`() = testApplication {
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

        val response = client.post("/api/user") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"newuser","password":"password123"}""")
        }

        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    fun `POST user registration with existing username should return 400 Bad Request`() = testApplication {
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

        // First registration
        client.post("/api/user") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"duplicate","password":"password123"}""")
        }

        // Second registration with same username
        val response = client.post("/api/user") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"duplicate","password":"password123"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `POST auth login should return JWT token`() = testApplication {
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

        // Register user first
        client.post("/api/user") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"logintest","password":"password123"}""")
        }

        // Login
        val response = client.post("/api/auth") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"logintest","password":"password123"}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        val json = Json.parseToJsonElement(body).jsonObject
        assertTrue(json.containsKey("token"))
        assertTrue(json["token"]?.jsonPrimitive?.content?.isNotEmpty() == true)
    }

    @Test
    fun `POST auth with wrong password should return 401 Unauthorized`() = testApplication {
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

        // Register user
        client.post("/api/user") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"authtest","password":"correctpassword"}""")
        }

        // Login with wrong password
        val response = client.post("/api/auth") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"authtest","password":"wrongpassword"}""")
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `POST auth with non-existent user should return 401 Unauthorized`() = testApplication {
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

        val response = client.post("/api/auth") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"nonexistent","password":"password123"}""")
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }
}
