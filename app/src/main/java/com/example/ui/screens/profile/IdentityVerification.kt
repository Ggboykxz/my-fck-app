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
    var selectedIdType by remember { mutableStateOf("CNI (Carte Nationale d'Identité)") }
    var inputDocNumber by remember { mutableStateOf("") }
    
    // Interactive Scanner States
    var showScanOverlay by remember { mutableStateOf(false) }
    var scanStep by remember { mutableStateOf("voyer") } // "voyer", "scanning", "ocr_reading", "scanned_success"
    var scanLaserOffset by remember { mutableStateOf(0f) }
    var documentPhotoTaken by remember { mutableStateOf(false) }
    
    // Interactive Biometric States
    var showSelfieGuide by remember { mutableStateOf(false) }
    var selfieInstruction by remember { mutableStateOf("Alignez votre visage") }
    var isSelfieTaking by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Scanning visual loop
    LaunchedEffect(showScanOverlay, scanStep) {
        if (showScanOverlay && scanStep == "scanning") {
            var goingDown = true
            while (scanStep == "scanning") {
                delay(16)
                if (goingDown) {
                    scanLaserOffset += 0.02f
                    if (scanLaserOffset >= 1f) goingDown = false
                } else {
                    scanLaserOffset -= 0.02f
                    if (scanLaserOffset <= 0f) goingDown = true
                }
            }
        }
    }

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

        if (currentStatus == "Non vérifié") {
            Text("Configurez vos documents officiels pour gagner en confiance auprès des annonceurs LocAll.", color = Color.White.copy(alpha = 0.65f), fontSize = 13.sp, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(20.dp))

            Text("Type de document", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            var isDocDropdownEx by remember { mutableStateOf(false) }
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isDocDropdownEx = !isDocDropdownEx },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))
                ) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(selectedIdType, color = Color.White, fontSize = 14.sp)
                        Icon(Icons.Rounded.ArrowDropDown, contentDescription = null, tint = Color.White)
                    }
                }
            }

            Text("Numéro du document", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                value = inputDocNumber,
                onValueChange = { inputDocNumber = it },
                placeholder = { Text("Ex: 104278429", color = Color.White.copy(alpha = 0.35f)) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Front Card Box photo Simulation container
            Text("Photo Recto du document", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(vertical = 8.dp)
                    .clickable {
                        showScanOverlay = true
                        scanStep = "scanning"
                        coroutineScope.launch {
                            delay(2500)
                            scanStep = "ocr_reading"
                            delay(2000)
                            scanStep = "scanned_success"
                            documentPhotoTaken = true
                        }
                    },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, if (documentPhotoTaken) PrimaryGreen else Color.White.copy(alpha = 0.10f))
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (documentPhotoTaken) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.CheckCircle, contentDescription = "Succès du scan", tint = PrimaryGreen, modifier = Modifier.size(28.dp))
                            }
                            Text("CNI de NGUEMA Pierre scannée avec succès !", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("ID: " + inputDocNumber.ifBlank { "CNI-84729" } + " | OCR Valide", color = PrimaryGreen, fontSize = 11.sp)
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Rounded.AddAPhoto, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(32.dp))
                            Text("Lancer le Scanner Intelligent LocAll (Vision AI)", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Reconnaissance OCR de la CNI / Passeport", color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { 
                    viewModel.setIdentityVerificationStatus("Documents soumis") 
                },
                enabled = inputDocNumber.isNotBlank() && documentPhotoTaken,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Valider l'étape du document", fontWeight = FontWeight.Bold)
            }
        } else if (currentStatus == "Documents soumis") {
            // STEP 2: Selfie Validation live capture simulated view
            Text("Vérification Faciale Selfie", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Cadrez votre visage à l'intérieur du cercle vert ci-dessous pour confirmer votre identité face à la fraude.", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 20.dp))

            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .border(4.dp, if (isSelfieTaking) PrimaryGreen else Color.White.copy(alpha = 0.3f), CircleShape)
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                // Show face outline / target circle mockup
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(Color.White.copy(alpha = 0.05f), radius = size.minDimension * 0.45f)
                    drawCircle(PrimaryGreen.copy(alpha = 0.08f), radius = size.minDimension * 0.4f)
                }

                if (isSelfieTaking) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(color = PrimaryGreen)
                        Text(selfieInstruction, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp))
                    }
                } else {
                    Icon(
                        Icons.Rounded.Face,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(120.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    isSelfieTaking = true
                    coroutineScope.launch {
                        selfieInstruction = "Alignement du visage..."
                        delay(1500)
                        selfieInstruction = "Veuillez cligner des yeux..."
                        delay(1500)
                        selfieInstruction = "Veuillez sourire doucement..."
                        delay(1500)
                        selfieInstruction = "Liveness 3D en cours..."
                        delay(1500)
                        isSelfieTaking = false
                        viewModel.setIdentityVerificationStatus("En révision")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Lancer la vérification biométrique", fontWeight = FontWeight.Bold)
            }
        } else if (currentStatus == "En révision") {
            // Step 3: Pending review status screen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4A3515)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Shield, contentDescription = "En cours d'examen", tint = Color(0xFFFFB300), modifier = Modifier.size(40.dp))
                    }

                    Text("Dossier en cours d'examen", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Vos documents et votre selfie ont été soumis avec succès. Les modérateurs LocAll Gabon examinent actuellement votre demande. Temps de validation moyen : 12-24 heures.",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f), contentColor = Color.White)
                    ) {
                        Text("Découvrir l'application")
                    }

                    // DEMO ADMIN BYPASS BUTTON
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                            .border(1.dp, Color(0xFFFFB300).copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1508))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "FONCTIONALITÉ DE DÉMO LOCALL",
                                color = Color(0xFFFFB300),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                "En mode démo, vous pouvez forcer la validation automatique instantanée pour débloquer votre badge 'Vérifié'.",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = { viewModel.setIdentityVerificationStatus("Vérifié") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300), contentColor = Color.Black),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Rounded.VerifiedUser, contentDescription = null, tint = Color.Black)
                                    Text("Approuver instantanément", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // VERIFIED GREEN SCREEN
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(Icons.Rounded.Verified, contentDescription = "Vérifié", tint = PrimaryGreen, modifier = Modifier.size(80.dp))
                    Text("Compte Entièrement Vérifié !", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Vous avez validé toutes les étapes d'identité de haut niveau.", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp, textAlign = TextAlign.Center)
                    Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy)) {
                        Text("Retour", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
    }

    // HIGH FIDELITY SCANNER MODAL OVERLAY
    if (showScanOverlay) {
        Dialog(onDismissRequest = { showScanOverlay = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .border(2.dp, PrimaryGreen, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF090E17))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "SCANNER DE DOCUMENT LOCALL AI",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    // Viewfinder Mock
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.5f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        // Scanner layout viewfinder guides
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeW = 4.dp.toPx()
                            val lineL = 20.dp.toPx()
                            // Top Left Corner
                            drawPath(
                                path = Path().apply {
                                    moveTo(strokeW, strokeW + lineL)
                                    lineTo(strokeW, strokeW)
                                    lineTo(strokeW + lineL, strokeW)
                                },
                                color = PrimaryGreen,
                                style = Stroke(width = strokeW)
                            )
                            // Top Right Corner
                            drawPath(
                                path = Path().apply {
                                    moveTo(size.width - strokeW, strokeW + lineL)
                                    lineTo(size.width - strokeW, strokeW)
                                    lineTo(size.width - strokeW - lineL, strokeW)
                                },
                                color = PrimaryGreen,
                                style = Stroke(width = strokeW)
                            )
                            // Bottom Left Corner
                            drawPath(
                                path = Path().apply {
                                    moveTo(strokeW, size.height - strokeW - lineL)
                                    lineTo(strokeW, size.height - strokeW)
                                    lineTo(strokeW + lineL, size.height - strokeW)
                                },
                                color = PrimaryGreen,
                                style = Stroke(width = strokeW)
                            )
                            // Bottom Right Corner
                            drawPath(
                                path = Path().apply {
                                    moveTo(size.width - strokeW, size.height - strokeW - lineL)
                                    lineTo(size.width - strokeW, size.height - strokeW)
                                    lineTo(size.width - strokeW - lineL, size.height - strokeW)
                                },
                                color = PrimaryGreen,
                                style = Stroke(width = strokeW)
                            )

                            // Laser scanning bar
                            if (scanStep == "scanning") {
                                val laserY = size.height * scanLaserOffset
                                drawLine(
                                    color = PrimaryGreen,
                                    start = Offset(0f, laserY),
                                    end = Offset(size.width, laserY),
                                    strokeWidth = 3.dp.toPx()
                                )
                            }
                        }

                        // Status messages on scan screen
                        when (scanStep) {
                            "scanning" -> {
                                Text(
                                    "ANALYSE DE LA PIECE D'IDENTITE...",
                                    color = PrimaryGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            "ocr_reading" -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(color = PrimaryGreen, modifier = Modifier.size(28.dp))
                                    Text(
                                        "RECONNAISSANCE OPTIQUE (OCR)...",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            "scanned_success" -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Rounded.Check, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(36.dp))
                                    Text(
                                        "NOM: NGUEMA PIERRE",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "SCAN OK - INTÉGRITÉ CLÉ VALIDÉE",
                                        color = PrimaryGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Type: " + selectedIdType.split(" ").first(),
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 11.sp
                        )

                        if (scanStep == "scanned_success") {
                            Button(
                                onClick = { showScanOverlay = false },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Terminer le scanner", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        } else {
                            Text(
                                "Veuillez ne pas bouger...",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                    }
                }
            }
        }
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
