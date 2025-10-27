package com.example.camera

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.exifinterface.media.ExifInterface
import android.util.Log

/**
 * Photo preview screen that displays captured images with polaroid-style effect.
 * Handles image rotation based on EXIF data and provides navigation controls.
 *
 * @param imageUri URI of the captured image
 * @param onBackToCamera Callback to return to camera screen
 * @param onSaveToGallery Callback to save image to gallery
 */
@Composable
fun PhotoPreviewScreen(
    imageUri: String,
    onBackToCamera: () -> Unit,
    onSaveToGallery: () -> Unit
) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var hasNavigated by remember { mutableStateOf(false) }

    // Safe navigation function to prevent multiple calls
    val safeNavigateBack = remember {
        {
            if (!hasNavigated) {
                hasNavigated = true
                Log.d("PhotoPreviewScreen", "Safe navigate back called")
                onBackToCamera()
            } else {
                Log.d("PhotoPreviewScreen", "Navigation already triggered, ignoring")
            }
            Unit
        }
    }

    // Reset navigation flag when imageUri changes
    LaunchedEffect(imageUri) {
        hasNavigated = false
        Log.d("PhotoPreviewScreen", "Reset navigation flag for URI: $imageUri")
    }

    /**
     * Gets the correct rotation angle from EXIF orientation data.
     *
     * @param uri Image URI to read EXIF data from
     * @return Rotation angle in degrees (0, 90, 180, or 270)
     */
    fun getImageRotation(uri: Uri): Float {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val exif = ExifInterface(inputStream)
                when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                    else -> 0f
                }
            } ?: 0f
        } catch (e: Exception) {
            Log.e("PhotoPreviewScreen", "Error reading EXIF data", e)
            0f
        }
    }

    /**
     * Rotates a bitmap by the specified degrees.
     *
     * @param bitmap Original bitmap
     * @param degrees Rotation angle in degrees
     * @return Rotated bitmap
     */
    fun rotateBitmap(bitmap: android.graphics.Bitmap, degrees: Float): android.graphics.Bitmap {
        return if (degrees == 0f) {
            bitmap
        } else {
            val matrix = Matrix()
            matrix.postRotate(degrees)
            android.graphics.Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
    }

    // Load bitmap from URI with correct orientation
    LaunchedEffect(imageUri) {
        try {
            val uri = Uri.parse(imageUri)
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                originalBitmap?.let {
                    val rotation = getImageRotation(uri)
                    bitmap = rotateBitmap(it, rotation)
                    Log.d("PhotoPreviewScreen", "Bitmap loaded and rotated by $rotation degrees")
                }
            }
        } catch (e: Exception) {
            Log.e("PhotoPreviewScreen", "Error loading image", e)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Photo display with polaroid-like effect
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(32.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(12.dp)
                ),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // The actual photo using Canvas
                bitmap?.let { bmp ->
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(3f / 4f)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        val imageBitmap = bmp.asImageBitmap()

                        // Calculate scaling to fit the canvas while maintaining aspect ratio
                        val imageAspectRatio = imageBitmap.width.toFloat() / imageBitmap.height.toFloat()
                        val canvasAspectRatio = size.width / size.height

                        val (drawWidth, drawHeight) = if (imageAspectRatio > canvasAspectRatio) {
                            // Image is wider than canvas - fit to width
                            size.width to size.width / imageAspectRatio
                        } else {
                            // Image is taller than canvas - fit to height
                            size.height * imageAspectRatio to size.height
                        }

                        val offsetX: Int = (size.width - drawWidth).toInt() / 2
                        val offsetY: Int = (size.height - drawHeight).toInt() / 2

                        drawImage(
                            image = imageBitmap,
                            dstOffset = IntOffset(offsetX, offsetY),
                            dstSize = IntSize(drawWidth.toInt(), drawHeight.toInt())
                        )
                    }
                } ?: run {
                    // Loading placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(3f / 4f)
                            .background(Color.Gray.copy(alpha = 0.3f))
                            .clip(RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Polaroid-style bottom space
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Back button
        FloatingActionButton(
            onClick = safeNavigateBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back to Camera",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        // Action buttons at the bottom
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Continue taking photos button - moved 5dp to the right
            Box(
                modifier = Modifier.offset(x = 15.dp)
            ) {
                FloatingActionButton(
                    onClick = safeNavigateBack,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        Icons.Default.PhotoCamera,
                        contentDescription = "Continue Taking Photos",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Save to gallery button
            FloatingActionButton(
                onClick = {
                    onSaveToGallery()
                    Toast.makeText(context, "Photo saved to gallery!", Toast.LENGTH_SHORT).show()
                },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    Icons.Default.Save,
                    contentDescription = "Save to Gallery",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Action labels - also adjust the "Continue" label to match button position
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 32.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(modifier = Modifier.offset(x = 5.dp)) {
                Text(
                    text = "Continue",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "Save",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}