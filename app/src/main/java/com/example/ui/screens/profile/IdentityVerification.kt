package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.activity.compose.BackHandler
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ---------------- IDENTITY VERIFICATION SCREEN (TENANT) ----------------

@Composable
fun IdentityVerificationScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val currentStatus by viewModel.identityVerificationStatus.collectAsState()
    val verificationPhone by viewModel.verificationPhone.collectAsState()
    val verificationEmail by viewModel.verificationEmail.collectAsState()
    val verificationIdCard by viewModel.verificationIdCard.collectAsState()
    val verificationAddress by viewModel.verificationAddress.collectAsState()
    val trustScore by viewModel.trustScore.collectAsState()

    var otpInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var addressInput by remember { mutableStateOf("") }
    var showOtpDialog by remember { mutableStateOf(false) }
    var showEmailDialog by remember { mutableStateOf(false) }
    var showAddressDialog by remember { mutableStateOf(false) }
    var idCardScanned by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val verificationLevels = listOf(
        Triple("Téléphone vérifié", "Recevez un code OTP par SMS", verificationPhone),
        Triple("Email vérifié", "Confirmez votre adresse email", verificationEmail),
        Triple("Pièce d'identité", "Scannez votre CNI ou passeport", verificationIdCard),
        Triple("Adresse vérifiée", "Justificatif de domicile", verificationAddress)
    )

    val completedCount = listOf(verificationPhone, verificationEmail, verificationIdCard, verificationAddress).count { it }
    val progressFloat = completedCount / 4f

    BackHandler { onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Vérification d'Identité", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Overall progress
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.08f)),
            border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Rounded.Verified, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("${(progressFloat * 100).toInt()}% vérifié", color = PrimaryGreen, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progressFloat },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = PrimaryGreen,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Score de confiance: $trustScore/100", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("ÉTAPES DE VÉRIFICATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))

        // Level 1: Phone
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = if (verificationPhone) PrimaryGreen.copy(alpha = 0.06f) else Color(0xFF162133))
        ) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(if (verificationPhone) PrimaryGreen.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.06f)), contentAlignment = Alignment.Center) {
                    Icon(if (verificationPhone) Icons.Rounded.CheckCircle else Icons.Rounded.Phone, contentDescription = null, tint = if (verificationPhone) PrimaryGreen else Color.White.copy(alpha = 0.3f), modifier = Modifier.size(22.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Téléphone vérifié", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Recevez un code OTP par SMS", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                }
                if (!verificationPhone) {
                    Button(onClick = { showOtpDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy), shape = RoundedCornerShape(8.dp)) {
                        Text("Vérifier", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text("Vérifié", color = PrimaryGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Level 2: Email
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = if (verificationEmail) PrimaryGreen.copy(alpha = 0.06f) else Color(0xFF162133))
        ) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(if (verificationEmail) PrimaryGreen.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.06f)), contentAlignment = Alignment.Center) {
                    Icon(if (verificationEmail) Icons.Rounded.CheckCircle else Icons.Rounded.Email, contentDescription = null, tint = if (verificationEmail) PrimaryGreen else Color.White.copy(alpha = 0.3f), modifier = Modifier.size(22.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Email vérifié", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Confirmez votre adresse email", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                }
                if (!verificationEmail) {
                    Button(onClick = { showEmailDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy), shape = RoundedCornerShape(8.dp)) {
                        Text("Vérifier", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text("Vérifié", color = PrimaryGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Level 3: ID Card
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = if (verificationIdCard) PrimaryGreen.copy(alpha = 0.06f) else Color(0xFF162133))
        ) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(if (verificationIdCard) PrimaryGreen.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.06f)), contentAlignment = Alignment.Center) {
                    Icon(if (verificationIdCard) Icons.Rounded.CheckCircle else Icons.Rounded.CreditCard, contentDescription = null, tint = if (verificationIdCard) PrimaryGreen else Color.White.copy(alpha = 0.3f), modifier = Modifier.size(22.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Pièce d'identité", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Scannez votre CNI ou passeport", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                }
                if (!verificationIdCard) {
                    Button(onClick = {
                        coroutineScope.launch {
                            idCardScanned = true
                            delay(2000)
                            viewModel.onVerificationComplete("id_card")
                            idCardScanned = false
                        }
                    }, enabled = !idCardScanned, colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy), shape = RoundedCornerShape(8.dp)) {
                        if (idCardScanned) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = BrandNavy, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Scan...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Text("Scanner", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Text("Vérifié", color = PrimaryGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Level 4: Address
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = if (verificationAddress) PrimaryGreen.copy(alpha = 0.06f) else Color(0xFF162133))
        ) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(if (verificationAddress) PrimaryGreen.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.06f)), contentAlignment = Alignment.Center) {
                    Icon(if (verificationAddress) Icons.Rounded.CheckCircle else Icons.Rounded.Home, contentDescription = null, tint = if (verificationAddress) PrimaryGreen else Color.White.copy(alpha = 0.3f), modifier = Modifier.size(22.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Adresse vérifiée", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Justificatif de domicile", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                }
                if (!verificationAddress) {
                    Button(onClick = { showAddressDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy), shape = RoundedCornerShape(8.dp)) {
                        Text("Vérifier", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text("Vérifié", color = PrimaryGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (completedCount == 4) {
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.1f)), border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Rounded.Verified, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(28.dp))
                    Column {
                        Text("Profil entièrement vérifié !", color = PrimaryGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Vous bénéficiez d'une confiance maximale", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

    // OTP Dialog
    if (showOtpDialog) {
        AlertDialog(
            onDismissRequest = { showOtpDialog = false },
            containerColor = Color(0xFF162133),
            title = { Text("Vérification Téléphone", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Un code OTP a été envoyé au +241 77 12 34 56", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                    OutlinedTextField(
                        value = otpInput,
                        onValueChange = { otpInput = it },
                        placeholder = { Text("Entrez le code OTP", color = Color.White.copy(alpha = 0.4f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedContainerColor = Color(0xFF0D1B2A),
                            unfocusedContainerColor = Color(0xFF0D1B2A)
                        )
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.onVerificationComplete("phone")
                    showOtpDialog = false
                    otpInput = ""
                }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen), shape = RoundedCornerShape(10.dp)) {
                    Text("Confirmer", fontWeight = FontWeight.Bold, color = BrandNavy)
                }
            },
            dismissButton = { TextButton(onClick = { showOtpDialog = false }) { Text("Annuler", color = Color.White.copy(alpha = 0.6f)) } }
        )
    }

    // Email Dialog
    if (showEmailDialog) {
        AlertDialog(
            onDismissRequest = { showEmailDialog = false },
            containerColor = Color(0xFF162133),
            title = { Text("Vérification Email", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Entrez votre adresse email pour recevoir un lien de confirmation", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        placeholder = { Text("votre@email.com", color = Color.White.copy(alpha = 0.4f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedContainerColor = Color(0xFF0D1B2A),
                            unfocusedContainerColor = Color(0xFF0D1B2A)
                        )
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.onVerificationComplete("email")
                    showEmailDialog = false
                    emailInput = ""
                }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen), shape = RoundedCornerShape(10.dp)) {
                    Text("Envoyer", fontWeight = FontWeight.Bold, color = BrandNavy)
                }
            },
            dismissButton = { TextButton(onClick = { showEmailDialog = false }) { Text("Annuler", color = Color.White.copy(alpha = 0.6f)) } }
        )
    }

    // Address Dialog
    if (showAddressDialog) {
        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            containerColor = Color(0xFF162133),
            title = { Text("Vérification Adresse", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Entrez votre adresse complète pour vérification", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                    OutlinedTextField(
                        value = addressInput,
                        onValueChange = { addressInput = it },
                        placeholder = { Text("Votre adresse complète", color = Color.White.copy(alpha = 0.4f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedContainerColor = Color(0xFF0D1B2A),
                            unfocusedContainerColor = Color(0xFF0D1B2A)
                        )
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.onVerificationComplete("address")
                    showAddressDialog = false
                    addressInput = ""
                }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen), shape = RoundedCornerShape(10.dp)) {
                    Text("Soumettre", fontWeight = FontWeight.Bold, color = BrandNavy)
                }
            },
            dismissButton = { TextButton(onClick = { showAddressDialog = false }) { Text("Annuler", color = Color.White.copy(alpha = 0.6f)) } }
        )
    }
}

// ==================== REAL-TIME VERIFICATION SCREEN ====================
@Composable
fun RealTimeVerificationScreen(
    onBack: () -> Unit
) {
    val verificationSteps = listOf(
        Triple("Identité vérifiée", "CNI scannée et validée par OCR", true),
        Triple("Selfie validé", "Correspondance faciale confirmée", true),
        Triple("Adresse confirmée", "Justificatif de domicile vérifié", false),
        Triple("Téléphone vérifié", "Code SMS reçu et validé", true)
    )

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.08f), CircleShape)) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Vérification Temps Réel", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.08f)), border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Rounded.Verified, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Profil 75% vérifié", color = PrimaryGreen, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { 0.75f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = PrimaryGreen,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("ÉTAPES DE VÉRIFICATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(12.dp))

        verificationSteps.forEach { (title, desc, isVerified) ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = if (isVerified) PrimaryGreen.copy(alpha = 0.06f) else Color(0xFF162133))
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(if (isVerified) PrimaryGreen.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.06f)), contentAlignment = Alignment.Center) {
                        Icon(
                            if (isVerified) Icons.Rounded.CheckCircle else Icons.Rounded.Schedule,
                            contentDescription = null,
                            tint = if (isVerified) PrimaryGreen else Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(desc, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                    }
                    Text(
                        if (isVerified) "Vérifié" else "En attente",
                        color = if (isVerified) PrimaryGreen else Color(0xFFFFB300),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Rounded.Info, contentDescription = "Information", tint = Color(0xFF4FC3F7), modifier = Modifier.size(20.dp))
                Text("La vérification complète débloque le badge Vérifié et augmente votre confiance de 40%.", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
            }
        }
    }
}
