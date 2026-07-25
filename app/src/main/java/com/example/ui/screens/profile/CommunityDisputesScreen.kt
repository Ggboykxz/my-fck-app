package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CommunityDispute
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CommunityDisputesScreen(viewModel: RentalViewModel, onBack: () -> Unit) {
    val disputes by viewModel.communityDisputes.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showNewDisputeDialog by remember { mutableStateOf(false) }
    var newReason by remember { mutableStateOf("") }
    var newDescription by remember { mutableStateOf("") }
    var expandedId by remember { mutableStateOf<Int?>(null) }

    BackHandler { onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Litiges Communautaires", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Ouverts", "Mes signalements").forEachIndexed { index, label ->
                FilterChip(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    label = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryGreen.copy(alpha = 0.2f),
                        selectedLabelColor = PrimaryGreen,
                        containerColor = Color(0xFF162133),
                        labelColor = Color.White.copy(alpha = 0.6f)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = Color.White.copy(alpha = 0.12f),
                        selectedBorderColor = PrimaryGreen.copy(alpha = 0.4f),
                        enabled = true,
                        selected = selectedTab == index
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val filteredDisputes = if (selectedTab == 1) disputes.filter { it.reporterId == 1 } else disputes

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredDisputes, key = { it.id }) { dispute ->
                val isExpanded = expandedId == dispute.id
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(dispute.reason, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Signalé par ${if (dispute.reporterId == 1) "Vous" else "Utilisateur #${dispute.reporterId}"}", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                            }
                            StatusBadge(
                                text = when (dispute.status) {
                                    "open" -> "Ouvert"
                                    "investigating" -> "Enquête"
                                    "resolved" -> "Résolu"
                                    else -> "Fermé"
                                },
                                color = when (dispute.status) {
                                    "open" -> Color(0xFFFFB300)
                                    "investigating" -> Color(0xFF4FC3F7)
                                    "resolved" -> PrimaryGreen
                                    else -> Color.Gray
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Votes: ${dispute.communityVotes}",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 11.sp
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(
                                    onClick = { viewModel.voteDispute(dispute.id, 1) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Rounded.ThumbUp, contentDescription = "+1", tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = { viewModel.voteDispute(dispute.id, -1) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Rounded.ThumbDown, contentDescription = "-1", tint = Color(0xFFEF5350), modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        if (isExpanded) {
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(dispute.description, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, lineHeight = 18.sp)

                            Spacer(modifier = Modifier.height(8.dp))

                            if (dispute.evidence.isNotEmpty()) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    dispute.evidence.forEach { ev ->
                                        Surface(
                                            color = Color(0xFF4FC3F7).copy(alpha = 0.12f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(ev, color = Color(0xFF4FC3F7), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                        }
                                    }
                                }
                            }

                            Surface(
                                onClick = {
                                    val mockEvidence = "Preuve_${System.currentTimeMillis() % 10000}.jpg"
                                    viewModel.addEvidenceToDispute(dispute.id, mockEvidence)
                                },
                                color = Color(0xFF4FC3F7).copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Rounded.Add, contentDescription = null, tint = Color(0xFF4FC3F7), modifier = Modifier.size(14.dp))
                                    Text("Ajouter une preuve", color = Color(0xFF4FC3F7), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (dispute.resolution != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.1f))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("Résolution", color = PrimaryGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(dispute.resolution, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        Text(
                            text = if (isExpanded) "Moins" else "Détails",
                            color = PrimaryGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedId = if (isExpanded) null else dispute.id }
                                .padding(top = 8.dp)
                        )
                    }
                }
            }

            if (filteredDisputes.isEmpty()) {
                item {
                    AnimatedEmptyState(
                        icon = Icons.Rounded.Gavel,
                        title = "Aucun litige",
                        subtitle = "Pas de signalement pour le moment"
                    )
                }
            }
        }

        Button(
            onClick = { showNewDisputeDialog = true },
            modifier = Modifier.fillMaxWidth().height(48.dp).padding(bottom = 12.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
        ) {
            Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Nouveau signalement", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandNavy)
        }
    }

    if (showNewDisputeDialog) {
        AlertDialog(
            onDismissRequest = { showNewDisputeDialog = false },
            containerColor = Color(0xFF162133),
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(alpha = 0.8f),
            title = { Text("Nouveau signalement", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newReason,
                        onValueChange = { newReason = it },
                        placeholder = { Text("Raison", color = Color.White.copy(alpha = 0.4f)) },
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
                    OutlinedTextField(
                        value = newDescription,
                        onValueChange = { newDescription = it },
                        placeholder = { Text("Description", color = Color.White.copy(alpha = 0.4f)) },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
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
                Button(
                    onClick = {
                        if (newReason.isNotBlank()) {
                            viewModel.insertCommunityDispute(
                                CommunityDispute(
                                    listingId = 0,
                                    reporterId = 1,
                                    reportedUserId = 0,
                                    reason = newReason,
                                    description = newDescription
                                )
                            )
                            showNewDisputeDialog = false
                            newReason = ""
                            newDescription = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Envoyer", fontWeight = FontWeight.Bold, color = BrandNavy)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewDisputeDialog = false }) {
                    Text("Annuler", color = Color.White.copy(alpha = 0.6f))
                }
            }
        )
    }
}
