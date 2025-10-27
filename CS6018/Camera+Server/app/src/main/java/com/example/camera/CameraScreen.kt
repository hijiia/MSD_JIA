package com.example.camera

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.compose.CameraXViewfinder

import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.CameraSelector.DEFAULT_FRONT_CAMERA
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.setFrom
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.camera.util.UploadUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Composable
fun CameraScreen(
    authToken: String,
    onRequestPermission: () -> Unit,
    cameraViewModel: CameraViewModel = viewModel()
) {
    val currentScreen by cameraViewModel.currentScreen
    val capturedImageUri by cameraViewModel.capturedImageUri
    val isNavigating by cameraViewModel.isNavigating

    LaunchedEffect(currentScreen) {
        Log.d("CameraScreen", "Current screen = $currentScreen")
    }

    when (currentScreen) {
        is ScreenState.Camera -> {
            CameraPreviewScreen(
                authToken = authToken,
                onPhotoTaken = { uri ->
                    if (!isNavigating) {
                        cameraViewModel.navigateToPreview(uri)
                    }
                },
                viewModel = cameraViewModel
            )
        }
        is ScreenState.PhotoPreview -> {
            capturedImageUri?.let { uri ->
                PhotoPreviewScreen(
                    imageUri = uri,
                    onBackToCamera = { if (!isNavigating) cameraViewModel.navigateToCamera() },
                    onSaveToGallery = { /* 已在 capturePhoto() 保存 */ }
                )
            } ?: LaunchedEffect(Unit) { cameraViewModel.navigateToCamera() }
        }
    }
}

sealed class ScreenState {
    object Camera : ScreenState()
    object PhotoPreview : ScreenState()
}

@Composable
private fun CameraPreviewScreen(
    authToken: String,
    onPhotoTaken: (String) -> Unit,
    viewModel: CameraViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var cameraSelector by remember { mutableStateOf(DEFAULT_BACK_CAMERA) }

    val transformer = remember { MutableCoordinateTransformer() }
    var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var isCapturing by remember { mutableStateOf(false) }

    val detectedObjects by viewModel.detectedObjects
    val analyzerToBuffer by viewModel.analyzerToBuffer

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    LaunchedEffect(Unit) { HighlightAnalysis.DetectorHolder.activate() }
    DisposableEffect(Unit) { onDispose { HighlightAnalysis.DetectorHolder.deactivate() } }

    val objectDetectionAnalyzer = remember(context) {
        HighlightAnalysis.createObjectDetectionAnalyzer(
            context = context,
            executor = cameraExecutor,
            onDetectionResult = { objs, mat -> viewModel.updateDetectedObjects(objs, mat) }
        )
    }

    LaunchedEffect(cameraSelector) {
        try {
            val cameraProvider = ProcessCameraProvider.getInstance(context).get()

            val preview = Preview.Builder().build().apply {
                setSurfaceProvider { request -> surfaceRequest = request }
            }

            val imageCaptureUseCase = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            imageCapture = imageCaptureUseCase

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCaptureUseCase,
                objectDetectionAnalyzer
            )
        } catch (e: Exception) {
            Log.e("CameraPreviewScreen", "Camera init failed", e)
            Toast.makeText(context, "Camera init failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Box(Modifier.fillMaxSize()) {
        surfaceRequest?.let { request ->
            CameraXViewfinder(
                surfaceRequest = request,
                coordinateTransformer = transformer,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        val transformInfo by produceState<SurfaceRequest.TransformationInfo?>(
            initialValue = null,
            key1 = surfaceRequest
        ) {
            surfaceRequest?.setTransformationInfoListener(Runnable::run) { info -> value = info }
            try { awaitCancellation() } finally { surfaceRequest?.clearTransformationInfoListener() }
        }

        Canvas(Modifier.fillMaxSize()) {
            transformInfo?.let { info ->
                val bufferToUi = Matrix().apply {
                    setFrom(transformer.transformMatrix); invert()
                }
                val analysisToSensor = android.graphics.Matrix().also { analyzerToBuffer.invert(it) }
                val totalMatrix = analysisToSensor.apply { postConcat(info.sensorToBufferTransform) }
                val totalCompose = Matrix().apply { setFrom(totalMatrix) }

                detectedObjects.forEach { obj ->
                    val r = androidx.compose.ui.geometry.Rect(
                        obj.bbox.left, obj.bbox.top, obj.bbox.right, obj.bbox.bottom
                    )
                    val uiRect = bufferToUi.map(totalCompose.map(r))
                    drawRect(
                        color = Color.Red,
                        topLeft = Offset(uiRect.left, uiRect.top),
                        size = Size(uiRect.width, uiRect.height),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
                    )
                }
            }
        }

        Column(
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        ) {
            Text("Objects: ${detectedObjects.size}", color = Color.White)
        }

        FloatingActionButton(
            onClick = {
                cameraSelector =
                    if (cameraSelector == DEFAULT_BACK_CAMERA) DEFAULT_FRONT_CAMERA else DEFAULT_BACK_CAMERA
            },
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).size(56.dp),
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        ) {
            Icon(Icons.Default.Cameraswitch, contentDescription = "Switch",
                tint = MaterialTheme.colorScheme.onSurface)
        }

        Box(
            modifier = Modifier.align(Alignment.BottomCenter).padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            FloatingActionButton(
                onClick = {
                    if (!isCapturing) {
                        imageCapture?.let { cap ->
                            capturePhoto(
                                imageCapture = cap,
                                context = context,
                                authToken = authToken,
                                executor = cameraExecutor,
                                onImageCaptured = { uri ->
                                    onPhotoTaken(uri)
                                    isCapturing = false
                                },
                                onError = {
                                    isCapturing = false
                                    Toast.makeText(context, "Capture failed: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                            )
                            isCapturing = true
                        }
                    }
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(72.dp)
            ) {
                if (isCapturing) {
                    CircularProgressIndicator(Modifier.size(24.dp), color = Color.White)
                } else {
                    Icon(Icons.Default.PhotoCamera, contentDescription = "Take Photo",
                        tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}

private fun capturePhoto(
    imageCapture: ImageCapture,
    context: Context,
    authToken: String,
    executor: Executor,
    onImageCaptured: (String) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault())
        .format(System.currentTimeMillis())

    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraApp")
        }
    }

    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) = onError(exception)

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri: Uri = output.savedUri ?: return
                onImageCaptured(savedUri.toString())

                val scope = androidx.lifecycle.ProcessLifecycleOwner.get().lifecycleScope
                scope.launch {
                    try {
                        val ok = UploadUtils.uploadPhoto(
                            context = context,
                            serverBase = SERVER_BASE,
                            token = authToken,
                            fileUri = savedUri,
                            fileName = "$name.jpg"
                        )
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                if (ok) "Uploaded to server" else "Upload failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Upload error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                        Log.e("CameraScreen", "Upload error", e)
                    }
                }
            }
        }
    )
}