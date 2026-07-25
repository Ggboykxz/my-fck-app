package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReferralTracking
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel

@Composable
fun ReferralTrackingScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val referralList by viewModel.referralTrackingList.collectAsState()
    val referralCount by viewModel.referralCount.collectAsState()
    val referralEarnings by viewModel.referralEarnings.collectAsState()
    val context = LocalContext.current
    val referralCode = "LOCALL-MC-2026"

    val mockLeaderboard = listOf(
        Triple("Kofi M.", 12, 60000),
        Triple("Sophie N.", 8, 40000),
        Triple("Vous", referralCount, referralEarnings),
        Triple("Paul O.", 5, 25000),
        Triple("Marie-Claire", 3, 15000)
    ).sortedByDescending { it.second }

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
                Text("Parrainage", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Box(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0C2417)),
                border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("VOTRE CODE DE PARRAINAGE", color = PrimaryGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                    Text(referralCode, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                    Surface(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("referral_code", referralCode)
                            clipboard.setPrimaryClip(clip)
                            viewModel.showSnackbar("Code copié dans le presse-papier")
                        },
                        color = PrimaryGreen,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Rounded.ContentCopy, contentDescription = null, tint = BrandNavy, modifier = Modifier.size(16.dp))
                            Text("Copier le code", color = BrandNavy, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Parrainés", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("$referralCount", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Gains", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(formatPriceCfa(referralEarnings), color = PrimaryGreen, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Inviter des amis", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f).clickable { },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF25D366).copy(alpha = 0.12f)),
                    border = BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Rounded.Chat, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(24.dp))
                        Text("WhatsApp", color = Color(0xFF25D366), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f).clickable { },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2196F3).copy(alpha = 0.12f)),
                    border = BorderStroke(1.dp, Color(0xFF2196F3).copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Rounded.Sms, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(24.dp))
                        Text("SMS", color = Color(0xFF2196F3), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f).clickable { },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4FC3F7).copy(alpha = 0.12f)),
                    border = BorderStroke(1.dp, Color(0xFF4FC3F7).copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Rounded.Share, contentDescription = null, tint = Color(0xFF4FC3F7), modifier = Modifier.size(24.dp))
                        Text("Autre", color = Color(0xFF4FC3F7), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Historique des parrainages", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(referralList, key = { it.id }) { ref ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0C2417)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            ref.referredUserName.first().toString(),
                            color = PrimaryGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(ref.referredUserName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(
                            when (ref.status) {
                                "verified" -> "Vérifié"
                                "pending" -> "En attente"
                                else -> ref.status
                            },
                            fontSize = 11.sp,
                            color = if (ref.status == "verified") PrimaryGreen else Color(0xFFFFB300)
                        )
                    }
                    if (ref.rewardEarned > 0) {
                        Text("+${formatPriceCfa(ref.rewardEarned)}", color = PrimaryGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))

            Text("Classement des parrains", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(mockLeaderboard, key = { it.first }) { (name, count, earnings) ->
            val isMe = name == "Vous"
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isMe) Color(0xFF0C2417) else Color(0xFF162133)
                ),
                border = BorderStroke(1.dp, if (isMe) PrimaryGreen.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val rank = mockLeaderboard.indexOfFirst { it.first == name } + 1
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (rank == 1) Color(0xFFFFB300).copy(alpha = 0.2f) else if (isMe) PrimaryGreen.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.06f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "$rank",
                            color = if (rank == 1) Color(0xFFFFB300) else if (isMe) PrimaryGreen else Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(name, color = if (isMe) PrimaryGreen else Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("$count parrainages", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                    }
                    Text(formatPriceCfa(earnings), color = if (isMe) PrimaryGreen else Color.White.copy(alpha = 0.7f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
