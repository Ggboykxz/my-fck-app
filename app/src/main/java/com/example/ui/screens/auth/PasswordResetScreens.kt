package com.example.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import com.example.ui.components.PasswordStrength
import kotlinx.coroutines.delay

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
                contentDescription = "Retour",
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
                contentDescription = "Icône mot de passe oublié",
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
                contentDescription = "Retour",
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
                contentDescription = "Icône de vérification",
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
                contentDescription = "Retour",
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
                contentDescription = "Confirmation nouveau mot de passe",
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
                            contentDescription = "Basculer la visibilité du mot de passe",
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
                            contentDescription = "Basculer la visibilité du mot de passe",
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
                            contentDescription = "Icône de succès",
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
