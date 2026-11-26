package com.siamts.synapse.ui.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Matrix
import android.graphics.Paint
import android.media.ExifInterface
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.siamts.synapse.ui.theme.LocalThemeIsDark
import com.siamts.synapse.ui.theme.SunsetOrange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max

/**
 * Interactive Profile Picture Crop Modal Dialog.
 *
 * Provides:
 * - Circular viewport masking with semi-transparent outer scrim.
 * - Interactive pinch-to-zoom (1x up to 4x).
 * - Drag/Pan translation to frame the subject.
 * - 90-degree clockwise rotation button.
 * - Reset button to center framing.
 * - Done/Save button that crops the exact visible area to a crisp 512x512 JPEG,
 *   persists it to internal storage, and passes the cropped file URI to the caller.
 */
@Composable
fun ImageCropDialog(
    imageUri: Uri,
    userId: Int = 1,
    onCropSuccess: (croppedUriString: String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isDark = LocalThemeIsDark.current

    var loadedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var userScale by remember { mutableFloatStateOf(1f) }
    var userOffsetX by remember { mutableFloatStateOf(0f) }
    var userOffsetY by remember { mutableFloatStateOf(0f) }
    var rotationDegrees by remember { mutableFloatStateOf(0f) }

    val viewportSizeDp = 280.dp
    val density = LocalDensity.current
    val viewportPx = with(density) { viewportSizeDp.toPx() }

    // Load and decode the source Bitmap safely with EXIF orientation correction
    LaunchedEffect(imageUri) {
        isLoading = true
        errorMessage = null
        withContext(Dispatchers.IO) {
            try {
                val bitmap = decodeSampledBitmapFromUri(context, imageUri, 2048)
                if (bitmap != null) {
                    val oriented = applyExifOrientation(context, imageUri, bitmap)
                    loadedBitmap = oriented
                } else {
                    errorMessage = "Unable to load selected image."
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Failed to read image."
            } finally {
                isLoading = false
            }
        }
    }

    Dialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val cardBg = if (isDark) Color(0xFF14161D) else Color.White
        val textPrimaryColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F172A)
        val textSecondaryColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (isDark) Color(0xFF2B2F3D) else Color(0xFFE2E8F0), RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Crop Profile Picture",
                                color = textPrimaryColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Pinch to zoom · Drag to adjust",
                                color = textSecondaryColor,
                                fontSize = 12.sp
                            )
                        }

                        // Close button
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0xFF222633) else Color(0xFFF1F5F9))
                                .clickable(enabled = !isSaving) { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "✕", color = textSecondaryColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .size(viewportSizeDp)
                                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = SunsetOrange, modifier = Modifier.size(36.dp))
                        }
                    } else if (errorMessage != null || loadedBitmap == null) {
                        Box(
                            modifier = Modifier
                                .size(viewportSizeDp)
                                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = errorMessage ?: "Failed to load image",
                                color = Color(0xFFEF4444),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        val bitmap = loadedBitmap!!

                        // Compute initial fit so the image completely covers the circular viewport
                        val initialScale = remember(bitmap, rotationDegrees) {
                            val isSwapped = (rotationDegrees.toInt() % 180 != 0)
                            val w = if (isSwapped) bitmap.height else bitmap.width
                            val h = if (isSwapped) bitmap.width else bitmap.height
                            max(viewportPx / w.toFloat(), viewportPx / h.toFloat())
                        }

                        // Viewport Box with Transform Gestures and Circular Overlay
                        Box(
                            modifier = Modifier
                                .size(viewportSizeDp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color.Black)
                                .clipToBounds()
                                .pointerInput(Unit) {
                                    detectTransformGestures { _, pan, zoom, _ ->
                                        userScale = (userScale * zoom).coerceIn(0.8f, 5.0f)
                                        userOffsetX += pan.x
                                        userOffsetY += pan.y
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            // The Image being transformed
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Crop Target",
                                modifier = Modifier
                                    .size(
                                        width = with(density) { bitmap.width.toDp() },
                                        height = with(density) { bitmap.height.toDp() }
                                    )
                                    .graphicsLayer {
                                        scaleX = initialScale * userScale
                                        scaleY = initialScale * userScale
                                        translationX = userOffsetX
                                        translationY = userOffsetY
                                        rotationZ = rotationDegrees
                                    }
                            )

                            // Circular Mask Overlay: Dims outer area and highlights circular crop zone
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val radius = size.minDimension / 2f
                                val center = Offset(size.width / 2f, size.height / 2f)

                                val maskPath = Path().apply {
                                    addRect(Rect(0f, 0f, size.width, size.height))
                                    addOval(
                                        Rect(
                                            center.x - radius,
                                            center.y - radius,
                                            center.x + radius,
                                            center.y + radius
                                        )
                                    )
                                    fillType = PathFillType.EvenOdd
                                }
                                drawPath(maskPath, color = Color.Black.copy(alpha = 0.65f))

                                // Crisp glowing Sunset Orange border
                                drawCircle(
                                    color = SunsetOrange,
                                    radius = radius - 1.dp.toPx(),
                                    center = center,
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Secondary Tools: Rotate 90° and Reset
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                rotationDegrees = (rotationDegrees + 90f) % 360f
                                userOffsetX = 0f
                                userOffsetY = 0f
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(36.dp),
                            enabled = !isSaving && loadedBitmap != null
                        ) {
                            Text(text = "🔄 Rotate 90°", fontSize = 12.sp, color = textPrimaryColor)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        OutlinedButton(
                            onClick = {
                                userScale = 1f
                                userOffsetX = 0f
                                userOffsetY = 0f
                                rotationDegrees = 0f
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(36.dp),
                            enabled = !isSaving && loadedBitmap != null
                        ) {
                            Text(text = "↺ Reset", fontSize = 12.sp, color = textSecondaryColor)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Buttons: Cancel and Done / Set Photo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            enabled = !isSaving
                        ) {
                            Text(text = "Cancel", color = textSecondaryColor, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                val srcBitmap = loadedBitmap ?: return@Button
                                isSaving = true
                                scope.launch(Dispatchers.IO) {
                                    try {
                                        val isSwapped = (rotationDegrees.toInt() % 180 != 0)
                                        val w = if (isSwapped) srcBitmap.height else srcBitmap.width
                                        val h = if (isSwapped) srcBitmap.width else srcBitmap.height
                                        val initialScale = max(viewportPx / w.toFloat(), viewportPx / h.toFloat())
                                        val totalScale = initialScale * userScale

                                        val cropped = renderCroppedBitmap(
                                            source = srcBitmap,
                                            viewportPx = viewportPx,
                                            scale = totalScale,
                                            offsetX = userOffsetX,
                                            offsetY = userOffsetY,
                                            rotation = rotationDegrees,
                                            targetSize = 512
                                        )

                                        val avatarsDir = File(context.filesDir, "avatars").apply { if (!exists()) mkdirs() }
                                        val croppedFile = File(avatarsDir, "avatar_${userId}_${System.currentTimeMillis()}.jpg")
                                        FileOutputStream(croppedFile).use { out ->
                                            cropped.compress(Bitmap.CompressFormat.JPEG, 92, out)
                                        }

                                        withContext(Dispatchers.Main) {
                                            isSaving = false
                                            onCropSuccess(croppedFile.toURI().toString())
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) {
                                            isSaving = false
                                            errorMessage = e.localizedMessage ?: "Failed to crop image."
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange),
                            enabled = !isSaving && loadedBitmap != null
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Text(text = "✓ Set Photo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Mathematically extracts and renders the visible viewport area into a crisp square Bitmap.
 */
private fun renderCroppedBitmap(
    source: Bitmap,
    viewportPx: Float,
    scale: Float,
    offsetX: Float,
    offsetY: Float,
    rotation: Float,
    targetSize: Int = 512
): Bitmap {
    val output = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(output)

    val outputScale = targetSize.toFloat() / viewportPx
    val matrix = Matrix()

    // 1. Center the source image
    matrix.postTranslate(-source.width / 2f, -source.height / 2f)

    // 2. Rotate according to user rotation
    matrix.postRotate(rotation)

    // 3. Scale by total scale (initial fit * user pinch scale)
    matrix.postScale(scale, scale)

    // 4. Translate by user drag offset + center in viewport
    matrix.postTranslate(viewportPx / 2f + offsetX, viewportPx / 2f + offsetY)

    // 5. Scale from viewport coordinate system up/down to target 512x512 output
    matrix.postScale(outputScale, outputScale)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    canvas.drawBitmap(source, matrix, paint)

    return output
}

/**
 * Safely decodes a downsampled bitmap from URI avoiding OutOfMemoryError.
 */
private fun decodeSampledBitmapFromUri(context: Context, uri: Uri, maxDimension: Int = 2048): Bitmap? {
    val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use { stream ->
        BitmapFactory.decodeStream(stream, null, boundsOptions)
    }

    if (boundsOptions.outWidth <= 0 || boundsOptions.outHeight <= 0) return null

    var inSampleSize = 1
    val maxActual = max(boundsOptions.outWidth, boundsOptions.outHeight)
    while (maxActual / inSampleSize > maxDimension) {
        inSampleSize *= 2
    }

    val decodeOptions = BitmapFactory.Options().apply {
        this.inSampleSize = inSampleSize
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }

    return context.contentResolver.openInputStream(uri)?.use { stream ->
        BitmapFactory.decodeStream(stream, null, decodeOptions)
    }
}

/**
 * Reads EXIF orientation and returns an oriented bitmap if necessary.
 */
private fun applyExifOrientation(context: Context, uri: Uri, source: Bitmap): Bitmap {
    val orientation = try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            val exif = ExifInterface(input)
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        } ?: ExifInterface.ORIENTATION_NORMAL
    } catch (_: Exception) {
        ExifInterface.ORIENTATION_NORMAL
    }

    val degrees = when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
        else -> 0f
    }

    if (degrees == 0f) return source

    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
}
