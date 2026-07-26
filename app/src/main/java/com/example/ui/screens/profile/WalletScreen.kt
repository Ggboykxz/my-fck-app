package com.example.ui.screens.profile

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel
import com.example.ui.viewmodel.WalletTxn
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WalletScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit,
    onTopUp: () -> Unit,
    onPromoCodes: () -> Unit
) {
    val balance by viewModel.walletBalance.collectAsState()
    val transactions by viewModel.walletTransactions.collectAsState()
    val selectedFilter by viewModel.selectedWalletFilter.collectAsState()
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var withdrawAmount by remember { mutableStateOf("") }
    LaunchedEffect(Unit) { delay(600); isLoading = false }
    LaunchedEffect(isRefreshing) { if (isRefreshing) { delay(800); isRefreshing = false } }
    BackHandler { onBack() }

    if (showWithdrawDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            containerColor = Color(0xFF162133),
            title = { Text("Retirer du portefeuille", color = Color.White) },
            text = {
                Column {
                    Text("Solde disponible : $balance FCFA", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = withdrawAmount,
                        onValueChange = { withdrawAmount = it.filter { c -> c.isDigit() } },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("Montant à retirer", color = Color.White.copy(alpha = 0.3f)) },
                        prefix = { Text("FCFA ", color = PrimaryGreen, fontWeight = FontWeight.Bold) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedContainerColor = Color(0xFF0F1A2A),
                            unfocusedContainerColor = Color(0xFF0F1A2A)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = withdrawAmount.toIntOrNull() ?: 0
                        if (amount > 0) {
                            viewModel.withdrawFromWallet(amount)
                            showWithdrawDialog = false
                            withdrawAmount = ""
                        }
                    },
                    enabled = (withdrawAmount.toIntOrNull() ?: 0) > 0 && (withdrawAmount.toIntOrNull() ?: 0) <= balance,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7), contentColor = Color.White)
                ) {
                    Text("Retirer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false; withdrawAmount = "" }) {
                    Text("Annuler", color = Color.White.copy(alpha = 0.6f))
                }
            }
        )
    }

    val filters = listOf("Toutes", "Recharges", "Paiements", "Gains", "Remboursements")
    val filteredTransactions = remember(transactions, selectedFilter) {
        when (selectedFilter) {
            "Recharges" -> transactions.filter { it.type == "topup" }
            "Paiements" -> transactions.filter { it.type == "payment" }
            "Gains" -> transactions.filter { it.type == "earning" }
            "Remboursements" -> transactions.filter { it.type == "refund" || it.type == "withdrawal" }
            else -> transactions
        }
    }

    if (isLoading) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.08f))) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Mon Portefeuille", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            repeat(4) {
                SkeletonBookingItem()
            }
        }
    } else {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.08f))
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Mon Portefeuille", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                IconButton(onClick = { isRefreshing = true }) {
                    if (isRefreshing) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color(0xFF4FC3F7))
                    } else {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Actualiser", tint = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(PrimaryGreen, Color(0xFF0FA04A), BrandNavy)
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("SOLDE DISPONIBLE", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$balance",
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text("FCFA", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WalletActionButton(
                    icon = Icons.Rounded.Add,
                    label = "Recharger",
                    color = PrimaryGreen,
                    onClick = onTopUp,
                    modifier = Modifier.weight(1f)
                )
                WalletActionButton(
                    icon = Icons.Rounded.AccountBalance,
                    label = "Retirer",
                    color = Color(0xFF4FC3F7),
                    onClick = { showWithdrawDialog = true },
                    modifier = Modifier.weight(1f)
                )
                WalletActionButton(
                    icon = Icons.Rounded.SwapHoriz,
                    label = "Transférer",
                    color = Color(0xFFFFB300),
                    onClick = { viewModel.showSnackbar("Transfert bientôt disponible") },
                    modifier = Modifier.weight(1f)
                )
                WalletActionButton(
                    icon = Icons.Rounded.LocalOffer,
                    label = "Promos",
                    color = Color(0xFFCE93D8),
                    onClick = onPromoCodes,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                filters.forEach { filter ->
                    val isActive = selectedFilter == filter
                    Surface(
                        onClick = { viewModel.setWalletFilter(filter) },
                        color = if (isActive) PrimaryGreen else Color.White.copy(alpha = 0.06f),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, if (isActive) PrimaryGreen else Color.White.copy(alpha = 0.1f))
                    ) {
                        Text(
                            text = filter,
                            color = if (isActive) BrandNavy else Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (filteredTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedEmptyState(
                        icon = Icons.Rounded.Receipt,
                        title = "Aucune transaction",
                        subtitle = "Vos transactions apparaîtront ici"
                    )
                }
            }
        } else {
            items(filteredTransactions, key = { it.id }) { txn ->
                WalletTransactionItem(txn = txn, modifier = Modifier.animateItem())
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
    }
}

@Composable
private fun WalletActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(22.dp))
            Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun WalletTransactionItem(txn: WalletTxn, modifier: Modifier = Modifier) {
    val isCredit = txn.amount > 0
    val iconBg = when (txn.type) {
        "topup" -> Color(0xFF4CAF50).copy(alpha = 0.15f)
        "payment" -> Color(0xFFF44336).copy(alpha = 0.15f)
        "earning" -> Color(0xFFFFB300).copy(alpha = 0.15f)
        "refund" -> Color(0xFF4FC3F7).copy(alpha = 0.15f)
        "withdrawal" -> Color(0xFF9C27B0).copy(alpha = 0.15f)
        else -> Color.White.copy(alpha = 0.05f)
    }
    val iconTint = when (txn.type) {
        "topup" -> Color(0xFF4CAF50)
        "payment" -> Color(0xFFF44336)
        "earning" -> Color(0xFFFFB300)
        "refund" -> Color(0xFF4FC3F7)
        "withdrawal" -> Color(0xFF9C27B0)
        else -> Color.White
    }
    val icon = when (txn.type) {
        "topup" -> Icons.Rounded.AddCircle
        "payment" -> Icons.Rounded.ShoppingCart
        "earning" -> Icons.Rounded.TrendingUp
        "refund" -> Icons.Rounded.Undo
        "withdrawal" -> Icons.Rounded.AccountBalance
        else -> Icons.Rounded.Receipt
    }
    val typeLabel = when (txn.type) {
        "topup" -> "Recharge"
        "payment" -> "Paiement"
        "earning" -> "Gain"
        "refund" -> "Remboursement"
        "withdrawal" -> "Retrait"
        else -> "Transaction"
    }

    Card(
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = typeLabel, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(txn.description, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(typeLabel, color = iconTint, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Text(txn.date, color = Color.White.copy(alpha = 0.35f), fontSize = 11.sp)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (isCredit) "+${txn.amount}" else "${txn.amount}",
                    color = if (isCredit) Color(0xFF4CAF50) else Color(0xFFF44336),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("FCFA", color = Color.White.copy(alpha = 0.35f), fontSize = 10.sp)
            }
        }
    }
}
