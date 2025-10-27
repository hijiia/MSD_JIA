package com.example.camera.util

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.RequestBody.Companion.toRequestBody


/**
 * Upload JPEG to server: POST /api/upload/{fileName}
 */
object UploadUtils {

    private val http: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
            )
            .build()
    }

    suspend fun uploadPhoto(
        context: Context,
        serverBase: String, // e.g. "http://10.0.2.2:8080"
        token: String,
        fileUri: Uri,
        fileName: String
    ): Boolean {
        val input = context.contentResolver.openInputStream(fileUri) ?: return false

        val multipart = input.use { stream ->
            val reqBody = stream.readBytes().toRequestBody("image/jpeg".toMediaType())
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, reqBody)
                .build()
        }

        val req = Request.Builder()
            .url("$serverBase/api/upload/$fileName")
            .header("Authorization", "Bearer $token")
            .post(multipart)
            .build()

        return http.newCall(req).execute().use { it.isSuccessful }
    }
}