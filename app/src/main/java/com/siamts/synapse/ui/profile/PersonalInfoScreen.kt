package com.siamts.synapse.ui.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.PurpleAccent
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox
import java.io.File
import java.io.FileOutputStream

@Composable
fun PersonalInfoScreen(
    initialName: String = "Kishore",
    initialEmail: String = "kishore@gmail.com",
    initialPhone: String = "+91 98765-43210",
    initialBio: String = "Learning, exploring and building a better version of myself.",
    initialPhotoUri: String? = null,
    isSaving: Boolean = false,
    errorMessage: String? = null,
    onBackClick: () -> Unit,
    onSaveChangesClick: (name: String, email: String, phone: String, bio: String, photoUri: String?) -> Unit,
    onRemoveProfilePictureClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var name by remember(initialName) { mutableStateOf(initialName) }
    var email by remember(initialEmail) { mutableStateOf(initialEmail) }
    var photoUri by remember(initialPhotoUri) { mutableStateOf(initialPhotoUri) }
    var phone by remember(initialPhone) { mutableStateOf(initialPhone) }
    var bio by remember(initialBio) { mutableStateOf(initialBio) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {}

            val savedUri = copyUriToInternalStorage(context, uri)
            photoUri = savedUri ?: uri.toString()
        }
    }

    val initials = remember(name) {
        name.trim().split(" ").filter { it.isNotEmpty() }.let { parts ->
            if (parts.size >= 2) {
                "${parts.first().first()}${parts.last().first()}".uppercase()
            } else if (parts.isNotEmpty() && parts.first().isNotEmpty()) {
                parts.first().take(2).uppercase()
            } else {
                "S"
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkBackgroundTop,
                        Color(0xFF090D22),
                        DarkBackgroundBottom,
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Top Bar
            DashedBorderBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentPadding = 6.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF161F42), CircleShape)
                    ) {
                        Text(text = "←", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "PROFILE SETTINGS",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Personal Info",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Light Form Sheet
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    if (!errorMessage.isNullOrBlank()) {
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Text(
                                text = errorMessage,
                                color = Color(0xFFDC2626),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Avatar Section
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 12.dp) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE0E7FF), CircleShape)
                                        .border(2.dp, BlueAccent.copy(alpha = 0.5f), CircleShape)
                                        .clickable {
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!photoUri.isNullOrEmpty()) {
                                        AsyncImage(
                                            model = photoUri,
                                            contentDescription = "Profile Picture",
                                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Text(
                                            text = initials,
                                            color = Color(0xFF3730A3),
                                            fontSize = 26.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Change Photo",
                                        color = BlueAccent,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.clickable {
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        }
                                    )

                                    if (!photoUri.isNullOrEmpty()) {
                                        Text(text = "•", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                        Text(
                                            text = "Remove Photo",
                                            color = Color(0xFFEF4444),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.clickable {
                                                photoUri = null
                                                onRemoveProfilePictureClick?.invoke()
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Edit Name
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 4.dp) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(text = "Full Name", color = Color(0xFF0F172A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = name,
                                    onValueChange = { name = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedTextColor = Color(0xFF0F172A),
                                        unfocusedTextColor = Color(0xFF0F172A),
                                        focusedBorderColor = BlueAccent,
                                        unfocusedBorderColor = Color(0xFFE2E8F0)
                                    ),
                                    singleLine = true
                                )
                            }
                        }

                        // Edit Email
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 4.dp) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(text = "Email Address", color = Color(0xFF0F172A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedTextColor = Color(0xFF0F172A),
                                        unfocusedTextColor = Color(0xFF0F172A),
                                        focusedBorderColor = BlueAccent,
                                        unfocusedBorderColor = Color(0xFFE2E8F0)
                                    ),
                                    singleLine = true
                                )
                            }
                        }

                        // Phone Number
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 4.dp) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(text = "Phone Number", color = Color(0xFF0F172A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = phone,
                                    onValueChange = { phone = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedTextColor = Color(0xFF0F172A),
                                        unfocusedTextColor = Color(0xFF0F172A),
                                        focusedBorderColor = BlueAccent,
                                        unfocusedBorderColor = Color(0xFFE2E8F0)
                                    ),
                                    singleLine = true
                                )
                            }
                        }

                        // Bio
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 4.dp) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(text = "Bio (Optional)", color = Color(0xFF0F172A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = bio,
                                    onValueChange = { bio = it },
                                    modifier = Modifier.fillMaxWidth().height(90.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedTextColor = Color(0xFF0F172A),
                                        unfocusedTextColor = Color(0xFF0F172A),
                                        focusedBorderColor = BlueAccent,
                                        unfocusedBorderColor = Color(0xFFE2E8F0)
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Save Changes Button Box
                    DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 4.dp) {
                        Button(
                            onClick = { onSaveChangesClick(name, email, phone, bio, photoUri) },
                            enabled = !isSaving,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Brush.horizontalGradient(listOf(PurpleAccent, BlueAccent, CyanAccent)), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (isSaving) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = "Saving...", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Text(text = "Save Changes", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Remove Profile Picture Button Box (Shown if photo exists)
                    if (!photoUri.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))

                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 4.dp) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .background(Color(0xFFFEF2F2), RoundedCornerShape(14.dp))
                                    .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(14.dp))
                                    .clickable {
                                        photoUri = null
                                        onRemoveProfilePictureClick?.invoke()
                                    },
                                contentAlignment = Alignment.Center,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🗑 ", fontSize = 14.sp)
                                    Text(text = "Remove Profile Picture", color = Color(0xFFDC2626), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Backward compatibility overload for 3-argument signature.
 */
@Composable
fun PersonalInfoScreen(
    initialName: String = "Kishore",
    initialEmail: String = "kishore@gmail.com",
    initialPhotoUri: String? = null,
    onBackClick: () -> Unit,
    onSaveChangesClick: (name: String, email: String, photoUri: String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    PersonalInfoScreen(
        initialName = initialName,
        initialEmail = initialEmail,
        initialPhotoUri = initialPhotoUri,
        onBackClick = onBackClick,
        onSaveChangesClick = { n, e, _, _, p -> onSaveChangesClick(n, e, p) },
        modifier = modifier
    )
}

private fun copyUriToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val profileDir = File(context.filesDir, "profile_images").apply { mkdirs() }
        val targetFile = File(profileDir, "profile_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(targetFile)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        targetFile.absolutePath
    } catch (_: Exception) {
        null
    }
}
