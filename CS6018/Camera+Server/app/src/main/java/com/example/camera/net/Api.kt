package com.example.camera.net


import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject

object Api {
    var baseUrl = "http://10.0.2.2:8080"

    private val json = "application/json; charset=utf-8".toMediaType()

    val http: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
    }

    fun register(username: String, password: String): Boolean {
        val body = JSONObject().apply {
            put("username", username)
            put("password", password)
        }.toString().toRequestBody(json)

        val req = Request.Builder()
            .url("$baseUrl/api/users")
            .post(body)
            .build()

        return http.newCall(req).execute().use { it.isSuccessful }
    }

    fun login(username: String, password: String): String? {
        val body = JSONObject().apply {
            put("username", username)
            put("password", password)
        }.toString().toRequestBody(json)

        val req = Request.Builder()
            .url("$baseUrl/api/auth/login") // {token:"..."}
            .post(body)
            .build()

        return http.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) return@use null
            val txt = resp.body?.string() ?: return@use null
            runCatching { JSONObject(txt).optString("token", null) }.getOrNull()
        }
    }
}