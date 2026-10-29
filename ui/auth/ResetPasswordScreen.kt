package com.siamts.synapse.ui.auth

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.PurpleAccent
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.welcome.components.BrainGraphic
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

@Composable
fun ResetPasswordScreen(
    token: String,
    viewModel: ResetPasswordViewModel,
    onBackToLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(token) {
        if (token.isNotEmpty()) {
            viewModel.setToken(token)
        }
    }

    ResetPasswordContent(
        uiState = uiState,
        onNewPasswordChange = { viewModel.onNewPasswordChanged(it) },
        onConfirmPasswordChange = { viewModel.onConfirmPasswordChanged(it) },
        onTogglePasswordVisibility = { viewModel.onTogglePasswordVisibility() },
        onResetPasswordClick = { viewModel.onResetPasswordClick { } },
        onBackToLoginClick = onBackToLoginClick,
        modifier = modifier,
    )
}

@Composable
fun ResetPasswordContent(
    uiState: ResetPasswordUiState,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onResetPasswordClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
            // Top Section with SYNAPSE AI Branding Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                DashedBorderBox(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = 16.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // Brain Logo
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    brush = Brush.linearGradient(listOf(PurpleAccent, BlueAccent, CyanAccent)),
                                    shape = CircleShape
                                )
                                .padding(10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            BrainGraphic(modifier = Modifier.fillMaxSize())
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Reset Password",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Create a new secure password for your Synapse AI account.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            // White Rounded Sheet Container
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
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // New Password Box
                    DashedBorderBox(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = 8.dp
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "New Password",
                                color = Color(0xFF1E293B),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = uiState.newPassword,
                                onValueChange = onNewPasswordChange,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BlueAccent,
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A),
                                ),
                                singleLine = true,
                                trailingIcon = {
                                    IconButton(onClick = onTogglePasswordVisibility) {
                                        Text(
                                            text = if (uiState.isPasswordVisible) "👁" else "🙈",
                                            fontSize = 18.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                            )
                        }
                    }

                    // Confirm Password Box
                    DashedBorderBox(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = 8.dp
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Confirm Password",
                                color = Color(0xFF1E293B),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = uiState.confirmPassword,
                                onValueChange = onConfirmPasswordChange,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BlueAccent,
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A),
                                ),
                                singleLine = true,
                                trailingIcon = {
                                    IconButton(onClick = onTogglePasswordVisibility) {
                                        Text(
                                            text = if (uiState.isPasswordVisible) "👁" else "🙈",
                                            fontSize = 18.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                            )
                        }
                    }

                    // Error Alert
                    if (uiState.errorMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFEE2E2), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = uiState.errorMessage,
                                color = Color(0xFFDC2626),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Success Alert
                    if (uiState.successMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF0FDF4), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = uiState.successMessage,
                                    color = Color(0xFF16A34A),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Click below to Sign In with your new password.",
                                    color = Color(0xFF475569),
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // Reset Password Button Box
                    DashedBorderBox(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = 4.dp
                    ) {
                        Button(
                            onClick = onResetPasswordClick,
                            enabled = !uiState.isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
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
                                                PurpleAccent,
                                                BlueAccent,
                                                CyanAccent,
                                            )
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (uiState.isLoading) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(22.dp),
                                        strokeWidth = 2.5.dp
                                    )
                                } else {
                                    Text(
                                        text = "Reset Password",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }

                    // Sign In Link
                    DashedBorderBox(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = 8.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = "Return to ",
                                color = Color(0xFF64748B),
                                fontSize = 13.sp,
                            )
                            Text(
                                text = "Sign In",
                                color = BlueAccent,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { onBackToLoginClick() },
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
fun ResetPasswordScreenPreview() {
    SYNAPSETheme {
        ResetPasswordContent(
            uiState = ResetPasswordUiState(),
            onNewPasswordChange = {},
            onConfirmPasswordChange = {},
            onTogglePasswordVisibility = {},
            onResetPasswordClick = {},
            onBackToLoginClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
