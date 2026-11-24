package com.siamts.synapse.ui.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.siamts.synapse.ui.theme.AmberGold
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.CyanGlow
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.LocalThemeIsDark
import com.siamts.synapse.ui.theme.PurpleAccent
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.SunsetOrange
import com.siamts.synapse.ui.theme.SunsetOrangeEnd
import com.siamts.synapse.ui.theme.SurfaceBorder
import com.siamts.synapse.ui.theme.SurfacePanel
import com.siamts.synapse.ui.theme.SurfacePanelElevated
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

@Composable
fun ProfileSetupScreen(
    viewModel: ProfileSetupViewModel,
    modifier: Modifier = Modifier,
    onContinueClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileSetupContent(
        uiState = uiState,
        onNameChange = { viewModel.onNameChanged(it) },
        onCourseChange = { viewModel.onCourseSelected(it) },
        onLanguageChange = { viewModel.onLanguageSelected(it) },
        onToggleInterest = { viewModel.onToggleInterest(it) },
        onContinueClick = onContinueClick,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileSetupContent(
    uiState: ProfileSetupUiState,
    onNameChange: (String) -> Unit,
    onCourseChange: (String) -> Unit,
    onLanguageChange: (String) -> Unit,
    onToggleInterest: (String) -> Unit,
    onContinueClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark = LocalThemeIsDark.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isDark) listOf(
                        DarkBackgroundTop,
                        Color(0xFF090D22),
                        DarkBackgroundBottom,
                    ) else listOf(
                        Color(0xFFF5F6FA),
                        Color(0xFFF5F6FA)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            // Top Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBackClick) {
                        Text(
                            text = "←",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "LET'S GET TO KNOW YOU",
                            color = SunsetOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Make it yours",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(if (isDark) SurfacePanel else Color(0xFF161F42), CircleShape)
                            .clickable { },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "•••",
                            color = if (isDark) SunsetOrange else TextSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            // Sheet Card Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        color = if (isDark) Color.Transparent else Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    )
            ) {
                Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
                    // Scrollable Form Fields
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 1. Step Progress Indicator Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                LinearProgressIndicator(
                                    progress = { uiState.currentStep.toFloat() / uiState.totalSteps },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(6.dp),
                                    color = SunsetOrange,
                                    trackColor = if (isDark) Color(0xFF25221F) else Color(0xFFE2E8F0),
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "${uiState.currentStep} of ${uiState.totalSteps}",
                                    color = if (isDark) TextSecondary else Color(0xFF64748B),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }

                        // 2. Avatar & Subtitle Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(if (isDark) SunsetOrange.copy(alpha = 0.2f) else Color(0xFFE0F2FE), CircleShape)
                                        .border(2.dp, if (isDark) SunsetOrange.copy(alpha = 0.5f) else Color(0xFFBAE6FD), CircleShape),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = uiState.initials,
                                        color = SunsetOrange,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "A learning path, just for you.",
                                        color = if (isDark) TextPrimary else Color(0xFF0F172A),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Add profile photo",
                                        color = SunsetOrange,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.clickable { },
                                    )
                                }
                            }
                        }

                        // 3. Name Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Name",
                                    color = if (isDark) TextPrimary else Color(0xFF1E293B),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                OutlinedTextField(
                                    value = uiState.name,
                                    onValueChange = onNameChange,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SunsetOrange,
                                        unfocusedBorderColor = if (isDark) SurfaceBorder else Color(0xFFE2E8F0),
                                        focusedContainerColor = if (isDark) SurfacePanel else Color.White,
                                        unfocusedContainerColor = if (isDark) SurfacePanel else Color.White,
                                        focusedTextColor = if (isDark) TextPrimary else Color(0xFF0F172A),
                                        unfocusedTextColor = if (isDark) TextPrimary else Color(0xFF0F172A),
                                    ),
                                    singleLine = true,
                                    trailingIcon = {
                                        Text(text = "👤", fontSize = 15.sp, color = if (isDark) TextSecondary else Color(0xFF64748B))
                                    }
                                )
                            }
                        }

                        // 4. Class / Course Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Class / Course",
                                    color = if (isDark) TextPrimary else Color(0xFF1E293B),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                OutlinedTextField(
                                    value = uiState.selectedCourse,
                                    onValueChange = onCourseChange,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SunsetOrange,
                                        unfocusedBorderColor = if (isDark) SurfaceBorder else Color(0xFFE2E8F0),
                                        focusedContainerColor = if (isDark) SurfacePanel else Color.White,
                                        unfocusedContainerColor = if (isDark) SurfacePanel else Color.White,
                                        focusedTextColor = if (isDark) TextPrimary else Color(0xFF0F172A),
                                        unfocusedTextColor = if (isDark) TextPrimary else Color(0xFF0F172A),
                                    ),
                                    singleLine = true,
                                    trailingIcon = {
                                        Text(text = "∨", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (isDark) TextSecondary else Color(0xFF64748B))
                                    }
                                )
                            }
                        }

                        // 5. Preferred Language Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Preferred language",
                                    color = if (isDark) TextPrimary else Color(0xFF1E293B),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                OutlinedTextField(
                                    value = uiState.preferredLanguage,
                                    onValueChange = onLanguageChange,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SunsetOrange,
                                        unfocusedBorderColor = if (isDark) SurfaceBorder else Color(0xFFE2E8F0),
                                        focusedContainerColor = if (isDark) SurfacePanel else Color.White,
                                        unfocusedContainerColor = if (isDark) SurfacePanel else Color.White,
                                        focusedTextColor = if (isDark) TextPrimary else Color(0xFF0F172A),
                                        unfocusedTextColor = if (isDark) TextPrimary else Color(0xFF0F172A),
                                    ),
                                    singleLine = true,
                                    trailingIcon = {
                                        Text(text = "文A", fontSize = 14.sp, color = if (isDark) TextSecondary else Color(0xFF64748B))
                                    }
                                )
                            }
                        }

                        // 6. Learning Interests Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Learning interests",
                                    color = if (isDark) TextPrimary else Color(0xFF1E293B),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Choose subjects you'd like to explore.",
                                    color = if (isDark) TextSecondary else Color(0xFF64748B),
                                    fontSize = 11.sp,
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    uiState.availableInterests.forEach { interest ->
                                        val isSelected = uiState.selectedInterests.contains(interest)
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    color = if (isSelected) (if (isDark) SunsetOrange.copy(alpha = 0.2f) else Color(0xFFEFF6FF)) else (if (isDark) SurfacePanel else Color.White),
                                                    shape = RoundedCornerShape(20.dp)
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = if (isSelected) SunsetOrange else (if (isDark) SurfaceBorder else Color(0xFFE2E8F0)),
                                                    shape = RoundedCornerShape(20.dp)
                                                )
                                                .clickable { onToggleInterest(interest) }
                                                .padding(horizontal = 10.dp, vertical = 5.dp),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                text = if (isSelected) "✓ $interest" else interest,
                                                color = if (isSelected) SunsetOrange else (if (isDark) TextSecondary else Color(0xFF475569)),
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Pinned Sticky Bottom Section: Always Visible Continue Button
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isDark) DarkBackgroundBottom else Color(0xFFF8FAFC))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Dashed Action Button Box: Continue Button
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 4.dp
                        ) {
                            Button(
                                onClick = onContinueClick,
                                enabled = !uiState.isLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                contentPadding = PaddingValues(0.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(
                                                    SunsetOrange,
                                                    SunsetOrangeEnd,
                                                    AmberGold,
                                                )
                                            ),
                                            shape = RoundedCornerShape(16.dp),
                                        ),
                                        contentAlignment = Alignment.Center,
                                ) {
                                    if (uiState.isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "→",
                                                color = Color.White,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Continue",
                                                color = Color.White,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Footer Disclaimer Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 4.dp
                        ) {
                            Text(
                                text = "You can change these preferences anytime.",
                                color = if (isDark) TextSecondary else Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileSetupScreenPreview() {
    SYNAPSETheme {
        ProfileSetupContent(
            uiState = ProfileSetupUiState(),
            onNameChange = {},
            onCourseChange = {},
            onLanguageChange = {},
            onToggleInterest = {},
            onContinueClick = {},
            onBackClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
