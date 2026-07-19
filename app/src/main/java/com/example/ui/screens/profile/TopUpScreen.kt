package com.example.ui.screens.profile

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel
import kotlinx.coroutines.delay

@Composable
fun TopUpScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val balance by viewModel.walletBalance.collectAsState()
    var selectedAmount by remember { mutableIntStateOf(0) }
    var customAmount by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("Airtel Money") }
    var phoneNumber by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    val presetAmounts = listOf(5000, 10000, 25000, 50000, 100000)
    val finalAmount = if (customAmount.isNotBlank()) customAmount.toIntOrNull() ?: 0 else selectedAmount

    BackHandler { onBack() }

    if (showSuccess) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFF0B1526)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                modifier = Modifier.padding(32.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier.size(80.dp).clip(CircleShape).background(Color(0xFF4CAF50).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = "Succès", tint = Color(0xFF4CAF50), modifier = Modifier.size(48.dp))
                    }
                    Text("Recharge Réussie !", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "$finalAmount FCFA ajoutés à votre portefeuille",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Nouveau solde : ${balance + finalAmount} FCFA",
                        color = PrimaryGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = { showSuccess = false; onBack() },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Terminer", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        return
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp).verticalScroll(rememberScrollState())
    ) {
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
            Text("Recharger le Portefeuille", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Solde actuel", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                Text("$balance FCFA", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text("MONTANT", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presetAmounts.take(3).forEach { amount ->
                val isSelected = selectedAmount == amount && customAmount.isBlank()
                Surface(
                    onClick = { selectedAmount = amount; customAmount = "" },
                    modifier = Modifier.weight(1f),
                    color = if (isSelected) PrimaryGreen.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.06f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (isSelected) PrimaryGreen else Color.White.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = "${amount / 1000}K",
                        color = if (isSelected) PrimaryGreen else Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presetAmounts.drop(3).forEach { amount ->
                val isSelected = selectedAmount == amount && customAmount.isBlank()
                Surface(
                    onClick = { selectedAmount = amount; customAmount = "" },
                    modifier = Modifier.weight(1f),
                    color = if (isSelected) PrimaryGreen.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.06f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (isSelected) PrimaryGreen else Color.White.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = "${amount / 1000}K",
                        color = if (isSelected) PrimaryGreen else Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = customAmount,
            onValueChange = { customAmount = it.filter { c -> c.isDigit() }; selectedAmount = 0 },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            label = { Text("Montant personnalisé", color = Color.White.copy(alpha = 0.5f)) },
            prefix = { Text("FCFA ", color = PrimaryGreen, fontWeight = FontWeight.Bold) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                focusedContainerColor = Color(0xFF162133),
                unfocusedContainerColor = Color(0xFF162133)
            )
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text("MODE DE PAIEMENT", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(10.dp))

        listOf(
            Triple("Airtel Money", BrandAirtel, Color(0xFF381519)),
            Triple("Moov Money", BrandMoov, Color(0xFF0E2235))
        ).forEach { (name, tint, bg) ->
            val isSelected = selectedMethod == name
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp).clickable { selectedMethod = name },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(2.dp, if (isSelected) tint.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(bg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Phone, contentDescription = name, tint = tint, modifier = Modifier.size(22.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text("Mobile Money", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                    }
                    if (isSelected) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = "Sélectionné", tint = tint, modifier = Modifier.size(22.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("NUMÉRO DE TÉLÉPHONE", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            placeholder = { Text("+241 07 XX XX XX", color = Color.White.copy(alpha = 0.3f)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                focusedContainerColor = Color(0xFF162133),
                unfocusedContainerColor = Color(0xFF162133)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A2A)),
            border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Rounded.Info, contentDescription = "Info", tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                Column {
                    Text("Frais de transaction", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("0 FCFA — Gratuit pour la démo", color = PrimaryGreen, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                isProcessing = true
                viewModel.topUpWallet(finalAmount, selectedMethod)
                showSuccess = true
                isProcessing = false
            },
            enabled = finalAmount > 0 && phoneNumber.isNotBlank() && !isProcessing,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen,
                contentColor = BrandNavy,
                disabledContainerColor = PrimaryGreen.copy(alpha = 0.3f)
            )
        ) {
            if (isProcessing) {
                CircularProgressIndicator(modifier = Modifier.size(22.dp), color = BrandNavy, strokeWidth = 2.dp)
            } else {
                Icon(Icons.Rounded.AddCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Recharger $finalAmount FCFA", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
