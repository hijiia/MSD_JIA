package com.example.camera.server.routing

import com.example.camera.server.model.User
import com.example.camera.server.routing.request.UserRequest
import com.example.camera.server.routing.response.UserResponse
import com.example.camera.server.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

/**
 * route("/api/user") { userRoute(userService) }
 *
 *   POST /api/user
 *   GET  /api/user/getUser  (需要 JWT)
 */
fun Route.userRoute(userService: UserService) {

    // 注册
    post {
        // 1) 打印原始请求体并手动解析，避免 receive<T>() 的类型协商问题
        val raw = runCatching { call.receiveText() }.getOrElse { e ->
            println("❌ [UserRoute] read body error: ${e.message}")
            call.respond(HttpStatusCode.BadRequest, "Invalid request body")
            return@post
        }
        println("📦 [UserRoute] raw body = $raw")

        val userRequest = runCatching {
            Json { ignoreUnknownKeys = true }.decodeFromString<UserRequest>(raw)
        }.getOrElse { e ->
            println("❌ [UserRoute] decode error: ${e.message}")
            call.respond(HttpStatusCode.BadRequest, "Invalid request body")
            return@post
        }

        // 2) 基本校验
        if (userRequest.username.isBlank() || userRequest.password.isBlank()) {
            println("❌ [UserRoute] Empty username/password")
            call.respond(HttpStatusCode.BadRequest, "Username and password required")
            return@post
        }

        println("➡️  [UserRoute] Register request: ${userRequest.username}")

        // 3) 持久化（由 UserService 决定去重/保存逻辑）
        val created = try {
            userService.save(userRequest)
        } catch (e: Exception) {
            println("❌ [UserRoute] userService.save() error: ${e.message}")
            null
        }

        // 4) 结果处理
        if (created == null) {
            // 一般表示用户名已存在或存储失败
            println("❌ [UserRoute] Create user failed (duplicate or storage error): ${userRequest.username}")
            call.respond(HttpStatusCode.Conflict, "User exists or cannot be created")
            return@post
        }

        println("✅ [UserRoute] User created: ${created.username}")
        call.response.header("username", created.username)
        // 返回简单 JSON，便于端侧调试
        call.respond(HttpStatusCode.Created, mapOf("username" to created.username))
    }

    // 获取当前登录用户（需要 JWT）
    authenticate {
        get("/getUser") {
            val principal = call.principal<JWTPrincipal>()
            val username = principal?.payload?.getClaim("username")?.asString()
            if (username.isNullOrBlank()) {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }
            call.respond(UserResponse(username = username))
        }
    }
}

// 需要的话，这个转换保留（当前未直接使用）
private fun User.toResponse(): UserResponse = UserResponse(username = this.username)