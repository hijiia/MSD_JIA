package com.example.camera.net

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.example.camera.SERVER_BASE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.buffer
import okio.source
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream


object PhotoApi {


    suspend fun listMyPhotos(token: String): List<String> = withContext(Dispatchers.IO) {
        val req = Request.Builder()
            .url("$SERVER_BASE/api/photos")
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        ApiClient.client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) return@use emptyList<String>()
            val txt = resp.body?.string().orEmpty()
            val arr = JSONArray(txt)
            buildList {
                for (i in 0 until arr.length()) add(arr.getString(i))
            }
        }
    }
    suspend fun upload(
        context: Context,
        token: String,
        fileUri: Uri,
        fileName: String
    ): Boolean = withContext(Dispatchers.IO) {

        val tmp = uriToTempFile(context, fileUri, fileName)
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)

            .addFormDataPart(
                name = "file",
                filename = fileName,
                body = tmp.asRequestBody("image/jpeg".toMediaType())
            )
            .build()

        val req = Request.Builder()
            .url("$SERVER_BASE/api/upload/$fileName")
            .addHeader("Authorization", "Bearer $token")
            .post(body)
            .build()

        ApiClient.client.newCall(req).execute().use { resp ->
            tmp.delete()
            resp.isSuccessful // 201 Created return true
        }
    }

    private fun uriToTempFile(context: Context, uri: Uri, fileName: String): File {
        val tmp = File(context.cacheDir, fileName)
        val cr: ContentResolver = context.contentResolver
        cr.openInputStream(uri).use { input ->
            FileOutputStream(tmp).use { out ->
                input?.copyTo(out)
            }
        }
        return tmp
    }
}