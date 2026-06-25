package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel
import kotlinx.coroutines.delay

@Composable
fun AuthNavigator(viewModel: RentalViewModel) {
    val authState by viewModel.authState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1526)) // Sophisticated Dark Brand Navy
    ) {
        // Upper background glow blob
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 100.dp, y = (-80).dp)
                .size(350.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFF13EC5B).copy(alpha = 0.12f), Color.Transparent)))
                .blur(80.dp)
        )

        // Lower background glow blob
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-100).dp, y = 100.dp)
                .size(350.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFF2563EB).copy(alpha = 0.05f), Color.Transparent)))
                .blur(100.dp)
        )

        AnimatedContent(
            targetState = authState,
            transitionSpec = {
                slideInHorizontally { width -> width } + fadeIn() togetherWith
                        slideOutHorizontally { width -> -width } + fadeOut()
            },
            label = "AuthScreensTransition"
        ) { currentScreen ->
            when (currentScreen) {
                "login" -> LoginScreenView(
                    onLoginSuccess = {
                        viewModel.setAuthState("loading_login")
                    },
                    onNavigateToRegister = { viewModel.setAuthState("register") },
                    onNavigateToForgotPassword = { viewModel.setAuthState("forgot_password") }
                )
                "loading_login" -> LoadingLoginView(
                    onLoadingFinished = {
                        viewModel.setAuthState("login_success")
                    }
                )
                "login_success" -> LoginSuccessView(
                    onProceed = {
                        viewModel.setLoggedIn(true)
                    }
                )
                "register" -> RegisterScreenView(
                    onRegisterSuccess = {
                        viewModel.setAuthState("loading_register")
                    },
                    onNavigateToLogin = { viewModel.setAuthState("login") }
                )
                "loading_register" -> LoadingRegisterView(
                    onLoadingFinished = {
                        viewModel.setAuthState("register_success")
                    }
                )
                "register_success" -> RegisterSuccessView(
                    onExplore = {
                        viewModel.setLoggedIn(true)
                    },
                    onCompleteProfile = {
                        viewModel.setAuthState("complete_profile")
                    }
                )
                "complete_profile" -> CompleteProfileView(
                    viewModel = viewModel,
                    onSaveProfileSuccess = {
                        viewModel.setAuthState("profile_success")
                    },
                    onBack = {
                        viewModel.setAuthState("register_success")
                    }
                )
                "profile_success" -> ProfileSuccessView(
                    onProceed = {
                        viewModel.setLoggedIn(true)
                    }
                )
                "forgot_password" -> ForgotPasswordScreenView(
                    onCodeSent = { viewModel.setAuthState("otp") },
                    onBack = { viewModel.setAuthState("login") }
                )
                "otp" -> OtpScreenView(
                    onVerifySuccess = { viewModel.setAuthState("new_password") },
                    onBack = { viewModel.setAuthState("forgot_password") }
                )
                "new_password" -> NewPasswordScreenView(
                    onResetSuccess = { viewModel.setAuthState("password_reset_success") },
                    onBack = { viewModel.setAuthState("otp") }
                )
                "password_reset_success" -> PasswordResetSuccessView(
                    onBackToLogin = { viewModel.setAuthState("login") }
                )
            }
        }
    }
}

@Composable
fun LoginScreenView(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    fun validateLogin(): Boolean {
        emailError = if (emailOrPhone.isBlank()) "Ce champ est requis" else null
        passwordError = if (password.isBlank()) "Ce champ est requis" else null
        return emailError == null && passwordError == null
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Spacer(modifier = Modifier.statusBarsPadding())

            // Upper Logo Design exactly from HTML
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .rotate(3f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "LocAll brand logo",
                    tint = Color(0xFF13EC5B),
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Connexion",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Accédez à votre compte LocAll",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email or Phone field
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "EMAIL OU TÉLÉPHONE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.60f),
                    letterSpacing = 1.sp
                )

                OutlinedTextField(
                    value = emailOrPhone,
                    onValueChange = { emailOrPhone = it; emailError = null },
                    placeholder = { Text("Ex: jean.dupont@email.com", color = Color.White.copy(alpha = 0.4f)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_identity_input"),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF13EC5B),
                        unfocusedBorderColor = if (emailError != null) Color.Red else Color.White.copy(alpha = 0.12f),
                        focusedContainerColor = Color(0xFF162133),
                        unfocusedContainerColor = Color(0xFF162133)
                    )
                )
                if (emailError != null) {
                    Text(
                        text = emailError!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Password field
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "MOT DE PASSE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.60f),
                    letterSpacing = 1.sp
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; passwordError = null },
                    placeholder = { Text("••••••••", color = Color.White.copy(alpha = 0.4f)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle password visibility",
                                tint = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_password_input"),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF13EC5B),
                        unfocusedBorderColor = if (passwordError != null) Color.Red else Color.White.copy(alpha = 0.12f),
                        focusedContainerColor = Color(0xFF162133),
                        unfocusedContainerColor = Color(0xFF162133)
                    )
                )
                if (passwordError != null) {
                    Text(
                        text = passwordError!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Forgot password trigger
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "Mot de passe oublié ?",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier
                            .clickable { onNavigateToForgotPassword() }
                            .testTag("forgot_password_link")
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Se connecter Button
            Button(
                onClick = { if (validateLogin()) onLoginSuccess() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("login_submit_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF13EC5B),
                    contentColor = Color(0xFF0B1526)
                )
            ) {
                Text(
                    text = "Se connecter",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Divider matching HTML
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.1f))
                Text(
                    text = "OU CONTINUER AVEC",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.40f),
                    modifier = Modifier.padding(horizontal = 14.dp),
                    letterSpacing = 1.sp
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Social Login Buttons Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Google
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF162133))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                        .clickable { onLoginSuccess() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email, // Representing Gmail/Google simply with icon
                            contentDescription = "Google sign in",
                            tint = Color(0xFF13EC5B),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Google",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Facebook
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF162133))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                        .clickable { onLoginSuccess() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public, // Representing social web
                            contentDescription = "Facebook sign in",
                            tint = Color(0xFF1877F2),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Facebook",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Sign Up link
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Pas encore de compte ?",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "S'inscrire",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF13EC5B),
                    modifier = Modifier
                        .clickable { onNavigateToRegister() }
                        .testTag("nav_register_link")
                )
            }

            Spacer(modifier = Modifier.statusBarsPadding())
        }
    }
}

@Composable
fun RegisterScreenView(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var termsChecked by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var termsError by remember { mutableStateOf<String?>(null) }
    val passwordStrength = PasswordStrength.evaluate(password)

    fun validateRegister(): Boolean {
        nameError = if (fullName.isBlank()) "Le nom est requis" else null
        emailError = when {
            email.isBlank() -> "L'email est requis"
            !email.contains("@") -> "Email invalide"
            else -> null
        }
        phoneError = if (phone.isBlank()) "Le téléphone est requis" else null
        passwordError = when {
            password.isBlank() -> "Le mot de passe est requis"
            password.length < 6 -> "Minimum 6 caractères"
            else -> null
        }
        termsError = if (!termsChecked) "Vous devez accepter les conditions" else null
        return nameError == null && emailError == null && phoneError == null && passwordError == null && termsError == null
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Spacer(modifier = Modifier.height(40.dp))

            // App Logo
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .rotate(3f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF13EC5B),
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Inscription",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Créez votre compte pour commencer à louer",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(26.dp))

            // Nom complet
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "NOM COMPLET",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.60f),
                    letterSpacing = 1.sp
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it; nameError = null },
                    placeholder = { Text("Ex: Jean Dupont", color = Color.White.copy(alpha = 0.4f)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_fullname_input"),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = if (nameError != null) Color.Red else Color(0xFF13EC5B),
                        unfocusedBorderColor = if (nameError != null) Color.Red else Color.White.copy(alpha = 0.12f),
                        focusedContainerColor = Color(0xFF162133),
                        unfocusedContainerColor = Color(0xFF162133)
                    )
                )
                if (nameError != null) {
                    Text(
                        text = nameError!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Email
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "EMAIL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.60f),
                    letterSpacing = 1.sp
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; emailError = null },
                    placeholder = { Text("Ex: jean.dupont@email.com", color = Color.White.copy(alpha = 0.4f)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_email_input"),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = if (emailError != null) Color.Red else Color(0xFF13EC5B),
                        unfocusedBorderColor = if (emailError != null) Color.Red else Color.White.copy(alpha = 0.12f),
                        focusedContainerColor = Color(0xFF162133),
                        unfocusedContainerColor = Color(0xFF162133)
                    )
                )
                if (emailError != null) {
                    Text(
                        text = emailError!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Phone
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "NUMÉRO DE TÉLÉPHONE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.60f),
                    letterSpacing = 1.sp
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it; phoneError = null },
                    placeholder = { Text("Ex: +241 07 00 00 00", color = Color.White.copy(alpha = 0.4f)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_phone_input"),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = if (phoneError != null) Color.Red else Color(0xFF13EC5B),
                        unfocusedBorderColor = if (phoneError != null) Color.Red else Color.White.copy(alpha = 0.12f),
                        focusedContainerColor = Color(0xFF162133),
                        unfocusedContainerColor = Color(0xFF162133)
                    )
                )
                if (phoneError != null) {
                    Text(
                        text = phoneError!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "MOT DE PASSE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.60f),
                    letterSpacing = 1.sp
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; passwordError = null },
                    placeholder = { Text("••••••••", color = Color.White.copy(alpha = 0.4f)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle password visibility",
                                tint = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_password_input"),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = if (passwordError != null) Color.Red else Color(0xFF13EC5B),
                        unfocusedBorderColor = if (passwordError != null) Color.Red else Color.White.copy(alpha = 0.12f),
                        focusedContainerColor = Color(0xFF162133),
                        unfocusedContainerColor = Color(0xFF162133)
                    )
                )
                if (passwordError != null) {
                    Text(
                        text = passwordError!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                if (password.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(
                                        when (passwordStrength) {
                                            PasswordStrength.WEAK -> 0.33f
                                            PasswordStrength.MEDIUM -> 0.66f
                                            PasswordStrength.STRONG -> 1f
                                        }
                                    )
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(passwordStrength.color)
                            )
                        }
                        Text(
                            text = passwordStrength.label,
                            color = passwordStrength.color,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Terms and conditions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Checkbox(
                    checked = termsChecked,
                    onCheckedChange = { termsChecked = it; termsError = null },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF13EC5B),
                        uncheckedColor = Color.White.copy(alpha = 0.4f),
                        checkmarkColor = Color(0xFF0B1526)
                    ),
                    modifier = Modifier.testTag("terms_checkbox")
                )
                Text(
                    text = "J'accepte les conditions générales",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier
                        .clickable { termsChecked = !termsChecked }
                        .padding(start = 4.dp)
                )
            }
            if (termsError != null) {
                Text(
                    text = termsError!!,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Registration Submit Button
            Button(
                onClick = { if (validateRegister()) onRegisterSuccess() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("register_submit_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF13EC5B),
                    contentColor = Color(0xFF0B1526)
                )
            ) {
                Text(
                    text = "S'inscrire",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Google / Facebook direct social login simple grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.1f))
                Text(
                    text = "OU S'INSCRIRE AVEC",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.40f),
                    modifier = Modifier.padding(horizontal = 14.dp),
                    letterSpacing = 1.sp
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.1f))
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Google
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF162133))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                        .clickable { onRegisterSuccess() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Google signup",
                            tint = Color(0xFF13EC5B),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Google",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Facebook
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF162133))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                        .clickable { onRegisterSuccess() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Facebook signup",
                            tint = Color(0xFF1877F2),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Facebook",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Navigation to login Screen
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Déjà un compte ?",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Se connecter",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF13EC5B),
                    modifier = Modifier
                        .clickable { onNavigateToLogin() }
                        .testTag("nav_login_link")
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ForgotPasswordScreenView(
    onCodeSent: () -> Unit,
    onBack: () -> Unit
) {
    var emailOrPhone by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .imePadding()
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Back action button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF162133))
                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                .testTag("forgot_back_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back back",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.weight(0.1f))

        // Card illustration logo
        Box(
            modifier = Modifier
                .size(80.dp)
                .rotate(3f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LockReset,
                contentDescription = "Forgot brand icon",
                tint = Color(0xFF13EC5B),
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Mot de passe oublié",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Entrez votre email ou numéro de téléphone pour recevoir un code de réinitialisation.",
            fontSize = 15.sp,
            color = Color.White.copy(alpha = 0.65f),
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Inputs
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "EMAIL OU NUMÉRO DE TÉLÉPHONE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.60f),
                letterSpacing = 1.sp
            )

            OutlinedTextField(
                value = emailOrPhone,
                onValueChange = { emailOrPhone = it },
                placeholder = { Text("Ex: jean.dupont@email.com", color = Color.White.copy(alpha = 0.4f)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.5f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("forgot_identity_input"),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF13EC5B),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                    focusedContainerColor = Color(0xFF162133),
                    unfocusedContainerColor = Color(0xFF162133)
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Action submit button
        Button(
            onClick = onCodeSent,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("forgot_submit_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF13EC5B),
                contentColor = Color(0xFF0B1526)
            )
        ) {
            Text(
                text = "Envoyer le code",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.weight(0.3f))
    }
}

@Composable
fun OtpScreenView(
    onVerifySuccess: () -> Unit,
    onBack: () -> Unit
) {
    // 5 separate OTP fields
    val otpValues = remember { mutableStateListOf("", "", "", "", "") }
    val focusRequesters = remember { List(5) { FocusRequester() } }
    val focusManager = LocalFocusManager.current
    var timerSeconds by remember { mutableStateOf(119) } // 01:59

    LaunchedEffect(Unit) {
        while (timerSeconds > 0) {
            delay(1000)
            timerSeconds--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .imePadding()
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Back button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF162133))
                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                .testTag("otp_back_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.weight(0.1f))

        // Icon Header matching HTML
        Box(
            modifier = Modifier
                .size(80.dp)
                .rotate(3f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.VerifiedUser,
                contentDescription = "Verified confirmation icon",
                tint = Color(0xFF13EC5B),
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Vérifier votre compte",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Un code de vérification a été envoyé à votre email/téléphone. Saisissez-le ci-dessous.",
            fontSize = 15.sp,
            color = Color.White.copy(alpha = 0.65f),
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Grid representation for OTP
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until 5) {
                OutlinedTextField(
                    value = otpValues[i],
                    onValueChange = { input ->
                        if (input.length <= 1) {
                            otpValues[i] = input
                            if (input.isNotEmpty() && i < 4) {
                                focusRequesters[i + 1].requestFocus()
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .focusRequester(focusRequesters[i])
                        .testTag("otp_input_$i"),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF13EC5B),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                        focusedContainerColor = Color(0xFF162133),
                        unfocusedContainerColor = Color(0xFF162133)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Timer and Resend option
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (timerSeconds > 0) {
                val min = timerSeconds / 60
                val sec = timerSeconds % 60
                val timerStr = String.format("%02d:%02d", min, sec)
                Text(
                    text = "Renvoyer le code dans $timerStr",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            } else {
                Text(
                    text = "Vous n'avez pas reçu le code ?",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Text(
                    text = "Renvoyer le code",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF13EC5B),
                    modifier = Modifier.clickable {
                        timerSeconds = 119
                        // Clear OTP fields
                        for (i in 0 until 5) otpValues[i] = ""
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Submit action Button
        Button(
            onClick = onVerifySuccess,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("otp_submit_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF13EC5B),
                contentColor = Color(0xFF0B1526)
            )
        ) {
            Text(
                text = "Vérifier",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.weight(0.1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "SECURED BY LOCALL",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.4f),
                letterSpacing = 1.5.sp
            )
        }
    }
}

@Composable
fun NewPasswordScreenView(
    onResetSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isVisibleNew by remember { mutableStateOf(false) }
    var isVisibleConfirm by remember { mutableStateOf(false) }
    var newPasswordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    val passwordStrength = PasswordStrength.evaluate(newPassword)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .imePadding()
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Back button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF162133))
                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                .testTag("reset_password_back_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.weight(0.1f))

        // Core icon exactly from HTML template
        Box(
            modifier = Modifier
                .size(80.dp)
                .rotate(3f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LockReset, // Re-use lock reset icon
                contentDescription = "New password confirmation",
                tint = Color(0xFF13EC5B),
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Nouveau mot de passe",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Votre code a été vérifié. Veuillez choisir un mot de passe sécurisé.",
            fontSize = 15.sp,
            color = Color.White.copy(alpha = 0.65f),
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Input 1
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "NOUVEAU MOT DE PASSE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.60f),
                letterSpacing = 1.sp
            )

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it; newPasswordError = null },
                placeholder = { Text("••••••••", color = Color.White.copy(alpha = 0.4f)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.5f)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { isVisibleNew = !isVisibleNew }) {
                        Icon(
                            imageVector = if (isVisibleNew) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle password visibility",
                            tint = Color.White.copy(alpha = 0.5f)
                        )
                    }
                },
                visualTransformation = if (isVisibleNew) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("new_password_input_field"),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = if (newPasswordError != null) Color.Red else Color(0xFF13EC5B),
                    unfocusedBorderColor = if (newPasswordError != null) Color.Red else Color.White.copy(alpha = 0.12f),
                    focusedContainerColor = Color(0xFF162133),
                    unfocusedContainerColor = Color(0xFF162133)
                )
            )
            if (newPasswordError != null) {
                Text(
                    text = newPasswordError!!,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            if (newPassword.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(
                                    when (passwordStrength) {
                                        PasswordStrength.WEAK -> 0.33f
                                        PasswordStrength.MEDIUM -> 0.66f
                                        PasswordStrength.STRONG -> 1f
                                    }
                                )
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(2.dp))
                                .background(passwordStrength.color)
                        )
                    }
                    Text(
                        text = passwordStrength.label,
                        color = passwordStrength.color,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Input 2
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "CONFIRMER LE MOT DE PASSE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.60f),
                letterSpacing = 1.sp
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; confirmPasswordError = null },
                placeholder = { Text("••••••••", color = Color.White.copy(alpha = 0.4f)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.5f)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { isVisibleConfirm = !isVisibleConfirm }) {
                        Icon(
                            imageVector = if (isVisibleConfirm) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle password visibility",
                            tint = Color.White.copy(alpha = 0.5f)
                        )
                    }
                },
                visualTransformation = if (isVisibleConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("confirm_password_input_field"),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = if (confirmPasswordError != null) Color.Red else Color(0xFF13EC5B),
                    unfocusedBorderColor = if (confirmPasswordError != null) Color.Red else Color.White.copy(alpha = 0.12f),
                    focusedContainerColor = Color(0xFF162133),
                    unfocusedContainerColor = Color(0xFF162133)
                )
            )
            if (confirmPasswordError != null) {
                Text(
                    text = confirmPasswordError!!,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Password requirement visualizers
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Feature 1
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (newPassword.length >= 8) Color(0xFF13EC5B) else Color.White.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = if (newPassword.length >= 8) Color(0xFF0B1526) else Color.White.copy(alpha = 0.40f),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Text(
                        text = "Au moins 8 caractères",
                        color = if (newPassword.length >= 8) Color.White else Color.White.copy(alpha = 0.50f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Feature 2
                val hasUppercase = newPassword.any { it.isUpperCase() }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (hasUppercase) Color(0xFF13EC5B) else Color.White.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = if (hasUppercase) Color(0xFF0B1526) else Color.White.copy(alpha = 0.40f),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Text(
                        text = "Une majuscule",
                        color = if (hasUppercase) Color.White else Color.White.copy(alpha = 0.50f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Feature 3
                val hasDigit = newPassword.any { it.isDigit() }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (hasDigit) Color(0xFF13EC5B) else Color.White.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = if (hasDigit) Color(0xFF0B1526) else Color.White.copy(alpha = 0.40f),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Text(
                        text = "Un chiffre",
                        color = if (hasDigit) Color.White else Color.White.copy(alpha = 0.50f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Trigger Button
        Button(
            onClick = {
                newPasswordError = when {
                    newPassword.isBlank() -> "Le mot de passe est requis"
                    newPassword.length < 6 -> "Minimum 6 caractères"
                    else -> null
                }
                confirmPasswordError = when {
                    confirmPassword.isBlank() -> "Confirmez le mot de passe"
                    confirmPassword != newPassword -> "Les mots de passe ne correspondent pas"
                    else -> null
                }
                if (newPasswordError == null && confirmPasswordError == null) {
                    onResetSuccess()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("reset_password_submit_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF13EC5B),
                contentColor = Color(0xFF0B1526)
            )
        ) {
            Text(
                text = "Réinitialiser le mot de passe",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.weight(0.15f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "SECURED BY LOCALL",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.4f),
                letterSpacing = 1.5.sp
            )
        }
    }
}

// ==========================================
// NEW INTERMEDIATE & SUCCESS ONBOARDING VIEWS
// ==========================================

@Composable
fun LoadingLoginView(onLoadingFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1500)
        onLoadingFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Elegant Spinner
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    strokeWidth = 5.dp,
                    color = Color(0xFF13EC5B),
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Connexion en cours...",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Nous préparons votre tableau de bord",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LoginSuccessView(onProceed: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .padding(top = 80.dp, bottom = 48.dp)
        ) {
            // Space holder to balance the column layout
            Spacer(modifier = Modifier.height(20.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Success glowing check badge
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF13EC5B).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF13EC5B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success checkmark icon",
                            tint = Color(0xFF0F1724),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Connexion réussie",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Bienvenue sur LocAll, Kofi ! Content de vous revoir.",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            // Bottom action button
            Button(
                onClick = onProceed,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF13EC5B),
                    contentColor = Color(0xFF0F1724)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("login_success_action_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Accéder à l'accueil",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingRegisterView(onLoadingFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1500)
        onLoadingFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    strokeWidth = 5.dp,
                    color = Color(0xFF13EC5B),
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Création de votre compte en cours...",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Préparation de votre espace personnalisé",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun RegisterSuccessView(
    onExplore: () -> Unit,
    onCompleteProfile: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .padding(top = 80.dp, bottom = 48.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF13EC5B).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF13EC5B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success checkmark icon",
                            tint = Color(0xFF0F1724),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Compte créé avec succès !",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Bienvenue sur LocAll. Vous pouvez désormais explorer les annonces ou commencer à louer vos biens.",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onExplore,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF0F1724)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("register_success_explore_button")
                ) {
                    Text(
                        text = "Commencer l'exploration",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = onCompleteProfile,
                    modifier = Modifier.testTag("register_success_complete_profile_button")
                ) {
                    Text(
                        text = "Compléter mon profil",
                        color = Color(0xFF13EC5B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CompleteProfileView(
    viewModel: RentalViewModel,
    onSaveProfileSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var profession by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }

    val isPhoneVerified by viewModel.isPhoneVerified.collectAsState()
    val isSocialLinked by viewModel.isSocialLinked.collectAsState()

    var showGenderMenu by remember { mutableStateOf(false) }
    var showCityMenu by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))

            // Upper Header matching standard flow
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Compléter mon profil",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1.3f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Upload photo avatar simulation
            Box(
                modifier = Modifier.size(136.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF162133))
                        .border(4.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar picture placeholder",
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF13EC5B))
                        .border(3.dp, Color(0xFF0B1526), CircleShape)
                        .clickable { /* Simulate selection */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "photo icon",
                        tint = Color(0xFF0F1724),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Photo de profil",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Date of birth Custom Layout matching styling of template
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "DATE DE NAISSANCE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.60f),
                    letterSpacing = 1.sp
                )

                OutlinedTextField(
                    value = dob,
                    onValueChange = { dob = it },
                    placeholder = { Text("JJ/MM/AAAA", color = Color.White.copy(alpha = 0.4f)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_dob_input"),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF13EC5B),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                        focusedContainerColor = Color(0xFF162133),
                        unfocusedContainerColor = Color(0xFF162133)
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Gender selector card custom modal simulation
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "SEXE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.60f),
                    letterSpacing = 1.sp
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Sélectionner votre sexe", color = Color.White.copy(alpha = 0.4f)) },
                        trailingIcon = {
                            IconButton(onClick = { showGenderMenu = !showGenderMenu }) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Dropdown sex open",
                                    tint = Color.White.copy(alpha = 0.5f)
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showGenderMenu = !showGenderMenu }
                            .testTag("profile_gender_selector"),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF13EC5B),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedContainerColor = Color(0xFF162133),
                            unfocusedContainerColor = Color(0xFF162133)
                        )
                    )

                    DropdownMenu(
                        expanded = showGenderMenu,
                        onDismissRequest = { showGenderMenu = false },
                        modifier = Modifier.background(Color(0xFF162133))
                    ) {
                        DropdownMenuItem(
                            text = { Text("Homme", color = Color.White) },
                            onClick = { gender = "Homme"; showGenderMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Femme", color = Color.White) },
                            onClick = { gender = "Femme"; showGenderMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Autre", color = Color.White) },
                            onClick = { gender = "Autre"; showGenderMenu = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Profession Input Field
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "PROFESSION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.60f),
                    letterSpacing = 1.sp
                )

                OutlinedTextField(
                    value = profession,
                    onValueChange = { profession = it },
                    placeholder = { Text("Ex: Étudiant, Ingénieur...", color = Color.White.copy(alpha = 0.4f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_profession_input"),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF13EC5B),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                        focusedContainerColor = Color(0xFF162133),
                        unfocusedContainerColor = Color(0xFF162133)
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // City dropdown selection
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "VILLE DE RÉSIDENCE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.60f),
                    letterSpacing = 1.sp
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Sélectionner votre ville", color = Color.White.copy(alpha = 0.4f)) },
                        trailingIcon = {
                            IconButton(onClick = { showCityMenu = !showCityMenu }) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Dropdown city open",
                                    tint = Color.White.copy(alpha = 0.5f)
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCityMenu = !showCityMenu }
                            .testTag("profile_city_selector"),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF13EC5B),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedContainerColor = Color(0xFF162133),
                            unfocusedContainerColor = Color(0xFF162133)
                        )
                    )

                    DropdownMenu(
                        expanded = showCityMenu,
                        onDismissRequest = { showCityMenu = false },
                        modifier = Modifier.background(Color(0xFF162133))
                    ) {
                        listOf("Libreville", "Port-Gentil", "Franceville", "Oyem", "Mouila", "Akanda").forEach { cityName ->
                            DropdownMenuItem(
                                text = { Text(cityName, color = Color.White) },
                                onClick = { city = cityName; showCityMenu = false }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Badge verified panel card design exactly matching HTML
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF13EC5B).copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = "Verified trust label icon",
                                tint = Color(0xFF13EC5B),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "Badge Vérifié",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Obtenez le badge de confiance en vérifiant votre identité ou en liant vos réseaux sociaux.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buttons
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Action 1: Verify number
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F1724))
                                .clickable { viewModel.setPhoneVerified(!isPhoneVerified) }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Smartphone,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isPhoneVerified) "Numéro vérifié !" else "Vérifier le numéro",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isPhoneVerified) Color(0xFF13EC5B) else Color.White
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = if (isPhoneVerified) Icons.Default.Check else Icons.Default.KeyboardArrowRight,
                                contentDescription = null,
                                tint = if (isPhoneVerified) Color(0xFF13EC5B) else Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Action 2: Link social accounts
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F1724))
                                .clickable { viewModel.setSocialLinked(!isSocialLinked) }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isSocialLinked) "Compte social lié !" else "Lier un compte social",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSocialLinked) Color(0xFF13EC5B) else Color.White
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = if (isSocialLinked) Icons.Default.Check else Icons.Default.KeyboardArrowRight,
                                tint = if (isSocialLinked) Color(0xFF13EC5B) else Color.White.copy(alpha = 0.3f),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Submit Button
            Button(
                onClick = {
                    viewModel.updateProfile(dob, gender, profession, city)
                    onSaveProfileSuccess()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF13EC5B),
                    contentColor = Color(0xFF0F1724)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("profile_save_submit_button")
            ) {
                Text(
                    text = "Enregistrer le profil",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun ProfileSuccessView(onProceed: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .padding(top = 80.dp, bottom = 48.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF13EC5B).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF13EC5B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success checkmark icon",
                            tint = Color(0xFF0F1724),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Profil mis à jour !",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Vos informations ont été enregistrées avec succès. Vous êtes maintenant prêt à explorer et louer sur LocAll.",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Button(
                onClick = onProceed,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF13EC5B),
                    contentColor = Color(0xFF0F1724)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("profile_success_action_button")
            ) {
                Text(
                    text = "Accéder à l'accueil",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PasswordResetSuccessView(onBackToLogin: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .padding(top = 80.dp, bottom = 48.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF13EC5B).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF13EC5B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success checkmark icon",
                            tint = Color(0xFF0F1724),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Mot de passe réinitialisé",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Votre mot de passe a été modifié avec succès. Vous pouvez maintenant vous connecter à votre compte.",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Button(
                onClick = onBackToLogin,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF13EC5B),
                    contentColor = Color(0xFF0F1724)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("password_reset_success_action_button")
            ) {
                Text(
                    text = "Retour à la connexion",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
