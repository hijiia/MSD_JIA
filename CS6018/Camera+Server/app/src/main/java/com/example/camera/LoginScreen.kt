
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.camera.TokenStore
import com.example.camera.SERVER_BASE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONObject
import com.example.camera.net.ApiClient

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegisterMode by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun showError(msg: String) { errorText = msg }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = if (isRegisterMode) "Create account" else "Sign in",
            style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = username, onValueChange = { username = it },
            label = { Text("Username") }, singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Password") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (errorText != null) {
            Text(errorText!!, color = MaterialTheme.colorScheme.error)
        }

        Button(
            enabled = !loading,
            onClick = {
                if (username.isBlank() || password.isBlank()) {
                    showError("Username and password required")
                    return@Button
                }
                loading = true
                errorText = null

                scope.launch {
                    val ok = try {
                        if (isRegisterMode) {
                            val reg = registerRequest(context, username.trim(), password)
                            if (reg) {
                                val token = loginRequest(context, username.trim(), password)
                                if (token != null) {
                                    TokenStore.save(context, token)
                                    onLoggedIn()
                                    true
                                } else false
                            } else false
                        } else {
                            val token = loginRequest(context, username.trim(), password)
                            if (token != null) {
                                TokenStore.save(context, token)
                                onLoggedIn()
                                true
                            } else false
                        }
                    } catch (e: Exception) {
                        showError(e.message ?: "Network error")
                        false
                    }
                    loading = false
                    if (!ok) showError(errorText ?: if (isRegisterMode) "Register failed" else "Login failed")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (loading) (if (isRegisterMode) "Creating..." else "Signing in...")
                else (if (isRegisterMode) "Create account" else "Sign in")
            )
        }

        TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
            Text(if (isRegisterMode) "Already have an account? Sign in" else "No account? Create one")
        }
    }
}

private val jsonMedia = "application/json; charset=utf-8".toMediaType()

private suspend fun registerRequest(context: Context, username: String, password: String): Boolean =
    withContext(Dispatchers.IO) {
        val bodyJson = JSONObject().apply {
            put("username", username); put("password", password)
        }.toString()

        val req = Request.Builder()
            .url("$SERVER_BASE/api/user")
            .post(bodyJson.toRequestBody(jsonMedia))
            .build()

        ApiClient.client.newCall(req).execute().use { it.isSuccessful }
    }

private suspend fun loginRequest(context: Context, username: String, password: String): String? =
    withContext(Dispatchers.IO) {
        val bodyJson = JSONObject().apply {
            put("username", username); put("password", password)
        }.toString()

        val req = Request.Builder()
            .url("$SERVER_BASE/api/auth")
            .post(bodyJson.toRequestBody(jsonMedia))
            .build()

        ApiClient.client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) return@use null
            val txt = resp.body?.string().orEmpty()
            val obj = JSONObject(txt)
            obj.optString("token", null)
        }
    }