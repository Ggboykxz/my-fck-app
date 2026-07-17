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
import com.example.data.model.BookingEscrow
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EscrowScreen(viewModel: RentalViewModel, onBack: () -> Unit) {
    val escrows by viewModel.escrows.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf("Actifs", "Libérés", "Remboursés")
    val filteredEscrows = when (selectedTab) {
        0 -> escrows.filter { it.status == "held" }
        1 -> escrows.filter { it.status == "released" }
        2 -> escrows.filter { it.status == "refunded" }
        else -> escrows
    }

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
            Text("Séquestre de Fonds", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabs.forEachIndexed { index, label ->
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

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredEscrows, key = { it.id }) { escrow ->
                EscrowCard(
                    escrow = escrow,
                    onRelease = { viewModel.releaseEscrow(escrow.id) },
                    onRefund = { viewModel.refundEscrow(escrow.id) }
                )
            }

            if (filteredEscrows.isEmpty()) {
                item {
                    AnimatedEmptyState(
                        icon = Icons.Rounded.AccountBalance,
                        title = "Aucun séquestre",
                        subtitle = when (selectedTab) {
                            0 -> "Pas de fonds en attente"
                            1 -> "Pas de fonds libérés"
                            2 -> "Pas de fonds remboursés"
                            else -> "Aucun résultat"
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EscrowCard(
    escrow: BookingEscrow,
    onRelease: () -> Unit,
    onRefund: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
    val statusColor = when (escrow.status) {
        "held" -> Color(0xFFFFB300)
        "released" -> PrimaryGreen
        "refunded" -> Color(0xFF4FC3F7)
        else -> Color.Gray
    }
    val statusLabel = when (escrow.status) {
        "held" -> "En séquestre"
        "released" -> "Libéré"
        "refunded" -> "Remboursé"
        else -> "Inconnu"
    }

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
                Column {
                    Text("Séquestre #${escrow.id}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Réservation #${escrow.bookingId}", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                }
                StatusBadge(text = statusLabel, color = statusColor)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Montant", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                    Text(formatPriceCfa(escrow.amount), color = PrimaryGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Séquestré le", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                    Text(dateFormat.format(Date(escrow.heldAt)), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            if (escrow.status == "held") {
                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onRelease,
                        modifier = Modifier.weight(1f).height(36.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) {
                        Text("Libérer", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                    }
                    OutlinedButton(
                        onClick = onRefund,
                        modifier = Modifier.weight(1f).height(36.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF5350)),
                        border = BorderStroke(1.dp, Color(0xFFEF5350).copy(alpha = 0.3f))
                    ) {
                        Text("Rembourser", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (escrow.releasedAt != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Libéré le ${dateFormat.format(Date(escrow.releasedAt))}", color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
            }
        }
    }
}
