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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PaymentReceipt
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PaymentReceiptScreen(viewModel: RentalViewModel, onBack: () -> Unit) {
    val receipts by viewModel.paymentReceipts.collectAsState()
    var selectedReceipt by remember { mutableStateOf<PaymentReceipt?>(null) }

    BackHandler { onBack() }

    if (selectedReceipt != null) {
        ReceiptDetail(receipt = selectedReceipt!!, viewModel = viewModel, onBack = { selectedReceipt = null })
    } else {
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
                Text("Reçus de Paiement", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(receipts, key = { it.id }) { receipt ->
                    ReceiptCard(receipt = receipt, onClick = { selectedReceipt = receipt })
                }

                if (receipts.isEmpty()) {
                    item {
                        AnimatedEmptyState(
                            icon = Icons.Rounded.Receipt,
                            title = "Aucun reçu",
                            subtitle = "Vos reçus de paiement apparaîtront ici"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptCard(receipt: PaymentReceipt, onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PrimaryGreen.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Receipt, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(receipt.receiptNumber, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(receipt.payerName + " → " + receipt.payeeName, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                Text(dateFormat.format(Date(receipt.date)), color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatPriceCfa(receipt.amount), color = PrimaryGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                StatusBadge(text = receipt.paymentMethod, color = Color(0xFF4FC3F7))
            }
        }
    }
}

@Composable
private fun ReceiptDetail(receipt: PaymentReceipt, viewModel: RentalViewModel, onBack: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)

    BackHandler { onBack() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
                Text("Détail du Reçu", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("LocAll", color = PrimaryGreen, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Location de biens au Gabon", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("REÇU DE PAIEMENT", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("N° Reçu", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                        Text(receipt.receiptNumber, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Date", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                        Text(dateFormat.format(Date(receipt.date)), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Payeur", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                        Text(receipt.payerName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Bénéficiaire", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                        Text(receipt.payeeName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Mode de paiement", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                        Text(receipt.paymentMethod, color = Color(0xFF4FC3F7), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(12.dp))

                    receipt.items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(item, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("TOTAL", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(formatPriceCfa(receipt.amount), color = PrimaryGreen, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Merci pour votre confiance !", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                    Text("LocAll Gabon — Louez tout, partout", color = Color.White.copy(alpha = 0.3f), fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.showSnackbar("PDF en cours de téléchargement...") },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryGreen),
                    border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))
                ) {
                    Icon(Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("PDF", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { viewModel.showSnackbar("Reçu partagé") },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Icon(Icons.Rounded.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Partager", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
