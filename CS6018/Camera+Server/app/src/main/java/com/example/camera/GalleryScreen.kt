package com.example.camera

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.camera.net.PhotoApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }
    var files by remember { mutableStateOf<List<String>>(emptyList()) }

    fun refresh() {
        val token = TokenStore.get(context)
        if (token == null) {
            errorText = "Please sign in first"
            return
        }
        loading = true
        errorText = null
        scope.launch {
            runCatching { PhotoApi.listMyPhotos(token) }
                .onSuccess { files = it }
                .onFailure { errorText = it.message ?: "Load failed" }
            loading = false
        }
    }

    LaunchedEffect(Unit) { refresh() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Photos") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                },
                actions = {
                    TextButton(onClick = { refresh() }) { Text("Refresh") }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            if (errorText != null) {
                Text(errorText!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(12.dp))
            }
            if (loading) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }

            val token = TokenStore.get(context)
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 120.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(files) { name ->
                    val request = ImageRequest.Builder(context)
                        .data("$SERVER_BASE/api/upload/$name")
                        .apply {
                            if (token != null) addHeader("Authorization", "Bearer $token")
                        }
                        .crossfade(true)
                        .build()

                    Card {
                        AsyncImage(
                            model = request,
                            contentDescription = name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        )
                    }
                }
            }
        }
    }
}