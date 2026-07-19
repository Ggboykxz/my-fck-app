package com.example.ui.screens.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.PromoCodeEntry
import com.example.ui.viewmodel.RentalViewModel

@Composable
fun PromoCodeScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val promos by viewModel.activePromos.collectAsState()
    var promoInput by remember { mutableStateOf("") }
    var showResult by remember { mutableStateOf<Boolean?>(null) }

    BackHandler { onBack() }

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
                Text("Codes Promo", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color(0xFFCE93D8).copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.LocalOffer, contentDescription = "Promo", tint = Color(0xFFCE93D8), modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Entrez votre code promo", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Obtenez des réductions sur vos réservations", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = promoInput,
                        onValueChange = { promoInput = it.uppercase(); showResult = null },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        placeholder = { Text("Ex: LOCALL20", color = Color.White.copy(alpha = 0.3f)) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFCE93D8),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedContainerColor = Color(0xFF0D1B2A),
                            unfocusedContainerColor = Color(0xFF0D1B2A)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val success = viewModel.applyPromoCode(promoInput)
                            showResult = success
                        },
                        enabled = promoInput.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFCE93D8),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFFCE93D8).copy(alpha = 0.3f)
                        )
                    ) {
                        Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Appliquer le code", fontWeight = FontWeight.Bold)
                    }

                    if (showResult == true) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Code promo appliqué avec succès !", color = PrimaryGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    } else if (showResult == false && promoInput.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Code invalide ou expiré", color = Color(0xFFF44336), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "CODES DISPONIBLES",
                color = Color.White.copy(alpha = 0.45f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(promos, key = { it.id }) { promo ->
            PromoCard(promo = promo, onApply = {
                promoInput = promo.code
                viewModel.applyPromoCode(promo.code)
                showResult = true
            })
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Rounded.Lightbulb, contentDescription = "Astuce", tint = Color(0xFFFFB300), modifier = Modifier.size(22.dp))
                    Column {
                        Text("Astuce", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Partagez votre code de parrainage avec vos amis pour gagner 5 000 FCFA chacun !",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun PromoCard(promo: PromoCodeEntry, onApply: () -> Unit) {
    val isExpired = false
    val usesLeft = promo.maxUses - promo.usedCount

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isExpired) Color(0xFF0D1B2A) else Color(0xFF162133)),
        border = BorderStroke(1.dp, if (isExpired) Color.White.copy(alpha = 0.05f) else Color(0xFFCE93D8).copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFCE93D8).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${promo.discount}%", color = Color(0xFFCE93D8), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    Text("OFF", color = Color(0xFFCE93D8), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(promo.code, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(promo.description, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Expire: ${promo.validUntil}", color = Color.White.copy(alpha = 0.35f), fontSize = 11.sp)
                    Text("$usesLeft utilisations", color = Color(0xFFCE93D8), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
            if (!isExpired) {
                Surface(
                    onClick = onApply,
                    color = Color(0xFFCE93D8).copy(alpha = 0.12f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFCE93D8).copy(alpha = 0.3f))
                ) {
                    Text(
                        "Utiliser",
                        color = Color(0xFFCE93D8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}
