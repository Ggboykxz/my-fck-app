package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel

// ==================== INSURANCE OPTIONS SCREEN ====================
@Composable
fun InsuranceScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val insuranceSubscription by viewModel.insuranceSubscription.collectAsState()
    val insuranceClaims by viewModel.insuranceClaims.collectAsState()
    val walletBalance by viewModel.walletBalance.collectAsState()
    var selectedPlan by remember { mutableStateOf(insuranceSubscription?.planName ?: "basic") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showClaimDialog by remember { mutableStateOf(false) }
    var claimDate by remember { mutableStateOf("") }
    var claimDescription by remember { mutableStateOf("") }
    var claimAmount by remember { mutableStateOf("") }

    val plans = listOf(
        Triple("basic", "Basique", 5000),
        Triple("standard", "Standard", 10000),
        Triple("premium", "Premium", 20000)
    )

    val planCoverages = mapOf(
        "basic" to listOf(
            "Dommages matériels" to true,
            "Vol et tentative de vol" to false,
            "Incendie" to false,
            "Assistance routière 24/7" to true,
            "Responsabilité civile" to false
        ),
        "standard" to listOf(
            "Dommages matériels" to true,
            "Vol et tentative de vol" to true,
            "Incendie" to true,
            "Assistance routière 24/7" to true,
            "Responsabilité civile" to false
        ),
        "premium" to listOf(
            "Dommages matériels" to true,
            "Vol et tentative de vol" to true,
            "Incendie" to true,
            "Assistance routière 24/7" to true,
            "Responsabilité civile" to true
        )
    )

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.08f), CircleShape)) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Assurance Location", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Icon(Icons.Rounded.Shield, contentDescription = "Assurance", tint = PrimaryGreen, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Text("Protégez votre location", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text("Choisissez une couverture adaptée à vos besoins", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp, textAlign = TextAlign.Center)

        if (insuranceSubscription != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.1f)), border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(28.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Couvert", color = PrimaryGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Plan ${insuranceSubscription?.planName ?: ""} actif", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(plans) { (id, name, price) ->
                val isSelected = selectedPlan == id
                val isCurrentPlan = insuranceSubscription?.planName == id
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { selectedPlan = id },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) PrimaryGreen.copy(alpha = 0.08f) else Color(0xFF162133)),
                    border = BorderStroke(2.dp, if (isSelected) PrimaryGreen else Color.White.copy(alpha = 0.08f))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(if (isSelected) PrimaryGreen.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.06f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = if (isSelected) PrimaryGreen else Color.White.copy(alpha = 0.3f))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("${formatPriceCfa(price)}/mois", color = PrimaryGreen, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        if (isCurrentPlan) {
                            StatusBadge(text = "Actif", color = PrimaryGreen)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Couverture ${plans.find { it.first == selectedPlan }?.second ?: ""} :", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        planCoverages[selectedPlan]?.forEach { (item, covered) ->
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(
                                    if (covered) Icons.Rounded.Check else Icons.Rounded.Close,
                                    contentDescription = null,
                                    tint = if (covered) PrimaryGreen else Color.Red.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(item, color = if (covered) Color.White.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.35f), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            if (insuranceSubscription != null) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { showClaimDialog = true },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800), contentColor = Color.White)
                    ) {
                        Icon(Icons.Rounded.Report, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Déclarer un sinistre", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            if (insuranceClaims.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Historique des sinistres", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                items(insuranceClaims) { claim ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.06f)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Rounded.Report, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(claim.description.take(40), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                                Text(claim.incidentDate, color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
                            }
                            StatusBadge(
                                text = when(claim.status) { "pending" -> "En cours"; "approved" -> "Approuvé"; else -> "Refusé" },
                                color = when(claim.status) { "pending" -> Color(0xFFFFB300); "approved" -> PrimaryGreen; else -> Color.Red }
                            )
                        }
                    }
                }
            }
        }

        if (insuranceSubscription == null) {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { showConfirmDialog = true },
                modifier = Modifier.fillMaxWidth().height(52.dp).padding(bottom = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy)
            ) {
                Icon(Icons.Rounded.Shield, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Souscrire à l'assurance", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showConfirmDialog) {
        val plan = plans.find { it.first == selectedPlan }
        ConfirmDialog(
            title = "Confirmer la souscription",
            message = "Souscrire au plan ${plan?.second ?: ""} pour ${formatPriceCfa(plan?.third ?: 0)}/mois ? Le montant sera déduit de votre portefeuille.",
            confirmText = "Souscrire",
            onConfirm = {
                viewModel.subscribeInsurance(selectedPlan)
                showConfirmDialog = false
            },
            onDismiss = { showConfirmDialog = false }
        )
    }

    if (showClaimDialog) {
        AlertDialog(
            onDismissRequest = { showClaimDialog = false },
            containerColor = Color(0xFF162133),
            title = { Text("Déclarer un sinistre", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = claimDate,
                        onValueChange = { claimDate = it },
                        placeholder = { Text("Date de l'incident (JJ/MM/AAAA)", color = Color.White.copy(alpha = 0.4f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PrimaryGreen, unfocusedBorderColor = Color.White.copy(alpha = 0.12f), focusedContainerColor = Color(0xFF0D1B2A), unfocusedContainerColor = Color(0xFF0D1B2A))
                    )
                    OutlinedTextField(
                        value = claimDescription,
                        onValueChange = { claimDescription = it },
                        placeholder = { Text("Description du sinistre", color = Color.White.copy(alpha = 0.4f)) },
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PrimaryGreen, unfocusedBorderColor = Color.White.copy(alpha = 0.12f), focusedContainerColor = Color(0xFF0D1B2A), unfocusedContainerColor = Color(0xFF0D1B2A))
                    )
                    OutlinedTextField(
                        value = claimAmount,
                        onValueChange = { claimAmount = it },
                        placeholder = { Text("Montant réclamé (FCFA)", color = Color.White.copy(alpha = 0.4f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PrimaryGreen, unfocusedBorderColor = Color.White.copy(alpha = 0.12f), focusedContainerColor = Color(0xFF0D1B2A), unfocusedContainerColor = Color(0xFF0D1B2A))
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (claimDate.isNotBlank() && claimDescription.isNotBlank() && claimAmount.isNotBlank()) {
                        viewModel.fileInsuranceClaim(
                            planName = insuranceSubscription?.planName ?: "",
                            incidentDate = claimDate,
                            description = claimDescription,
                            amountClaimed = claimAmount.toIntOrNull() ?: 0
                        )
                        showClaimDialog = false
                        claimDate = ""
                        claimDescription = ""
                        claimAmount = ""
                    }
                }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen), shape = RoundedCornerShape(10.dp)) {
                    Text("Soumettre", fontWeight = FontWeight.Bold, color = BrandNavy)
                }
            },
            dismissButton = { TextButton(onClick = { showClaimDialog = false }) { Text("Annuler", color = Color.White.copy(alpha = 0.6f)) } }
        )
    }
}

// ==================== DIGITAL DEPOSIT SCREEN ====================
@Composable
fun DigitalDepositScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val escrows by viewModel.escrows.collectAsState()
    val walletBalance by viewModel.walletBalance.collectAsState()
    var depositMethod by remember { mutableStateOf("airtel") }
    var showPaid by remember { mutableStateOf(false) }

    val activeEscrows = escrows.filter { it.status == "held" }
    val releasedEscrows = escrows.filter { it.status == "released" || it.status == "refunded" }

    BackHandler { onBack() }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.08f), CircleShape)) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Caution Numérique", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.08f)), border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("50 000 F CFA", color = PrimaryGreen, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Text("Montant de la caution", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Remboursée sous 48h après retour du bien", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (activeEscrows.isNotEmpty()) {
            Text("CAUTIONS ACTIVES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            activeEscrows.forEach { escrow ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                    border = BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("Dépôt #${escrow.id}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Réservation #${escrow.bookingId}", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                            }
                            StatusBadge(text = "Retenu", color = Color(0xFFFFB300))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Montant: ${formatPriceCfa(escrow.amount)}", color = PrimaryGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("En attente", color = Color(0xFFFFB300), fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.releaseEscrow(escrow.id) },
                                modifier = Modifier.weight(1f).height(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy)
                            ) {
                                Text("Libérer le dépôt", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { viewModel.refundEscrow(escrow.id) },
                                modifier = Modifier.weight(1f).height(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350), contentColor = Color.White)
                            ) {
                                Text("Retenir dommages", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (releasedEscrows.isNotEmpty()) {
            Text("CAUTIONS LIBÉRÉES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            releasedEscrows.forEach { escrow ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.06f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.AccountBalanceWallet, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Dépôt #${escrow.id} - Réservation #${escrow.bookingId}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                            Text(formatPriceCfa(escrow.amount), color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                        }
                        StatusBadge(
                            text = if (escrow.status == "released") "Libéré" else "Remboursé",
                            color = if (escrow.status == "released") PrimaryGreen else Color(0xFF4FC3F7)
                        )
                    }
                }
            }
        }

        if (activeEscrows.isEmpty() && releasedEscrows.isEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Text("MODE DE PAIEMENT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(12.dp))

            listOf(
                Triple("airtel", "Airtel Money", Color(0xFFE53935)),
                Triple("moov", "Moov Money", Color(0xFFFFB300)),
                Triple("card", "Carte Bancaire", Color(0xFF4FC3F7))
            ).forEach { (id, name, color) ->
                val isSelected = depositMethod == id
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { depositMethod = id },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) color.copy(alpha = 0.08f) else Color(0xFF162133)),
                    border = BorderStroke(1.dp, if (isSelected) color else Color.White.copy(alpha = 0.08f))
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.AccountBalanceWallet, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                        }
                        Text(name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        if (isSelected) Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                    }
                }
            }
        }

        if (showPaid) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.1f)), border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(28.dp))
                    Column {
                        Text("Caution payée avec succès !", color = PrimaryGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("50 000 F CFA déduits via ${if (depositMethod == "airtel") "Airtel Money" else if (depositMethod == "moov") "Moov Money" else "Carte Bancaire"}", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (activeEscrows.isEmpty() && releasedEscrows.isEmpty()) {
            Button(
                onClick = {
                    viewModel.insertEscrow(com.example.data.model.BookingEscrow(bookingId = 1, amount = 50000, status = "held"))
                    showPaid = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp).padding(bottom = 16.dp)
            ) {
                Text("Payer la caution de 50 000 F CFA", fontWeight = FontWeight.Bold)
            }
        }
    }
}
