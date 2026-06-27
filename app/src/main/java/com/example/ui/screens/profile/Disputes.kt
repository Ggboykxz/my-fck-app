package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DisputesHistoryScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit,
    onSelectDispute: (String) -> Unit
) {
    var disputeFormEx by remember { mutableStateOf(false) }
    var inputDesc by remember { mutableStateOf("") }
    var trackingCodeSubmit by remember { mutableStateOf<String?>(null) }

    val disputes by viewModel.disputes.collectAsState()

    if (disputeFormEx) {
        Dialog(onDismissRequest = { disputeFormEx = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Ouvrir un Litige", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandNavy)

                    Text("Raison du litige", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                    var selectedReason by remember { mutableStateOf("Bien endommagé") }
                    val reasons = listOf("Bien endommagé", "Clés non remises", "Nettoyage insuffisant", "Autre")
                    reasons.forEach { rs ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { selectedReason = rs }) {
                            RadioButton(selected = selectedReason == rs, onClick = { selectedReason = rs })
                            Text(rs, color = Color.DarkGray)
                        }
                    }

                    Text("Description détaillée", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                    OutlinedTextField(
                        value = inputDesc,
                        onValueChange = { inputDesc = it },
                        placeholder = { Text("Décrivez précisément le litige...") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        colors = OutlinedTextFieldDefaults.colors()
                    )

                    Button(
                        onClick = {
                            disputeFormEx = false
                            trackingCodeSubmit = "#LIT-" + (1000..9999).random()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Soumettre le dossier", color = Color.White)
                    }
                }
            }
        }
    }

    if (trackingCodeSubmit != null) {
        Dialog(onDismissRequest = { trackingCodeSubmit = null }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(Icons.Rounded.CheckCircle, contentDescription = "Succès", tint = PrimaryGreen, modifier = Modifier.size(52.dp))
                    Text("Litige Soumis !", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                    Text("Votre dossier a été enregistré sous le numéro de suivi ${trackingCodeSubmit}. Un médiateur LocAll va se mettre en relation avec vous sous 4 heures.", fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    Button(onClick = { trackingCodeSubmit = null }, colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)) {
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
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }

            Text("Mes Litiges", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            IconButton(
                onClick = { disputeFormEx = true },
                modifier = Modifier
                    .size(40.dp)
                    .background(PrimaryGreen.copy(alpha = 0.15f), CircleShape)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Créer litige", tint = PrimaryGreen)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(disputes, key = { it.id }) { disp ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectDispute(disp.id) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(disp.id, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Surface(
                                color = if (disp.status == "En cours") Color(0xFF4A3515) else Color(0xFF0C2417),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    disp.status,
                                    color = if (disp.status == "En cours") Color(0xFFFFB300) else PrimaryGreen,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

                        Text(disp.type, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(disp.description, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Indemnité réclamée", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                            Text(formatPriceCfa(disp.claimAmount), color = PrimaryGreen, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }
}

// ---------------- MEDIATION RECIPIENTS TIMELINES ----------------

@Composable
fun MediationDetailsScreen(
    viewModel: RentalViewModel,
    disputeId: String,
    onBack: () -> Unit
) {
    val messages by viewModel.mediationMessages.collectAsState()
    var chatHistory by remember { mutableStateOf(messages.map { Pair(it.sender, it.message) }) }
    var replyText by remember { mutableStateOf("") }
    var isMediatorWriting by remember { mutableStateOf(false) }

    // Amicable Settlement States
    var selectedAgreementValue by remember { mutableStateOf(25000f) }
    var isSettledSuccess by remember { mutableStateOf(false) }
    var isSettlementProposed by remember { mutableStateOf(false) }
    var timelineStep3Completed by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    fun sendReply(text: String) {
        if (text.isBlank()) return
        chatHistory = chatHistory + Pair("Moi", text)
        replyText = ""
        isMediatorWriting = true

        coroutineScope.launch {
            delay(1500)
            isMediatorWriting = false
            
            val responseText = when {
                text.contains("facture", ignoreCase = true) || text.contains("prix", ignoreCase = true) -> {
                    "Merci pour l'envoi de la facture de chez SOGAFRIC. L'expert évalue la dépréciation restante à 35 000 F CFA. Êtes-vous disposé à proposer un règlement à l'amiable via le widget ci-dessous ?"
                }
                text.contains("accord", ignoreCase = true) || text.contains("amiable", ignoreCase = true) || text.contains("d'accord", ignoreCase = true) -> {
                    "Parfait. Veuillez ajuster le curseur de proposition de règlement à l'amiable ci-dessous sur le montant qui vous convient pour clore la médiation."
                }
                else -> {
                    "J'ai pris note de vos remarques. Nous attendons le retour de la partie adverse. En attendant, proposer un règlement amiable reste le moyen le plus rapide de débloquer la caution en séquestre."
                }
            }
            chatHistory = chatHistory + Pair("Médiateur", responseText)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
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
            Text("Dossier $disputeId", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("État d'avancement de la Médiation", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                
                TimelineStep(title = "Dossier enregistré", desc = "06 Fév - Preuves reçues", isCompleted = true)
                TimelineStep(title = "Analyse de la l'assurance", desc = "Conformité du contrat de bail validée", isCompleted = true)
                TimelineStep(
                    title = if (isSettledSuccess) "Médiation résolue à l'amiable !" else "Décision de l'expert en cours",
                    desc = if (isSettledSuccess) "Accord mutuel enregistré par LocAll" else "En attente du rapport technique GabAsur ou accord",
                    isCompleted = timelineStep3Completed
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Simulated chat box with LocAll Support Team
        Text("Conversation avec le Médiateur", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    val listState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(listState)
                    ) {
                        chatHistory.forEach { msg ->
                            DisputeBubble(sender = msg.first, text = msg.second)
                        }
                        
                        if (isMediatorWriting) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                CircularProgressIndicator(color = PrimaryGreen, modifier = Modifier.size(12.dp), strokeWidth = 2.dp)
                                Text("L'arbitre analyse...", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                            }
                        }
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        placeholder = { Text("Écrire au médiateur LocAll...", color = Color.White.copy(alpha = 0.3f), fontSize = 12.sp) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
                        ),
                        singleLine = true
                    )
                    IconButton(
                        onClick = { sendReply(replyText) },
                        enabled = replyText.isNotBlank(),
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (replyText.isNotBlank()) PrimaryGreen else Color.White.copy(alpha = 0.05f),
                                RoundedCornerShape(10.dp)
                            )
                    ) {
                        Icon(
                            Icons.Rounded.Send,
                            contentDescription = "Envoyer",
                            tint = if (replyText.isNotBlank()) BrandNavy else Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // INTERACTIVE AMICABLE SETTLEMENT PANEL
        Text("Règlement Amiable d'Arbitrage (Optionnel)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A2A)),
            border = BorderStroke(1.dp, if (isSettledSuccess) PrimaryGreen else Color.White.copy(alpha = 0.08f))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isSettledSuccess) {
                    Icon(Icons.Rounded.Handshake, contentDescription = "Accord conclu", tint = PrimaryGreen, modifier = Modifier.size(48.dp))
                    Text(
                        "ACCORD GAGNANT-GAGNANT CONCLU !",
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        "Vous avez convenu d'un dédommagement mutuel de " + formatPriceCfa(selectedAgreementValue.toInt()) + " F CFA. Le reliquat de caution sera débloqué et transféré immédiatement sur vos comptes Airtel Money.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                } else {
                    Text(
                        "En proposant un accord, vous fixez une somme compensatoire à l'amiable. Si la partie accepte, la caution est reversée au prorata instantanément.",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        "Votre proposition : " + formatPriceCfa(selectedAgreementValue.toInt()),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Slider(
                        value = selectedAgreementValue,
                        onValueChange = { if (!isSettlementProposed) selectedAgreementValue = it },
                        valueRange = 5000f..50000f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = PrimaryGreen,
                            activeTrackColor = PrimaryGreen,
                            inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            isSettlementProposed = true
                            chatHistory = chatHistory + Pair("Moi", "Je propose de régler le litige à l'amiable pour un montant forfaitaire de " + formatPriceCfa(selectedAgreementValue.toInt()) + ".")
                            coroutineScope.launch {
                                delay(2000)
                                chatHistory = chatHistory + Pair("Médiateur", "La proposition de " + formatPriceCfa(selectedAgreementValue.toInt()) + " a été acceptée par la partie adverse ! Clôture de l'incident.")
                                delay(1000)
                                isSettledSuccess = true
                                timelineStep3Completed = true
                            }
                        },
                        enabled = !isSettlementProposed,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSettlementProposed) Color.Gray else PrimaryGreen,
                            contentColor = BrandNavy
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (isSettlementProposed) "Attente d'acceptation adverse..." else "Soumettre l'offre amiable",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DisputeBubble(sender: String, text: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()) {
        Text(sender, fontSize = 10.sp, color = PrimaryGreen, fontWeight = FontWeight.Bold)
        Surface(color = Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(8.dp)) {
            Text(text, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun TimelineStep(title: String, desc: String, isCompleted: Boolean) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(if (isCompleted) PrimaryGreen else Color.Gray)
        )
        Column {
            Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(desc, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

// ==================== DISPUTE SCREEN ====================
@Composable
fun DisputeScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    var disputeType by remember { mutableStateOf("Dommage") }
    var description by remember { mutableStateOf("") }
    var showSent by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.08f), CircleShape)) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Signaler un Litige", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Icon(Icons.Rounded.Gavel, contentDescription = "Litige", tint = Color(0xFFFFB300), modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Text("Décrivez votre problème", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text("Un médiateur LocAll examinera votre dossier sous 24-48h.", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(24.dp))

        Text("TYPE DE LITIGE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Dommage", "Retard", "Annulation", "Litige financier").forEach { type ->
                val isSelected = disputeType == type
                Surface(
                    onClick = { disputeType = type },
                    color = if (isSelected) PrimaryGreen.copy(alpha = 0.15f) else Color(0xFF162133),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (isSelected) PrimaryGreen else Color.White.copy(alpha = 0.08f))
                ) {
                    Text(type, color = if (isSelected) PrimaryGreen else Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            placeholder = { Text("Décrivez le problème en détail...", color = Color.White.copy(alpha = 0.3f)) },
            modifier = Modifier.fillMaxWidth().height(140.dp),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                focusedBorderColor = PrimaryGreen, unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                focusedContainerColor = Color(0xFF162133), unfocusedContainerColor = Color(0xFF162133)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.addDispute(description, disputeType)
                showSent = true
            },
            enabled = description.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Icon(Icons.Rounded.Send, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Envoyer la demande", fontWeight = FontWeight.Bold)
        }

        if (showSent) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.1f)), border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(28.dp))
                    Column {
                        Text("Demande envoyée !", color = PrimaryGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Un médiateur examinera votre dossier sous 24-48h", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
