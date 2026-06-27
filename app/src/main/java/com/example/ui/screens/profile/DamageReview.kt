package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ReceivedReservation
import com.example.ui.components.*
import com.example.ui.theme.*

// ---------------- DAMAGE REPORTING FORM ----------------

@Composable
fun DamageReportingScreen(
    reservation: ReceivedReservation?,
    onBack: () -> Unit,
    onSubmitted: () -> Unit
) {
    var detailsInput by remember { mutableStateOf("") }
    var compensValue by remember { mutableStateOf("") }
    var photoTaken by remember { mutableStateOf(false) }
    var isSubmittedSuccess by remember { mutableStateOf(false) }

    if (isSubmittedSuccess) {
        Dialog(onDismissRequest = { isSubmittedSuccess = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Rounded.CloudDone, contentDescription = "Problème soumis", tint = PrimaryGreen, modifier = Modifier.size(52.dp))
                    Text("Problème Soumis !", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                    Text("Votre rapport de dommage a été transmis au département de médiation de LocAll Gabon. Nous étudierons les preuves fournies.", fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    Button(onClick = { isSubmittedSuccess = false; onSubmitted() }, colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)) {
                        Text("Entendu", color = Color.White)
                    }
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
            Text("Signaler un Problème", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Référence Réservation: ${reservation?.id ?: "#RES-XXXX"}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
        Text(reservation?.itemTitle ?: "Bien loué", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        Text("Description des anomalies contractées", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            value = detailsInput,
            onValueChange = { detailsInput = it },
            placeholder = { Text("Détaillez précisément les rayures, pannes, ou bris rencontrés...", color = Color.White.copy(alpha = 0.3f)) },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen, unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedTextColor = Color.White, unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text("Demande d'Indemnisation Souhaitée (F CFA)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            value = compensValue,
            onValueChange = { compensValue = it },
            placeholder = { Text("Ex: 50000", color = Color.White.copy(alpha = 0.3f)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen, unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedTextColor = Color.White, unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text("Preuves Photo", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .padding(vertical = 10.dp)
                .clickable { photoTaken = true },
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.10f))
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (photoTaken) {
                    Icon(Icons.Rounded.LinkedCamera, contentDescription = "Caméra", tint = PrimaryGreen, modifier = Modifier.size(36.dp))
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Rounded.AddAPhoto, contentDescription = "Prendre une photo", tint = PrimaryGreen)
                        Text("Prendre une photo du sinistre", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { isSubmittedSuccess = true },
            enabled = detailsInput.isNotBlank() && compensValue.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Soumettre le litige à LocAll", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}

// ---------------- TENANT EVALUATION SCREEN ----------------

@Composable
fun TenantReviewScreen(
    reservation: ReceivedReservation?,
    onBack: () -> Unit,
    onSubmitted: () -> Unit
) {
    var noteStars by remember { mutableStateOf(5) }
    var reviewTextInput by remember { mutableStateOf("") }
    var isSubmittedSuccess by remember { mutableStateOf(false) }

    if (isSubmittedSuccess) {
        Dialog(onDismissRequest = { isSubmittedSuccess = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Rounded.SentimentSatisfiedAlt, contentDescription = "Avis publié", tint = PrimaryGreen, modifier = Modifier.size(52.dp))
                    Text("Avis publié !", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                    Text("Votre recommandation a été enregistrée sur le profil de ${reservation?.tenantName}.", fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    Button(onClick = { isSubmittedSuccess = false; onSubmitted() }, colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)) {
                        Text("Terminer", color = Color.White)
                    }
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
            Text("Évaluer le Locataire", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Tenant description
        Text("Évaluer l'expérience avec ${reservation?.tenantName}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text("Sélectionnez votre note générale", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, modifier = Modifier.padding(bottom = 16.dp))

        // Interactive Stars
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            for (i in 1..5) {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = null,
                    tint = if (i <= noteStars) Color(0xFFFFB300) else Color.White.copy(alpha = 0.2f),
                    modifier = Modifier
                        .size(44.dp)
                        .clickable { noteStars = i }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Laissez un commentaire sur la ponctualité & le respect du bien", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            value = reviewTextInput,
            onValueChange = { reviewTextInput = it },
            placeholder = { Text("Ex: Locataire très ponctuel et arrangeant, bien restitué dans un état impeccable. Je recommande vivement !", color = Color.White.copy(alpha = 0.3f)) },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen, unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedTextColor = Color.White, unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { isSubmittedSuccess = true },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Publier l'évaluation", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}
