package com.siamts.synapse.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.DarkBackground
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.LightBackground
import com.siamts.synapse.ui.theme.LightBorder
import com.siamts.synapse.ui.theme.LightTextPrimary
import com.siamts.synapse.ui.theme.LightTextSecondary
import com.siamts.synapse.ui.theme.LocalThemeIsDark
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

private data class OpenSourceLibrary(
    val name: String,
    val author: String,
    val licenseName: String,
    val version: String,
    val description: String,
    val licenseNotice: String
)

private const val APACHE_2_SUMMARY = """Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License."""

@Composable
fun OpenSourceLicensesScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark = LocalThemeIsDark.current

    val pageBgGradient = if (isDark) {
        listOf(DarkBackgroundTop, Color(0xFF090D22), DarkBackgroundBottom)
    } else {
        listOf(Color(0xFFE0E7FF), Color(0xFFEEF2F6), LightBackground)
    }

    val bodyBg = if (isDark) DarkBackground else Color(0xFFF8FAFC)
    val cardBg = if (isDark) Color(0xFF131C38) else Color.White
    val cardBorder = if (isDark) Color(0xFF1E2B58) else LightBorder
    val primaryText = if (isDark) TextPrimary else LightTextPrimary
    val secondaryText = if (isDark) Color(0xFF94A3B8) else LightTextSecondary

    var searchQuery by remember { mutableStateOf("") }
    var expandedLibraryName by remember { mutableStateOf<String?>(null) }

    val libraries = listOf(
        OpenSourceLibrary(
            name = "Jetpack Compose & AndroidX Libraries",
            author = "The Android Open Source Project (Google LLC)",
            licenseName = "Apache License 2.0",
            version = "2024.09.00 / 1.13.1",
            description = "Modern declarative UI toolkit, navigation foundations, animation runtimes, and lifecycle management for Android.",
            licenseNotice = APACHE_2_SUMMARY
        ),
        OpenSourceLibrary(
            name = "Kotlin Coroutines & Flow",
            author = "JetBrains s.r.o.",
            licenseName = "Apache License 2.0",
            version = "1.8.1",
            description = "Asynchronous non-blocking concurrency, structured concurrency primitives, and reactive StateFlow streams.",
            licenseNotice = APACHE_2_SUMMARY
        ),
        OpenSourceLibrary(
            name = "Room Database & SQLite",
            author = "The Android Open Source Project (Google LLC)",
            licenseName = "Apache License 2.0",
            version = "2.6.1",
            description = "Robust local persistence abstraction layer providing offline caching for student study notes, chat transcripts, and quiz records.",
            licenseNotice = APACHE_2_SUMMARY
        ),
        OpenSourceLibrary(
            name = "Google LiteRT / Play Services ML Kit",
            author = "Google LLC",
            licenseName = "Apache License 2.0",
            version = "17.0.0",
            description = "On-device neural inference acceleration and local speech-to-text audio recognition without external cloud latency.",
            licenseNotice = APACHE_2_SUMMARY
        ),
        OpenSourceLibrary(
            name = "Material Design 3 Components",
            author = "Google LLC",
            licenseName = "Apache License 2.0",
            version = "1.3.0",
            description = "Material You adaptive surfaces, color tokens, dynamic elevating containers, and accessible typography components.",
            licenseNotice = APACHE_2_SUMMARY
        ),
        OpenSourceLibrary(
            name = "Android Jetpack DataStore",
            author = "The Android Open Source Project (Google LLC)",
            licenseName = "Apache License 2.0",
            version = "1.1.1",
            description = "Transactional asynchronous key-value persistence for theme modes and user preference state.",
            licenseNotice = APACHE_2_SUMMARY
        )
    )

    val filteredLibraries = if (searchQuery.isBlank()) {
        libraries
    } else {
        libraries.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.author.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = pageBgGradient))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar Box
            DashedBorderBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentPadding = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBackClick) {
                        Text(
                            text = "←",
                            color = primaryText,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "LEGAL & CREDITS",
                            color = if (isDark) CyanAccent else BlueAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Open Source Licenses",
                            color = primaryText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Main Content Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        color = bodyBg,
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    // Search Filter Field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = "Search libraries or components...",
                                color = Color(0xFF94A3B8),
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Text(text = "🔍", fontSize = 14.sp)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = cardBg,
                            unfocusedContainerColor = cardBg,
                            focusedBorderColor = BlueAccent,
                            unfocusedBorderColor = cardBorder,
                            focusedTextColor = primaryText,
                            unfocusedTextColor = primaryText
                        ),
                        singleLine = true
                    )

                    // Scrollable Library List
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Header summary chip
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DEPENDENCIES & CREDITS",
                                color = if (isDark) CyanAccent else BlueAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${filteredLibraries.size} Libraries",
                                color = secondaryText,
                                fontSize = 11.sp
                            )
                        }

                        filteredLibraries.forEach { lib ->
                            val isExpanded = expandedLibraryName == lib.name

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedLibraryName = if (isExpanded) null else lib.name
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = lib.name,
                                                color = primaryText,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "${lib.author} • v${lib.version}",
                                                color = secondaryText,
                                                fontSize = 11.sp
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    BlueAccent.copy(alpha = 0.12f),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = lib.licenseName,
                                                color = BlueAccent,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = lib.description,
                                        color = secondaryText,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (isExpanded) "Tap to hide license notice ▲" else "Tap to view full license text ▼",
                                            color = BlueAccent,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    AnimatedVisibility(
                                        visible = isExpanded,
                                        enter = expandVertically(),
                                        exit = shrinkVertically()
                                    ) {
                                        Column(modifier = Modifier.padding(top = 10.dp)) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(
                                                        color = if (isDark) Color(0xFF090D22) else Color(0xFFF1F5F9),
                                                        shape = RoundedCornerShape(10.dp)
                                                    )
                                                    .border(
                                                        1.dp,
                                                        if (isDark) Color(0xFF1E2B58) else Color(0xFFE2E8F0),
                                                        RoundedCornerShape(10.dp)
                                                    )
                                                    .padding(10.dp)
                                            ) {
                                                Text(
                                                    text = lib.licenseNotice,
                                                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569),
                                                    fontSize = 11.sp,
                                                    lineHeight = 15.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Bottom Copyright Notice
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDark) Color(0xFF0F1A3A) else Color(0xFFEFF6FF)
                            )
                        ) {
                            Text(
                                text = "SYNAPSE AI expresses sincere gratitude to the open source community for powering world-class mobile educational software.",
                                color = secondaryText,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OpenSourceLicensesScreenPreview() {
    SYNAPSETheme {
        OpenSourceLicensesScreen(onBackClick = {}, modifier = Modifier.fillMaxSize())
    }
}

