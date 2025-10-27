package com.example.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.camera.server.AppServer
import com.example.camera.ui.theme.CameraTheme
import kotlinx.coroutines.flow.MutableStateFlow
import LoginScreen


enum class AppPage { LOGIN, CAMERA }

class MainActivity : ComponentActivity() {

    private val haveCameraPermissionsState = MutableStateFlow(false)

    // 权限 Launcher
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            haveCameraPermissionsState.value = granted
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1) 申请相机权限
        verifyPermissions()

        // 2) 启动内置 Ktor 服务器
        AppServer.start(port = 8080, host = "0.0.0.0")

        // 3) Compose UI
        setContent {
            CameraTheme {
                val haveCameraPermissions by haveCameraPermissionsState.collectAsState(initial = false)

                var page by rememberSaveable { mutableStateOf(AppPage.LOGIN) }
                var token by rememberSaveable { mutableStateOf<String?>(null) }

                Scaffold(modifier = Modifier.fillMaxSize()) { inner ->
                    Column(modifier = Modifier.padding(inner)) {
                        if (!haveCameraPermissions) {
                            Text("Need camera permissions")
                            return@Column
                        }

                        when (page) {
                            AppPage.LOGIN -> {
                              LoginScreen(
                                    onLoggedIn = {
                                        page = AppPage.CAMERA
                                    }
                                )
                            }

                            AppPage.CAMERA -> {
                                CameraScreen(
                                    authToken = token.orEmpty(),
                                    onRequestPermission = { verifyPermissions() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppServer.stop()
    }

    private fun verifyPermissions() {
        val granted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            haveCameraPermissionsState.value = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}