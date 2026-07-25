package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel

// ==================== INVITE FRIEND SCREEN ====================
@Composable
fun InviteFriendScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val inviteCode = "LOCALL-2026-GABON"
    val context = LocalContext.current
    val referralCount by viewModel.referralCount.collectAsState()
    val referralEarnings by viewModel.referralEarnings.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.08f), CircleShape)) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Inviter un ami", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(40.dp))

        Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(PrimaryGreen.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
            Icon(Icons.Rounded.CardGiftcard, contentDescription = "Récompenses", tint = PrimaryGreen, modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Gagnez des récompenses !", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Invitez vos amis et recevez chacun 5 000 F CFA de crédit pour votre prochaine location.", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp, textAlign = TextAlign.Center, lineHeight = 20.sp)

        Spacer(modifier = Modifier.height(32.dp))

        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)), border = BorderStroke(2.dp, PrimaryGreen.copy(alpha = 0.3f))) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Votre code d'invitation", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(inviteCode, color = PrimaryGreen, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        viewModel.onFirstShare()
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Rejoins LocAll avec mon code $inviteCode et gagne 5 000 F CFA ! https://locall.app/invite/$inviteCode")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Partager le code"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Rounded.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Partager le code", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$referralCount", color = PrimaryGreen, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Amis invités", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                }
            }
            Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${referralEarnings / 1000}", color = Color(0xFFFFB300), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    Text("F CFA gagnés", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                }
            }
        }
    }
}

// ==================== RATING SCREEN ====================
@Composable
fun RatingScreen(
    viewModel: RentalViewModel,
    rentalItemTitle: String,
    onBack: () -> Unit,
    onSubmitted: () -> Unit
) {
    var rating by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.08f), CircleShape)) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Donner un avis", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(40.dp))

        if (submitted) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Icon(Icons.Rounded.CheckCircle, contentDescription = "Avis envoyé", tint = PrimaryGreen, modifier = Modifier.size(72.dp))
                    Text("Merci pour votre avis !", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Votre retour aide la communauté LocAll", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
                    Button(onClick = { onSubmitted() }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy), shape = RoundedCornerShape(12.dp)) {
                        Text("Retour", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Text("Comment évaluez-vous \"$rentalItemTitle\" ?", color = Color.White.copy(alpha = 0.7f), fontSize = 15.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                for (i in 1..5) {
                    IconButton(onClick = { rating = i }) {
                        Icon(
                            imageVector = if (i <= rating) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                            contentDescription = "$i étoiles",
                            tint = if (i <= rating) Color(0xFFFFB300) else Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("VOTRE COMMENTAIRE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                placeholder = { Text("Décrivez votre expérience...", color = Color.White.copy(alpha = 0.3f)) },
                modifier = Modifier.fillMaxWidth().height(140.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    focusedBorderColor = PrimaryGreen, unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                    focusedContainerColor = Color(0xFF162133), unfocusedContainerColor = Color(0xFF162133)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { if (rating > 0) { viewModel.addReview(viewModel.selectedItem.value?.id ?: 1, rating, comment); submitted = true; onSubmitted() } },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (rating > 0) PrimaryGreen else Color.White.copy(alpha = 0.1f), contentColor = BrandNavy),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Publier l'avis", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==================== LEADERBOARD SCREEN ====================
@Composable
fun LeaderboardScreen(
    onBack: () -> Unit
) {
    val leaderboardData = listOf(
        mapOf("name" to "Kwame Asante", "rating" to "4.97", "city" to "Libreville"),
        mapOf("name" to "Marie-Claire Obiang", "rating" to "4.95", "city" to "Port-Gentil"),
        mapOf("name" to "Stéphane Koumba", "rating" to "4.92", "city" to "Libreville"),
        mapOf("name" to "Patricia Ndong", "rating" to "4.88", "city" to "Franceville"),
        mapOf("name" to "Rodrigue Mintsa", "rating" to "4.85", "city" to "Libreville"),
        mapOf("name" to "Sophie Nguema", "rating" to "4.82", "city" to "Port-Gentil"),
        mapOf("name" to "David Ogoula", "rating" to "4.80", "city" to "Libreville"),
        mapOf("name" to "Aimée Mboumba", "rating" to "4.78", "city" to "Owendo"),
        mapOf("name" to "Bernadette Nguéma", "rating" to "4.75", "city" to "Libreville"),
        mapOf("name" to "Françoise Limbaka", "rating" to "4.72", "city" to "Port-Gentil")
    )
    val medals = listOf("🥇", "🥈", "🥉")

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.08f), CircleShape)) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Classement", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Top Propriétaires", color = PrimaryGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Classement basé sur les notes et avis", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(leaderboardData.size, key = { it }) { index ->
                val entry = leaderboardData[index]
                val name = entry["name"] ?: ""
                val rating = entry["rating"] ?: ""
                val city = entry["city"] ?: ""
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (index < 3) PrimaryGreen.copy(alpha = 0.08f) else Color(0xFF162133)
                    ),
                    border = BorderStroke(1.dp, if (index < 3) PrimaryGreen.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.08f))
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = if (index < 3) medals[index] else "${index + 1}",
                            fontSize = if (index < 3) 22.sp else 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (index < 3) Color.White else Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.width(36.dp),
                            textAlign = TextAlign.Center
                        )
                        Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(PrimaryGreen.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.Person, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(24.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(city, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(rating, color = PrimaryGreen, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                            Icon(Icons.Rounded.Star, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }
        }
    }
}

// ==================== ACHIEVEMENTS SCREEN ====================
@Composable
fun AchievementsScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    data class AchievementData(val id: Int, val title: String, val desc: String, val icon: ImageVector)
    val achievements = listOf(
        AchievementData(0, "Premier Pas", "Créez votre premier compte", Icons.Rounded.PersonAdd),
        AchievementData(1, "Première Location", "Effectuez votre première réservation", Icons.Rounded.CarRental),
        AchievementData(2, "Propriétaire Actif", "Publiez 3 annonces minimum", Icons.Rounded.Home),
        AchievementData(3, "Groupe Social", "Invitez 5 amis via le parrainage", Icons.Rounded.Group),
        AchievementData(4, "Fidélité", "Cumulez 10 réservations", Icons.Rounded.EmojiEvents),
        AchievementData(5, "Confiance Verte", "Obtenez la vérification d'identité", Icons.Rounded.Verified),
        AchievementData(6, "Super Hôte", "Maintenez une note >= 4.8 sur 10 avis", Icons.Rounded.Star),
        AchievementData(7, "Réactif", "Répondez en moins de 1h pendant 30 jours", Icons.Rounded.Timer),
        AchievementData(8, "Événementier", "Louez du matériel événementiel 5 fois", Icons.Rounded.Celebration),
        AchievementData(9, "Explorateur", "Louez dans 3 villes différentes", Icons.Rounded.Explore)
    )
    val unlockedIds by viewModel.unlockedAchievements.collectAsState()
    val unlockedCount = unlockedIds.size

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.08f), CircleShape)) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Mes Succès", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$unlockedCount / ${achievements.size}", color = PrimaryGreen, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Text("succès débloqués", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { unlockedCount.toFloat() / achievements.size },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = PrimaryGreen,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(achievements, key = { it.id }, contentType = { "achievement" }) { achievement ->
                val isUnlocked = achievement.id in unlockedIds
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUnlocked) PrimaryGreen.copy(alpha = 0.08f) else Color(0xFF162133)
                    ),
                    border = BorderStroke(1.dp, if (isUnlocked) PrimaryGreen.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.08f))
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(if (isUnlocked) PrimaryGreen.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.06f)), contentAlignment = Alignment.Center) {
                            Icon(achievement.icon, contentDescription = null, tint = if (isUnlocked) PrimaryGreen else Color.White.copy(alpha = 0.3f), modifier = Modifier.size(24.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(achievement.title, color = if (isUnlocked) Color.White else Color.White.copy(alpha = 0.5f), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(achievement.desc, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                        }
                        if (isUnlocked) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = "Débloqué", tint = PrimaryGreen, modifier = Modifier.size(22.dp))
                        } else {
                            Surface(
                                onClick = { viewModel.unlockAchievement(achievement.id) },
                                color = PrimaryGreen.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))
                            ) {
                                Text("Débloquer", color = PrimaryGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== FLASH OFFERS SCREEN ====================
@Composable
fun FlashOffersScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val loyaltyPoints by viewModel.loyaltyPoints.collectAsState()
    val claimedOffers by viewModel.claimedFlashOffers.collectAsState()
    val flashOffers = listOf(
        Triple("Villa La Sablière", "-30%", "Se termine dans 2h 15min"),
        Triple("Toyota Hilux 4x4", "-25%", "Se termine dans 4h 30min"),
        Triple("Pack Sono Concert", "-20%", "Se termine dans 1h 45min"),
        Triple("Appartement F2 Sibang", "-15%", "Se termine dans 6h 00min"),
        Triple("Van Hiace 14 places", "-35%", "Se termine dans 3h 20min")
    )

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.08f), CircleShape)) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Offres Flash ⚡", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)), border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Rounded.Star, contentDescription = "Points", tint = PrimaryGreen, modifier = Modifier.size(28.dp))
                Column {
                    Text("$loyaltyPoints points disponibles", color = PrimaryGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Utilisez vos points pour réclamer des offres", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFF6F00).copy(alpha = 0.12f)), border = BorderStroke(1.dp, Color(0xFFFF6F00).copy(alpha = 0.3f))) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Rounded.FlashOn, contentDescription = null, tint = Color(0xFFFF6F00), modifier = Modifier.size(28.dp))
                Column {
                    Text("Dépêchez-vous !", color = Color(0xFFFF6F00), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Des réductions exclusives disparaissent bientôt", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(flashOffers, key = { it.first }, contentType = { "triple" }) { (title, discount, timer) ->
                val isClaimed = title in claimedOffers
                val cost = when (discount) {
                    "-30%" -> 500
                    "-25%" -> 400
                    "-20%" -> 300
                    "-15%" -> 200
                    "-35%" -> 600
                    else -> 250
                }
                Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)), border = BorderStroke(1.dp, Color(0xFFFF6F00).copy(alpha = 0.2f))) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFFF6F00).copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                            Text(discount, color = Color(0xFFFF6F00), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(timer, color = Color(0xFFFF6F00), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text("$cost points", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                        }
                        if (isClaimed) {
                            Surface(
                                color = PrimaryGreen.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(14.dp))
                                    Text("Réclamé", color = PrimaryGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            Button(
                                onClick = { viewModel.claimFlashOffer(title, cost) },
                                enabled = loyaltyPoints >= cost,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6F00), contentColor = Color.White, disabledContainerColor = Color.White.copy(alpha = 0.08f), disabledContentColor = Color.White.copy(alpha = 0.3f)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Réclamer", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== LOYALTY REDEEM SCREEN ====================
@Composable
fun LoyaltyRedeemScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val points by viewModel.loyaltyPoints.collectAsState()
    val rewards = listOf(
        Triple("Réduction 5 000 F", 5000, Icons.Rounded.Discount),
        Triple("Location gratuite 1 jour", 15000, Icons.Rounded.CardGiftcard),
        Triple("Upgrade véhicule", 10000, Icons.Rounded.Upgrade),
        Triple("Assurance offerte", 20000, Icons.Rounded.Shield),
        Triple("Cashback 10 000 F", 25000, Icons.Rounded.Payments)
    )

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.08f), CircleShape)) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Mes Points", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.1f)), border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Rounded.Star, contentDescription = "Points", tint = PrimaryGreen, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("$points", color = PrimaryGreen, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Text("points disponibles", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("RÉCOMPENSES DISPONIBLES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(rewards, key = { it.first }, contentType = { "triple" }) { (title, cost, icon) ->
                val canAfford = points >= cost
                Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(PrimaryGreen.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                            Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(24.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("$cost points", color = Color(0xFFFFB300), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = { viewModel.redeemLoyaltyPoints(cost, title) },
                            enabled = canAfford,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy, disabledContainerColor = Color.White.copy(alpha = 0.08f), disabledContentColor = Color.White.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("Échanger", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ==================== REWARDS & COUPONS SCREEN ====================
@Composable
fun RewardsCouponsScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val claimedRewards by viewModel.claimedRewards.collectAsState()
    val availableCoupons = listOf(
        Triple("PARRAINAGE", "5 000 F CFA de crédit", "Valide après 1ère utilisation"),
        Triple("FLASH20", "20% sur toute réservation flash", "Valide jusqu'au 31/08/2026"),
        Triple("FIDELE15", "15% pour les membres fidèles", "Valide jusqu'au 31/12/2026")
    )

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.08f), CircleShape)) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Récompenses & Coupons", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("MES COUPONS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(12.dp))

        if (claimedRewards.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.LocalOffer, contentDescription = null, tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Aucun coupon", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Réclamez des offres flash pour obtenir des coupons", color = Color.White.copy(alpha = 0.3f), fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(claimedRewards, key = { it.code }, contentType = { "reward" }) { reward ->
                    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)), border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.2f))) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Rounded.LocalOffer, contentDescription = "Coupon", tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                                Text(reward.code, color = PrimaryGreen, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                                Spacer(modifier = Modifier.weight(1f))
                                Surface(color = PrimaryGreen.copy(alpha = 0.12f), shape = RoundedCornerShape(8.dp)) {
                                    Text("Actif", color = PrimaryGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(reward.description, color = Color.White, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(reward.expiry, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        if (availableCoupons.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("COUPONS DISPONIBLES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(availableCoupons, key = { it.first }, contentType = { "available" }) { (code, description, expiry) ->
                    val isAlreadyClaimed = claimedRewards.any { it.code == code }
                    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)), border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Rounded.LocalOffer, contentDescription = "Coupon", tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                                    Text(code, color = Color(0xFFFFB300), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(description, color = Color.White, fontSize = 12.sp)
                                Text(expiry, color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
                            }
                            if (isAlreadyClaimed) {
                                Icon(Icons.Rounded.CheckCircle, contentDescription = "Réclamé", tint = PrimaryGreen, modifier = Modifier.size(22.dp))
                            } else {
                                Button(
                                    onClick = { viewModel.claimReward(code, description, expiry) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300), contentColor = Color(0xFF162133)),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Réclamer", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
