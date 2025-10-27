package com.example.camera

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.camera.net.httpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request

@Composable
fun MyPhotosScreen(
    baseUrl: String,
    token: String,
) {
    val ctx = LocalContext.current
    var names by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        names = withContext(Dispatchers.IO) {
            val req = Request.Builder()
                .url("$baseUrl/api/photos")
                .header("Authorization", "Bearer $token")
                .get()
                .build()
            httpClient.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) emptyList()
                else resp.body?.string()
                    ?.removePrefix("[")?.removeSuffix("]")
                    ?.split(',')?.mapNotNull { it.trim().trim('"').ifBlank { null } }
                    ?: emptyList()
            }
        }
    }

    LazyColumn(Modifier.fillMaxSize().padding(12.dp)) {
        items(names) { name ->
            val url = "$baseUrl/api/upload/$name"
            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(ctx)
                        .data(url)
                        .addHeader("Authorization", "Bearer $token")
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.size(88.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(name, style = MaterialTheme.typography.bodyLarge)
            }
            Divider()
        }
    }
}

