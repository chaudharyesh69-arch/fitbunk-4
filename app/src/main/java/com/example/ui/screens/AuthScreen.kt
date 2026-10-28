package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FitbunkLogo
import com.example.ui.theme.*

@Composable
fun AuthScreen(
    onAuthSuccess: (name: String, email: String) -> Unit,
    onGuestClick: () -> Unit
) {
    var isSignUpMode by remember { mutableStateOf(true) }
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("alex.rivera@fitbunk.io") }
    var passwordInput by remember { mutableStateOf("Fitbunk#2026") }
    var passwordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var googleNoticeMessage by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FitbunkLightBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("auth_screen_root")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // FITBUNK Official Logo Emblem
            FitbunkLogo(size = 76.dp)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isSignUpMode) "Join the FITBUNK Club" else "Welcome Back",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = FitbunkDeepPurple,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (isSignUpMode)
                    "Create your profile to unlock instant fits and fast drops."
                else
                    "Log in to manage your cart, orders, and saved fits.",
                style = MaterialTheme.typography.bodyMedium,
                color = FitbunkTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
            )

            // Mode Selector: Toggle between Sign Up and Login
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFECE4F7)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSignUpMode) FitbunkDeepPurple else Color.Transparent)
                            .clickable {
                                isSignUpMode = true
                                errorMessage = null
                            }
                            .testTag("auth_toggle_signup"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sign Up",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isSignUpMode) FitbunkWhite else FitbunkDeepPurple
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (!isSignUpMode) FitbunkDeepPurple else Color.Transparent)
                            .clickable {
                                isSignUpMode = false
                                errorMessage = null
                            }
                            .testTag("auth_toggle_login"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Log In",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (!isSignUpMode) FitbunkWhite else FitbunkDeepPurple
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Continue with Google Button
            OutlinedButton(
                onClick = {
                    googleNoticeMessage = "Google Sign-In requires active Google Play Credential Manager on physical device. Please use email or continue as Guest below."
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("auth_google_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = FitbunkWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, FitbunkCardBorder)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "G",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = FitbunkViolet
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Continue with Google",
                        color = FitbunkDeepPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            if (googleNoticeMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = FitbunkSurfaceElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = FitbunkLavender,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = googleNoticeMessage ?: "",
                            color = FitbunkLavenderLight,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // OR divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = FitbunkCardBorder)
                Text(
                    text = "  OR EMAIL  ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = FitbunkTextSecondary
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = FitbunkCardBorder)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Name field (if Sign Up)
            if (isSignUpMode) {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it; errorMessage = null },
                    label = { Text("Your Name") },
                    placeholder = { Text("Alex Rivera") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Outlined.Person, contentDescription = null, tint = FitbunkViolet)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_name_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = FitbunkWhite,
                        unfocusedContainerColor = FitbunkWhite,
                        focusedBorderColor = FitbunkViolet,
                        unfocusedBorderColor = FitbunkCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Email input
            OutlinedTextField(
                value = emailInput,
                onValueChange = { emailInput = it; errorMessage = null },
                label = { Text("Email Address") },
                placeholder = { Text("name@example.com") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(Icons.Outlined.Email, contentDescription = null, tint = FitbunkViolet)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_email_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = FitbunkWhite,
                    unfocusedContainerColor = FitbunkWhite,
                    focusedBorderColor = FitbunkViolet,
                    unfocusedBorderColor = FitbunkCardBorder
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Password input with show/hide
            OutlinedTextField(
                value = passwordInput,
                onValueChange = { passwordInput = it; errorMessage = null },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                leadingIcon = {
                    Icon(Icons.Outlined.Lock, contentDescription = null, tint = FitbunkViolet)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = FitbunkTextSecondary
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_password_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = FitbunkWhite,
                    unfocusedContainerColor = FitbunkWhite,
                    focusedBorderColor = FitbunkViolet,
                    unfocusedBorderColor = FitbunkCardBorder
                )
            )

            // Forgot password link (Login mode)
            if (!isSignUpMode) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "Forgot password?",
                        color = FitbunkViolet,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { showForgotPasswordDialog = true }
                            .testTag("auth_forgot_password")
                    )
                }
            }

            // Error Message
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = FitbunkAccentPink.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = FitbunkAccentPink,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = FitbunkAccentPink,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Action Button
            Button(
                onClick = {
                    focusManager.clearFocus()
                    // Validation
                    if (isSignUpMode && nameInput.trim().isEmpty()) {
                        errorMessage = "Please enter your name"
                        return@Button
                    }
                    if (!emailInput.contains("@") || !emailInput.contains(".")) {
                        errorMessage = "Please enter a valid email address"
                        return@Button
                    }
                    if (passwordInput.length < 6) {
                        errorMessage = "Password must be at least 6 characters"
                        return@Button
                    }

                    val finalName = if (nameInput.isNotBlank()) nameInput else "Alex Rivera"
                    onAuthSuccess(finalName, emailInput)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("auth_submit_button"),
                colors = ButtonDefaults.buttonColors(containerColor = FitbunkDeepPurple),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (isSignUpMode) "CREATE FITBUNK ACCOUNT" else "LOG IN",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp,
                    color = FitbunkWhite
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Guest Browsing Option
            OutlinedButton(
                onClick = onGuestClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("auth_guest_button"),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, FitbunkViolet)
            ) {
                Text(
                    text = "Continue as Guest",
                    color = FitbunkViolet,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Terms and Privacy links
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "By continuing, you agree to FITBUNK's ",
                    fontSize = 12.sp,
                    color = FitbunkTextSecondary
                )
                Text(
                    text = "Terms & Privacy",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = FitbunkViolet,
                    modifier = Modifier.clickable { showTermsDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Forgot password dialog
        if (showForgotPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showForgotPasswordDialog = false },
                title = { Text("Reset Password", fontWeight = FontWeight.Bold) },
                text = {
                    Text("We've sent a 6-digit verification code to $emailInput to reset your password.")
                },
                confirmButton = {
                    Button(
                        onClick = { showForgotPasswordDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = FitbunkViolet)
                    ) {
                        Text("OK")
                    }
                }
            )
        }

        // Terms and Privacy dialog
        if (showTermsDialog) {
            AlertDialog(
                onDismissRequest = { showTermsDialog = false },
                title = { Text("FITBUNK Terms & Privacy", fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "FITBUNK provides hyperfast on-demand fashion drops. We respect your privacy and only use your address for speedy delivery routing. No personal information is sold to third parties."
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { showTermsDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = FitbunkViolet)
                    ) {
                        Text("Understood")
                    }
                }
            )
        }
    }
}
