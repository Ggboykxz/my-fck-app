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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SplitPayment
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel

@Composable
fun SplitPaymentScreen(bookingId: Int, viewModel: RentalViewModel, onBack: () -> Unit) {
    val splitPayments by viewModel.splitPayments.collectAsState()
    var totalAmount by remember { mutableStateOf("150000") }
    var splits by remember { mutableStateOf(listOf("Vous: 50%", "Sophie: 25%", "Paul: 25%")) }
    var newPersonName by remember { mutableStateOf("") }
    var newPersonPercent by remember { mutableStateOf("50") }
    var showAddPerson by remember { mutableStateOf(false) }

    val paymentStatuses = listOf(
        Triple("Vous", "Payé", PrimaryGreen),
        Triple("Sophie Nguema", "En attente", Color(0xFFFFB300)),
        Triple("Paul Obiang", "En retard", Color(0xFFEF5350))
    )

    BackHandler { onBack() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Paiement Partagé", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Montant Total", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                    Text(formatPriceCfa(totalAmount.toIntOrNull() ?: 150000), color = PrimaryGreen, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFFFFB300).copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Réservation #$bookingId", color = Color(0xFFFFB300), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                    }
                }
            }
        }

        item {
            SectionHeader(title = "Configuration du partage")
            Spacer(modifier = Modifier.height(8.dp))

            splits.forEachIndexed { index, split ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimaryGreen.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${index + 1}", color = PrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(split, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = { splits = splits.toMutableList().apply { removeAt(index) } },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = null, tint = Color(0xFFEF5350), modifier = Modifier.size(14.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (showAddPerson) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B2A))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newPersonName,
                            onValueChange = { newPersonName = it },
                            placeholder = { Text("Nom", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = PrimaryGreen,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                                focusedContainerColor = Color(0xFF162133),
                                unfocusedContainerColor = Color(0xFF162133)
                            )
                        )
                        OutlinedTextField(
                            value = newPersonPercent,
                            onValueChange = { newPersonPercent = it },
                            placeholder = { Text("%", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp) },
                            modifier = Modifier.width(60.dp),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = PrimaryGreen,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                                focusedContainerColor = Color(0xFF162133),
                                unfocusedContainerColor = Color(0xFF162133)
                            )
                        )
                        IconButton(
                            onClick = {
                                if (newPersonName.isNotBlank()) {
                                    splits = splits + "${newPersonName}: ${newPersonPercent}%"
                                    newPersonName = ""
                                    newPersonPercent = "50"
                                    showAddPerson = false
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Rounded.Check, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            } else {
                Surface(
                    onClick = { showAddPerson = true },
                    color = Color.White.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ajouter une personne", color = PrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            SectionHeader(title = "Statut des paiements")
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(paymentStatuses) { (name, status, color) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    UserAvatar(name = name, size = 36.dp, backgroundColor = color.copy(alpha = 0.2f), textColor = color)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(status, color = color, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                    StatusBadge(text = status, color = color)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.insertSplitPayment(
                        SplitPayment(
                            bookingId = bookingId,
                            totalAmount = totalAmount.toIntOrNull() ?: 150000,
                            splits = splits,
                            status = "partial"
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Icon(Icons.Rounded.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Envoyer les demandes", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandNavy)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    viewModel.showSnackbar("Règlement effectué !")
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryGreen),
                border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))
            ) {
                Text("Régler les soldes", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
