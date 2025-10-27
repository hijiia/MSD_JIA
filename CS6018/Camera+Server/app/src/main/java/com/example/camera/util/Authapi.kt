package com.example.camera.util

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject

/**
 * Minimal client for your Ktor endpoints:
 *  POST /api/auth/login   -> { token: "..." }
 *  POST /api/user         -> register {username,password}
 */
object AuthApi {

    private val http by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
            .build()
    }

    suspend fun login(serverBase: String, username: String, password: String): String? {
        val bodyJson = JSONObject()
            .put("username", username)
            .put("password", password)
            .toString()
        val req = Request.Builder()
            .url("$serverBase/api/auth/login")
            .post(bodyJson.toRequestBody("application/json".toMediaType()))
            .build()

        return http.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) return null
            val txt = resp.body?.string() ?: return null
            // expected: { "token": "..." }
            runCatching { JSONObject(txt).optString("token", null) }.getOrNull()
        }
    }

    suspend fun register(serverBase: String, username: String, password: String): Boolean {
        val bodyJson = JSONObject()
            .put("username", username)
            .put("password", password)
            .toString()
        val req = Request.Builder()
            .url("$serverBase/api/user")
            .post(bodyJson.toRequestBody("application/json".toMediaType()))
            .build()

        return http.newCall(req).execute().use { it.isSuccessful }
    }
}