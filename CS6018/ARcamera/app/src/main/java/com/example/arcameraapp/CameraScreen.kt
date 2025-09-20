package com.example.arcameraapp

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 相机状态
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var brightestPixel by remember { mutableStateOf<Pair<Float, Float>?>(null) }

    // 相机控制器
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or
                        CameraController.IMAGE_ANALYSIS
            )
        }
    }

    // 设置图像分析（寻找最亮像素点）
    LaunchedEffect(cameraController) {
        cameraController.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(context)
        ) { imageProxy ->
            findBrightestPixel(imageProxy) { x, y ->
                brightestPixel = Pair(x, y)
            }
            imageProxy.close()
        }
    }

    // 更新相机选择器
    LaunchedEffect(lensFacing) {
        cameraController.cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 相机预览
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    this.controller = cameraController
                    cameraController.bindToLifecycle(lifecycleOwner)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // 绘制亮点标记
        brightestPixel?.let { (x, y) ->
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                // 将相对坐标转换为屏幕坐标
                val screenX = x * size.width
                val screenY = y * size.height

                // 绘制圆形标记
                drawCircle(
                    color = androidx.compose.ui.graphics.Color.Red,
                    radius = 20f,
                    center = androidx.compose.ui.geometry.Offset(screenX, screenY)
                )

                // 绘制十字标记
                drawLine(
                    color = androidx.compose.ui.graphics.Color.White,
                    start = androidx.compose.ui.geometry.Offset(screenX - 30, screenY),
                    end = androidx.compose.ui.geometry.Offset(screenX + 30, screenY),
                    strokeWidth = 4f
                )
                drawLine(
                    color = androidx.compose.ui.graphics.Color.White,
                    start = androidx.compose.ui.geometry.Offset(screenX, screenY - 30),
                    end = androidx.compose.ui.geometry.Offset(screenX, screenY + 30),
                    strokeWidth = 4f
                )
            }
        }

        // 底部控制按钮
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 切换摄像头按钮
            FloatingActionButton(
                onClick = {
                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                        CameraSelector.LENS_FACING_FRONT
                    } else {
                        CameraSelector.LENS_FACING_BACK
                    }
                },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "switch camera"
                )
            }

            // 拍照按钮
            FloatingActionButton(
                onClick = {
                    capturePhoto(context, cameraController)
                },
                modifier = Modifier.size(72.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "take photo",
                    modifier = Modifier.size(32.dp)
                )
            }

            // 占位符，保持布局平衡
            Spacer(modifier = Modifier.size(56.dp))
        }
    }
}

// 寻找最亮像素点的函数
private fun findBrightestPixel(
    imageProxy: ImageProxy,
    onResult: (Float, Float) -> Unit
) {
    val buffer: ByteBuffer = imageProxy.planes[0].buffer
    val data = ByteArray(buffer.remaining())
    buffer.get(data)

    val width = imageProxy.width
    val height = imageProxy.height

    var maxBrightness = 0
    var brightestX = 0f
    var brightestY = 0f

    // 遍历像素寻找最亮点（简化版本，只检查Y通道）
    for (y in 0 until height step 10) { // 每10个像素采样一次以提高性能
        for (x in 0 until width step 10) {
            val pixelIndex = y * width + x
            if (pixelIndex < data.size) {
                val brightness = data[pixelIndex].toInt() and 0xFF
                if (brightness > maxBrightness) {
                    maxBrightness = brightness
                    brightestX = x.toFloat() / width
                    brightestY = y.toFloat() / height
                }
            }
        }
    }

    onResult(brightestX, brightestY)
}

// 拍照功能
private fun capturePhoto(context: Context, cameraController: LifecycleCameraController) {
    val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
        .format(System.currentTimeMillis())

    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ARCamera")
        }
    }

    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ).build()

    cameraController.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                Toast.makeText(context, "photo saved", Toast.LENGTH_SHORT).show()
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraScreen", "failed to take photo: ${exception.message}", exception)
                Toast.makeText(context, "failed to take photo", Toast.LENGTH_SHORT).show()
            }
        }
    )
}