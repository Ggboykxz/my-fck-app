package com.example.ui.screens

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
    val activeInsurancePlan by viewModel.activeInsurancePlan.collectAsState()
    var selectedPlan by remember { mutableStateOf(activeInsurancePlan ?: "basic") }
    val showSubscribed = activeInsurancePlan != null
    val plans = listOf(
        Triple("basic", "Essentiel", "7 500 F CFA/jour"),
        Triple("standard", "Confort", "12 500 F CFA/jour"),
        Triple("premium", "Premium", "20 000 F CFA/jour")
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

        Icon(Icons.Rounded.Shield, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Text("Protégez votre location", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text("Choisissez une couverture adaptée à vos besoins", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(24.dp))

        plans.forEach { (id, name, price) ->
            val isSelected = selectedPlan == id
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { selectedPlan = id },
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
                        Text(price, color = PrimaryGreen, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Couverture incluse :", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                listOf("Dommages matériels", "Vol et tentative de vol", "Assistance routière 24/7", "Responsabilité civile").forEach { item ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Rounded.Check, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                        Text(item, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                }
            }
        }

        if (showSubscribed) {
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.1f)), border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(28.dp))
                    Column {
                        Text("Assurance souscrite !", color = PrimaryGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Vous êtes maintenant couvert pour cette location", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.subscribeInsurance(selectedPlan) },
            enabled = activeInsurancePlan == null,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp).padding(bottom = 16.dp)
        ) {
            Icon(Icons.Rounded.Shield, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Souscrire à l'assurance", fontWeight = FontWeight.Bold)
        }
    }
}

// ==================== DIGITAL DEPOSIT SCREEN ====================
@Composable
fun DigitalDepositScreen(
    onBack: () -> Unit
) {
    var depositMethod by remember { mutableStateOf("airtel") }
    var showPaid by remember { mutableStateOf(false) }

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

        Spacer(modifier = Modifier.height(24.dp))

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

        if (showPaid) {
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

        Button(
            onClick = { showPaid = true },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp).padding(bottom = 16.dp)
        ) {
            Text("Payer la caution de 50 000 F CFA", fontWeight = FontWeight.Bold)
        }
    }
}
