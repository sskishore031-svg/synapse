package com.siamts.synapse.ui.notes.components

import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.siamts.synapse.data.model.NoteItemDto
import com.siamts.synapse.ui.theme.AmberGold
import com.siamts.synapse.ui.theme.AmberGoldEnd
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.LocalThemeIsDark
import com.siamts.synapse.ui.theme.PurpleAccent
import com.siamts.synapse.ui.theme.SunsetOrange
import com.siamts.synapse.ui.theme.SunsetOrangeEnd
import com.siamts.synapse.ui.theme.SurfaceBorder
import com.siamts.synapse.ui.theme.SurfacePanel
import com.siamts.synapse.ui.theme.SurfacePanelElevated
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import kotlin.math.roundToInt

/**
 * Modern 3D Floating Note Viewer Dialog that renders saved notes with
 * realistic perspective depth, tactile 3D tilting, metallic badges,
 * and high-yield curriculum formatting.
 */
@Composable
fun ThreeDNoteViewerDialog(
    note: NoteItemDto,
    onDismiss: () -> Unit,
    onToggleSave: (() -> Unit)? = null,
    onToggleDownload: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val isDark = LocalThemeIsDark.current
    val clipboardManager = LocalClipboardManager.current
    val density = LocalDensity.current

    // Interactive 3D tilt angles based on finger drag
    var manualTiltX by remember { mutableFloatStateOf(0f) }
    var manualTiltY by remember { mutableFloatStateOf(0f) }

    val animatedTiltX by animateFloatAsState(
        targetValue = manualTiltX,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioLowBouncy),
        label = "3D Tilt X"
    )
    val animatedTiltY by animateFloatAsState(
        targetValue = manualTiltY,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioLowBouncy),
        label = "3D Tilt Y"
    )

    // Gentle ambient floating animation when not dragging
    val infiniteTransition = rememberInfiniteTransition(label = "Ambient 3D Float")
    val ambientFloatingY by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Ambient Y"
    )

    val ambientRotationZ by infiniteTransition.animateFloat(
        initialValue = -0.8f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Ambient Rot Z"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x99000000))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            // Clamp tilt angles to maximum of 22 degrees for pleasing perspective
                            manualTiltY = (manualTiltY + dragAmount.x * 0.12f).coerceIn(-22f, 22f)
                            manualTiltX = (manualTiltX - dragAmount.y * 0.12f).coerceIn(-22f, 22f)
                        },
                        onDragEnd = {
                            manualTiltX = 0f
                            manualTiltY = 0f
                        },
                        onDragCancel = {
                            manualTiltX = 0f
                            manualTiltY = 0f
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // 3D Perspective Card Container
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .graphicsLayer {
                        rotationX = animatedTiltX
                        rotationY = animatedTiltY
                        rotationZ = if (manualTiltX == 0f && manualTiltY == 0f) ambientRotationZ else 0f
                        translationY = if (manualTiltX == 0f && manualTiltY == 0f) ambientFloatingY else 0f
                        cameraDistance = 16f * density.density
                        shadowElevation = 32.dp.toPx()
                    }
            ) {
                // Outer 3D depth spine shadow layer (simulating stacked notebook depth)
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(x = 6.dp, y = 8.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF1E1B4B), Color(0xFF0F172A))
                            ),
                            shape = RoundedCornerShape(26.dp)
                        )
                )

                // Secondary 3D page edge layer
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(x = 3.dp, y = 4.dp)
                        .background(
                            color = if (isDark) SurfaceBorder else Color(0xFFE2E8F0),
                            shape = RoundedCornerShape(25.dp)
                        )
                )

                // Main 3D Card Body
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = if (isDark) {
                                    listOf(
                                        SunsetOrange.copy(alpha = 0.8f),
                                        AmberGold.copy(alpha = 0.7f),
                                        SunsetOrangeEnd.copy(alpha = 0.8f),
                                        AmberGold.copy(alpha = 0.4f)
                                    )
                                } else {
                                    listOf(
                                        Color.White,
                                        CyanAccent.copy(alpha = 0.6f),
                                        PurpleAccent.copy(alpha = 0.5f),
                                        Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanelElevated else Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Row 1: Subject Badge on left, Close button pinned cleanly on right
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 3D Subject Pill
                            Box(
                                modifier = Modifier
                                    .weight(1f, fill = false)
                                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp))
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(SunsetOrange, SunsetOrangeEnd)
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.wrapContentWidth()
                                ) {
                                    Text(text = note.subjectIcon, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = note.subjectName.uppercase(),
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Circular X Close Button (36.dp) pinned cleanly on the right
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(if (isDark) SurfacePanel else Color(0xFFF1F5F9), CircleShape)
                                    .border(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0), CircleShape)
                                    .clickable { onDismiss() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "✕",
                                    fontSize = 14.sp,
                                    color = if (isDark) TextSecondary else Color(0xFF64748B),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Row 2: Saved (Bookmark) pill and Share pill side-by-side below subject badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 3D Bookmark / Saved Badge
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isDark) SunsetOrange.copy(alpha = 0.2f) else Color(0xFFFEF3C7),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isDark) SunsetOrange.copy(alpha = 0.4f) else Color(0xFFFDE68A),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { onToggleSave?.invoke() }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = if (note.isSaved) "🔖" else "🏷️", fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (note.isSaved) "Saved" else "Bookmark",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) SunsetOrange else Color(0xFFB45309),
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }

                            // 3D Share with Friend Pill
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isDark) SurfacePanel else Color(0xFFE0F2FE),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isDark) SurfaceBorder else Color(0xFFBAE6FD),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        com.siamts.synapse.util.ShareUtils.shareNote(
                                            context = context,
                                            title = note.title,
                                            content = note.content,
                                            subject = note.subjectName,
                                            category = note.category
                                        )
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "📤", fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "Share",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) AmberGold else Color(0xFF0284C7),
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Note Title with 3D Depth
                        Text(
                            text = note.title,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDark) TextPrimary else Color(0xFF0F172A),
                            lineHeight = 25.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "📅 ${if (note.createdAt.isNotBlank()) note.createdAt else "Recently saved"} • ${note.category}",
                                fontSize = 11.sp,
                                color = if (isDark) TextSecondary else Color(0xFF64748B),
                                fontWeight = FontWeight.Medium
                            )
                            if (note.isDownloaded) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "💾 Offline Synced",
                                    fontSize = 11.sp,
                                    color = Color(0xFF16A34A),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = if (isDark) SurfaceBorder else Color(0xFFE2E8F0), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Scrollable Content Body with 3D Depth Styling
                        Column(
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .height(280.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val isSmartNote = note.content.contains("Key Definition", ignoreCase = true) ||
                                    note.content.contains("Exam Takeaways", ignoreCase = true) ||
                                    note.content.contains("Formulas", ignoreCase = true)

                            if (isSmartNote) {
                                // 3D Embossed Feature Container
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (isDark) SurfacePanel else Color(0xFFF0FDF4), RoundedCornerShape(14.dp))
                                        .border(1.dp, if (isDark) SunsetOrange.copy(alpha = 0.3f) else Color(0xFFBBF7D0), RoundedCornerShape(14.dp))
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = "✨", fontSize = 14.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "AI GENERATED HIGH-YIELD SUMMARY",
                                                color = if (isDark) AmberGold else Color(0xFF15803D),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.8.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = note.content,
                                            fontSize = 13.sp,
                                            color = if (isDark) TextPrimary else Color(0xFF1E293B),
                                            lineHeight = 20.sp
                                        )
                                    }
                                }
                            } else {
                                // Standard Note Content
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanel else Color(0xFFF8FAFC)),
                                    border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0))
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = note.content.ifBlank { "No detailed notes recorded for this item." },
                                            fontSize = 13.sp,
                                            color = if (isDark) TextSecondary else Color(0xFF334155),
                                            lineHeight = 21.sp
                                        )
                                    }
                                }
                            }

                            // 3D Interactivity Hint
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isDark) SurfacePanel else Color(0xFFF1F5F9), RoundedCornerShape(10.dp))
                                    .border(1.dp, if (isDark) SurfaceBorder else Color.Transparent, RoundedCornerShape(10.dp))
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "👆 Touch & drag anywhere to rotate this note in 3D perspective",
                                    color = if (isDark) TextSecondary else Color(0xFF64748B),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Bottom 3D Actions: 2 clean buttons (Copy and Done)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Copy Button
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .background(if (isDark) SurfacePanelElevated else Color.White, RoundedCornerShape(12.dp))
                                    .border(1.5.dp, if (isDark) SurfaceBorder else Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                                    .clickable {
                                        clipboardManager.setText(AnnotatedString("${note.title}\n\n${note.content}"))
                                        Toast.makeText(context, "Copied note to clipboard! 📋", Toast.LENGTH_SHORT).show()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "📋 Copy",
                                    color = if (isDark) TextPrimary else Color(0xFF1E293B),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }

                            // Done / Return Button
                            Button(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                listOf(SunsetOrange, SunsetOrangeEnd, AmberGold)
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Done ✓",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

