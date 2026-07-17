package com.example.ui.screens

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CommunityDispute
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun UserProfileScreen(userId: Int, viewModel: RentalViewModel, onBack: () -> Unit) {
    val userName by viewModel.userName.collectAsState()
    val isFollowing by viewModel.isFollowing(userId).collectAsState(initial = null)
    val followerCount by viewModel.getFollowerCount(userId).collectAsState(initial = 0)
    val followingCount by viewModel.getFollowingCount(userId).collectAsState(initial = 0)
    val badges by viewModel.getVerificationBadges(userId).collectAsState(initial = emptyList())
    val bookings by viewModel.bookings.collectAsState()
    var showReportDialog by remember { mutableStateOf(false) }
    var reportReason by remember { mutableStateOf("") }
    var reportDescription by remember { mutableStateOf("") }

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
                Text("Profil Utilisateur", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(modifier = Modifier.size(96.dp)) {
                UserAvatar(
                    name = userName,
                    size = 88.dp,
                    backgroundColor = PrimaryGreen,
                    textColor = BrandNavy
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(PrimaryGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.CameraAlt, contentDescription = null, tint = BrandNavy, modifier = Modifier.size(14.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(userName, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Libreville, Gabon", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
            Text("Membre depuis Janvier 2025", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = PrimaryGreen.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Passionné de location, toujours à la recherche de bonnes affaires",
                    color = PrimaryGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TrustScoreBar(score = 75f, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$followerCount", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Abonnés", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$followingCount", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Abonnements", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${bookings.size}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Réservations", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("4.8", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Note", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            SectionHeader(title = "Badges de vérification")
            Spacer(modifier = Modifier.height(8.dp))

            val verificationTypes = listOf(
                Triple("Téléphone", Icons.Rounded.Phone, Color(0xFF4FC3F7)),
                Triple("Email", Icons.Rounded.Email, Color(0xFFFFB300)),
                Triple("Carte d'identité", Icons.Rounded.Badge, Color(0xFFAB47BC)),
                Triple("Adresse", Icons.Rounded.LocationOn, PrimaryGreen)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                verificationTypes.forEach { (label, icon, color) ->
                    val verified = badges.any { it.badgeType == label.lowercase().replace(" ", "_") }
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (verified) color.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(icon, contentDescription = null, tint = if (verified) color else Color.White.copy(alpha = 0.3f), modifier = Modifier.size(20.dp))
                            Text(label, fontSize = 9.sp, color = if (verified) color else Color.White.copy(alpha = 0.4f), fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            SectionHeader(title = "Badges obtenus")
            Spacer(modifier = Modifier.height(8.dp))

            val achievementBadges = listOf(
                Triple("Top Locator", Icons.Rounded.EmojiEvents, Color(0xFFFFB300)),
                Triple("Vérifié", Icons.Rounded.VerifiedUser, PrimaryGreen),
                Triple("Réactif", Icons.Rounded.Speed, Color(0xFF4FC3F7)),
                Triple("Ponctuel", Icons.Rounded.Schedule, Color(0xFFAB47BC))
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                achievementBadges.forEach { (label, icon, color) ->
                    BadgeChip(label = label, icon = icon, color = color, modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { viewModel.toggleFollow(userId) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFollowing != null) Color.White.copy(alpha = 0.1f) else PrimaryGreen,
                    contentColor = if (isFollowing != null) Color.White else BrandNavy
                ),
                border = if (isFollowing != null) BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)) else null
            ) {
                Icon(
                    if (isFollowing != null) Icons.Rounded.PersonRemove else Icons.Rounded.PersonAdd,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (isFollowing != null) "Se désabonner" else "Suivre",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            SectionHeader(title = "Annonces récentes")
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Appartement Chic Vue Mer", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("75 000 F / jour • Batterie IV", color = PrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text("Immobilier • Vérifié", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SectionHeader(title = "Avis reçus")
            Spacer(modifier = Modifier.height(8.dp))

            val mockReviews = listOf(
                Pair("Sophie Nguema", "Très fiable, bon communicant. Je recommande !"),
                Pair("Paul Obiang", "Respectueux des biens, paiement ponctuel.")
            )
            mockReviews.forEach { (author, comment) ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(author, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                repeat(5) {
                                    Icon(Icons.Rounded.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                        Text(comment, fontSize = 12.sp, color = Color.White.copy(alpha = 0.65f), lineHeight = 18.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = { showReportDialog = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF5350)),
                border = BorderStroke(1.dp, Color(0xFFEF5350).copy(alpha = 0.3f))
            ) {
                Icon(Icons.Rounded.Flag, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Signaler cet utilisateur", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            containerColor = Color(0xFF162133),
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(alpha = 0.8f),
            title = { Text("Signaler un utilisateur", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = reportReason,
                        onValueChange = { reportReason = it },
                        placeholder = { Text("Raison du signalement", color = Color.White.copy(alpha = 0.4f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFEF5350),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedContainerColor = Color(0xFF0D1B2A),
                            unfocusedContainerColor = Color(0xFF0D1B2A)
                        )
                    )
                    OutlinedTextField(
                        value = reportDescription,
                        onValueChange = { reportDescription = it },
                        placeholder = { Text("Description détaillée", color = Color.White.copy(alpha = 0.4f)) },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFEF5350),
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
                        if (reportReason.isNotBlank()) {
                            viewModel.insertCommunityDispute(
                                CommunityDispute(
                                    listingId = 0,
                                    reporterId = 1,
                                    reportedUserId = userId,
                                    reason = reportReason,
                                    description = reportDescription
                                )
                            )
                            showReportDialog = false
                            reportReason = ""
                            reportDescription = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Envoyer", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Annuler", color = Color.White.copy(alpha = 0.6f))
                }
            }
        )
    }
}
