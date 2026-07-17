package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.*
import com.example.ui.navigation.ProfileNavHost
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

// NavHost-based ProfileNavigator (production)
@Composable
fun ProfileNavigator(viewModel: RentalViewModel) {
    val navController = rememberNavController()
    ProfileNavHost(navController = navController, viewModel = viewModel)
}

// ---------------- PROFILE MAIN SCREEN ----------------

@Composable
fun ProfileMainScreen(
    viewModel: RentalViewModel,
    onNavigate: (String) -> Unit,
    onEditProfile: () -> Unit
) {
    val isOwnerMode by viewModel.isOwnerMode.collectAsState()
    val verifStatus by viewModel.identityVerificationStatus.collectAsState()
    val language by viewModel.profileLanguage.collectAsState()
    val userName by viewModel.userName.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) { delay(600); isLoading = false }

    if (isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                "Tableau de Bord & Profil",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            repeat(5) {
                SkeletonBookingItem()
            }
        }
    } else {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(30.dp))
            
            // Header Title
            Text(
                "Tableau de Bord & Profil",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // User Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Profile Photo with badge overlay
                    Box(modifier = Modifier.size(72.dp)) {
                        UserAvatar(
                            name = userName,
                            size = 64.dp,
                            backgroundColor = PrimaryGreen,
                            textColor = BrandNavy
                        )
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryGreen)
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = BrandNavy, modifier = Modifier.size(10.dp))
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                userName,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            PointsChip(points = 1250)
                        }
                        Text(
                            maskPhoneNumber("+241 77 12 34 56") + " (Libreville)",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = PrimaryGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Propriétaire Vérifié",
                                color = PrimaryGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // Trust Score
            TrustScore(score = 85)

            Spacer(modifier = Modifier.height(12.dp))

            // Badge Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BadgeChip(label = "Top Locator", icon = Icons.Rounded.EmojiEvents, color = Color(0xFFFFB300))
                BadgeChip(label = "Vérifié", icon = Icons.Rounded.VerifiedUser, color = PrimaryGreen)
                BadgeChip(label = "Réactif", icon = Icons.Rounded.Speed, color = Color(0xFF4FC3F7))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Switch to Owner Mode Mode Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = if (isOwnerMode) Color(0xFF0C2417) else Color(0xFF162133)),
                border = BorderStroke(1.dp, if (isOwnerMode) PrimaryGreen.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isOwnerMode) PrimaryGreen.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isOwnerMode) Icons.Rounded.HomeWork else Icons.Rounded.Person,
                                contentDescription = null,
                                tint = if (isOwnerMode) PrimaryGreen else Color.White
                            )
                        }
                        Column {
                            Text(
                                "Mode Propriétaire",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                if (isOwnerMode) "Gérer vos revenus & annonces" else "Activer pour louer vos biens",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = isOwnerMode,
                        onCheckedChange = { viewModel.setOwnerMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BrandNavy,
                            checkedTrackColor = PrimaryGreen,
                            uncheckedThumbColor = Color.LightGray,
                            uncheckedTrackColor = Color.White.copy(alpha = 0.15f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Informations Personnelles
            ProfileOptionRow(
                icon = Icons.Rounded.Person,
                title = "Informations Personnelles",
                subtitle = "Modifier votre nom & téléphone",
                containerColor = PrimaryGreen.copy(alpha = 0.12f),
                iconTint = PrimaryGreen,
                onClick = onEditProfile
            )

            // Sub links Section depending on Mode
            Text(
                text = "Services & Gestion".uppercase(),
                color = Color.White.copy(alpha = 0.45f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )
        }

        if (isOwnerMode) {
            // OWNER MODE MENU ITEMS
            item {
                ProfileOptionRow(
                    icon = Icons.Rounded.Dashboard,
                    title = "Tableau de Bord Propriétaire",
                    subtitle = "Revenus, statistiques & gains",
                    containerColor = PrimaryGreen.copy(alpha = 0.12f),
                    iconTint = PrimaryGreen,
                    onClick = { onNavigate("dashboard") }
                )
                ProfileOptionRow(
                    icon = Icons.Rounded.HomeWork,
                    title = "Mes Annonces Actives",
                    subtitle = "Publications, validations & suspendus",
                    containerColor = Color(0xFF4FC3F7).copy(alpha = 0.12f),
                    iconTint = Color(0xFF4FC3F7),
                    onClick = { onNavigate("listings") }
                )
                ProfileOptionRow(
                    icon = Icons.Rounded.DateRange,
                    title = "Calendrier de Disponibilité",
                    subtitle = "Bloquer ou autoriser des dates",
                    containerColor = Color(0xFFFF9800).copy(alpha = 0.12f),
                    iconTint = Color(0xFFFF9800),
                    onClick = { onNavigate("calendar") }
                )
                ProfileOptionRow(
                    icon = Icons.Rounded.MoveToInbox,
                    title = "Réservations Reçues",
                    subtitle = "Gérer les remises & retours de biens",
                    containerColor = Color(0xFFAB47BC).copy(alpha = 0.12f),
                    iconTint = Color(0xFFAB47BC),
                    onClick = { onNavigate("bookings_received") }
                )
            }
        } else {
            // TENANT MODE MENU ITEMS
            item {
                ProfileOptionRow(
                    icon = Icons.Rounded.Task,
                    title = "Mes Réservations à venir",
                    subtitle = "Reçus, codes de retraits & dommages",
                    containerColor = PrimaryGreen.copy(alpha = 0.12f),
                    iconTint = PrimaryGreen,
                    onClick = { onNavigate("tenant_bookings") }
                )
                ProfileOptionRow(
                    icon = Icons.Rounded.VerifiedUser,
                    title = "Vérification d'Identité",
                    subtitle = "Statut actuel: $verifStatus",
                    containerColor = Color(0xFF4FC3F7).copy(alpha = 0.12f),
                    iconTint = Color(0xFF4FC3F7),
                    onClick = { onNavigate("identity") }
                )
                ProfileOptionRow(
                    icon = Icons.Rounded.Payment,
                    title = "Moyens de Paiement",
                    subtitle = "Airtel, Moov & Cartes Bancaires",
                    containerColor = Color(0xFFFF9800).copy(alpha = 0.12f),
                    iconTint = Color(0xFFFF9800),
                    onClick = { onNavigate("payment_methods") }
                )
            }
        }

        // COMMON SYSTEM SETTINGS MENU ITEMS
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Préférences & Aide".uppercase(),
                color = Color.White.copy(alpha = 0.45f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            ProfileOptionRow(
                icon = Icons.Rounded.GTranslate,
                title = "Paramètres de Langue",
                subtitle = "Sélectionné: $language",
                containerColor = Color(0xFF4FC3F7).copy(alpha = 0.12f),
                iconTint = Color(0xFF4FC3F7),
                onClick = { onNavigate("language") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.Notifications,
                title = "Notifications",
                subtitle = "Gérer vos alertes et préférences",
                containerColor = Color(0xFFFFB300).copy(alpha = 0.12f),
                iconTint = Color(0xFFFFB300),
                onClick = { onNavigate("notifications") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.Lock,
                title = "Sécurité & Mot de Passe",
                subtitle = "Changer le mot de passe confidentiel",
                containerColor = Color(0xFFEF5350).copy(alpha = 0.12f),
                iconTint = Color(0xFFEF5350),
                onClick = { onNavigate("security") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.Gavel,
                title = "Historique des Litiges",
                subtitle = "Suivre une procédure de médiation",
                containerColor = Color(0xFFFF9800).copy(alpha = 0.12f),
                iconTint = Color(0xFFFF9800),
                onClick = { onNavigate("disputes") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.HelpOutline,
                title = "Centre d'Aide & Support",
                subtitle = "FAQs Mobile Money, Assurance & Garanties",
                containerColor = PrimaryGreen.copy(alpha = 0.12f),
                iconTint = PrimaryGreen,
                onClick = { onNavigate("help") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.Payment,
                title = "Historique des Paiements",
                subtitle = "Vos transactions & reçus",
                containerColor = Color(0xFFFF9800).copy(alpha = 0.12f),
                iconTint = Color(0xFFFF9800),
                onClick = { onNavigate("payment_history") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.Settings,
                title = "Paramètres",
                subtitle = "Notifications, thème & géolocalisation",
                containerColor = Color(0xFF78909C).copy(alpha = 0.12f),
                iconTint = Color(0xFF78909C),
                onClick = { onNavigate("settings") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.CardGiftcard,
                title = "Inviter un Ami",
                subtitle = "Gagnez 5 000 F CFA par ami invité",
                containerColor = Color(0xFFAB47BC).copy(alpha = 0.12f),
                iconTint = Color(0xFFAB47BC),
                onClick = { onNavigate("invite_friend") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.Leaderboard,
                title = "Classement",
                subtitle = "Top propriétaires et locataires",
                containerColor = Color(0xFFFFB300).copy(alpha = 0.12f),
                iconTint = Color(0xFFFFB300),
                onClick = { onNavigate("leaderboard") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.EmojiEvents,
                title = "Mes Succès",
                subtitle = "Badges et réalisations débloqués",
                containerColor = Color(0xFFAB47BC).copy(alpha = 0.12f),
                iconTint = Color(0xFFAB47BC),
                onClick = { onNavigate("achievements") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.FlashOn,
                title = "Offres Flash",
                subtitle = "Réductions exclusives à durée limitée",
                containerColor = Color(0xFFFF6F00).copy(alpha = 0.12f),
                iconTint = Color(0xFFFF6F00),
                onClick = { onNavigate("flash_offers") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.Stars,
                title = "Mes Points",
                subtitle = "Échangez vos points contre des récompenses",
                containerColor = PrimaryGreen.copy(alpha = 0.12f),
                iconTint = PrimaryGreen,
                onClick = { onNavigate("loyalty_redeem") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.LocalOffer,
                title = "Récompenses & Coupons",
                subtitle = "Codes promo et avantages",
                containerColor = Color(0xFF4FC3F7).copy(alpha = 0.12f),
                iconTint = Color(0xFF4FC3F7),
                onClick = { onNavigate("rewards_coupons") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.Gavel,
                title = "Signaler un Litige",
                subtitle = "Médiation et résolution de conflits",
                containerColor = Color(0xFFEF5350).copy(alpha = 0.12f),
                iconTint = Color(0xFFEF5350),
                onClick = { onNavigate("dispute") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.Shield,
                title = "Assurance Location",
                subtitle = "Protégez vos biens avec une assurance",
                containerColor = PrimaryGreen.copy(alpha = 0.12f),
                iconTint = PrimaryGreen,
                onClick = { onNavigate("insurance") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.AccountBalance,
                title = "Caution Numérique",
                subtitle = "Paiement et remboursement des cautions",
                containerColor = Color(0xFF4FC3F7).copy(alpha = 0.12f),
                iconTint = Color(0xFF4FC3F7),
                onClick = { onNavigate("digital_deposit") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.Verified,
                title = "Vérification Temps Réel",
                subtitle = "Statut de vos documents de vérification",
                containerColor = Color(0xFFAB47BC).copy(alpha = 0.12f),
                iconTint = Color(0xFFAB47BC),
                onClick = { onNavigate("realtime_verification") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.CalendarMonth,
                title = "Calendrier Interactif",
                subtitle = "Disponibilités et réservations",
                containerColor = Color(0xFFFFB300).copy(alpha = 0.12f),
                iconTint = Color(0xFFFFB300),
                onClick = { onNavigate("interactive_calendar") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.BarChart,
                title = "Mes Statistiques",
                subtitle = "Réservations, dépenses & favoris",
                containerColor = Color(0xFF4FC3F7).copy(alpha = 0.12f),
                iconTint = Color(0xFF4FC3F7),
                onClick = { onNavigate("personal_stats") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.RateReview,
                title = "Donner un Avis",
                subtitle = "Évaluez une expérience de location",
                containerColor = Color(0xFFFFB300).copy(alpha = 0.12f),
                iconTint = Color(0xFFFFB300),
                onClick = { onNavigate("rating") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.Info,
                title = "À propos",
                subtitle = "LocAll v1.0.0 (Prototype)",
                containerColor = Color(0xFF4FC3F7).copy(alpha = 0.12f),
                iconTint = Color(0xFF4FC3F7),
                onClick = { onNavigate("about") }
            )

            Text(
                text = "Communauté & Avancé".uppercase(),
                color = Color.White.copy(alpha = 0.45f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp, top = 8.dp)
            )

            ProfileOptionRow(
                icon = Icons.Rounded.LocationCity,
                title = "Avis du Quartier",
                subtitle = "Consultez et partagez des avis",
                containerColor = Color(0xFFFFB300).copy(alpha = 0.12f),
                iconTint = Color(0xFFFFB300),
                onClick = { onNavigate("neighborhood_reviews") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.Gavel,
                title = "Litiges Communautaires",
                subtitle = "Signalez et votez sur les litiges",
                containerColor = Color(0xFFEF5350).copy(alpha = 0.12f),
                iconTint = Color(0xFFEF5350),
                onClick = { onNavigate("community_disputes") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.Receipt,
                title = "Reçus de Paiement",
                subtitle = "Consultez vos reçus et factures",
                containerColor = PrimaryGreen.copy(alpha = 0.12f),
                iconTint = PrimaryGreen,
                onClick = { onNavigate("payment_receipts") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.CalendarMonth,
                title = "Calendrier & Sync",
                subtitle = "Synchronisez avec Google Calendar",
                containerColor = Color(0xFF4FC3F7).copy(alpha = 0.12f),
                iconTint = Color(0xFF4FC3F7),
                onClick = { onNavigate("calendar_sync") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.AccountBalance,
                title = "Séquestre de Fonds",
                subtitle = "Gérez vos cautions et séquestres",
                containerColor = Color(0xFFFFB300).copy(alpha = 0.12f),
                iconTint = Color(0xFFFFB300),
                onClick = { onNavigate("escrow") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.NotificationsActive,
                title = "Paramètres de Notifications",
                subtitle = "Configurez vos alertes push",
                containerColor = Color(0xFFCE93D8).copy(alpha = 0.12f),
                iconTint = Color(0xFFCE93D8),
                onClick = { onNavigate("notification_settings") }
            )
            ProfileOptionRow(
                icon = Icons.Rounded.CardGiftcard,
                title = "Parrainage & Récompenses",
                subtitle = "Invitez des amis et gagnez",
                containerColor = PrimaryGreen.copy(alpha = 0.12f),
                iconTint = PrimaryGreen,
                onClick = { onNavigate("referral_tracking") }
            )

            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red.copy(alpha = 0.15f),
                    contentColor = Color.Red
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Se déconnecter", fontWeight = FontWeight.Bold)
            }

            if (showLogoutDialog) {
                ConfirmDialog(
                    title = "Se déconnecter",
                    message = "Êtes-vous sûr de vouloir vous déconnecter ?",
                    confirmText = "Déconnexion",
                    onConfirm = { viewModel.setLoggedIn(false) },
                    onDismiss = { showLogoutDialog = false },
                    isDestructive = true
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
    }
}

@Composable
fun ProfileOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    containerColor: Color = Color.White.copy(alpha = 0.05f),
    iconTint: Color = Color.LightGray,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SmoothIcon(
                icon = icon,
                backgroundColor = containerColor,
                tint = iconTint,
                size = 40.dp,
                iconSize = 20.dp,
                cornerRadius = 10.dp
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
            }

            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = Color.White.copy(alpha = 0.3f))
        }
    }
}

@Composable
fun EditProfileScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    val currentName by viewModel.userName.collectAsState()
    val currentPhone by viewModel.userPhone.collectAsState()
    val currentDob by viewModel.profileDob.collectAsState()
    val currentGender by viewModel.profileGender.collectAsState()
    val currentProfession by viewModel.profileProfession.collectAsState()
    val currentCity by viewModel.profileCity.collectAsState()

    var name by remember { mutableStateOf(currentName) }
    var phone by remember { mutableStateOf(currentPhone) }
    var email by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf(currentDob) }
    var gender by remember { mutableStateOf(currentGender) }
    var profession by remember { mutableStateOf(currentProfession) }
    var city by remember { mutableStateOf(currentCity) }

    BackHandler { onBack() }
    
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)
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
            Text("Modifier le profil", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text("NOM COMPLET", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.6f), letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF13EC5B),
                unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                focusedContainerColor = Color(0xFF162133),
                unfocusedContainerColor = Color(0xFF162133)
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("TÉLÉPHONE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.6f), letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF13EC5B),
                unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                focusedContainerColor = Color(0xFF162133),
                unfocusedContainerColor = Color(0xFF162133)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("EMAIL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.6f), letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF13EC5B),
                unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                focusedContainerColor = Color(0xFF162133),
                unfocusedContainerColor = Color(0xFF162133)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("DATE DE NAISSANCE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.6f), letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = dob,
            onValueChange = { dob = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            placeholder = { Text("JJ/MM/AAAA", color = Color.White.copy(alpha = 0.3f)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF13EC5B),
                unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                focusedContainerColor = Color(0xFF162133),
                unfocusedContainerColor = Color(0xFF162133)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("GENRE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.6f), letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Homme", "Femme", "Autre").forEach { option ->
                FilterChip(
                    selected = gender == option,
                    onClick = { gender = option },
                    label = { Text(option, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF13EC5B).copy(alpha = 0.2f),
                        selectedLabelColor = Color(0xFF13EC5B),
                        containerColor = Color(0xFF162133),
                        labelColor = Color.White.copy(alpha = 0.6f)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = Color.White.copy(alpha = 0.12f),
                        selectedBorderColor = Color(0xFF13EC5B).copy(alpha = 0.4f),
                        enabled = true,
                        selected = gender == option
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("PROFESSION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.6f), letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = profession,
            onValueChange = { profession = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF13EC5B),
                unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                focusedContainerColor = Color(0xFF162133),
                unfocusedContainerColor = Color(0xFF162133)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("VILLE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.6f), letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF13EC5B),
                unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                focusedContainerColor = Color(0xFF162133),
                unfocusedContainerColor = Color(0xFF162133)
            )
        )

        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                viewModel.updateProfileFull(name, phone, email, dob, gender, profession, city)
                onSave()
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF13EC5B), contentColor = Color(0xFF0B1526))
        ) {
            Text("Enregistrer", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ==================== PERSONAL STATS SCREEN ====================
@Composable
fun PersonalStatsScreen(viewModel: RentalViewModel, onBack: () -> Unit) {
    val bookings by viewModel.bookings.collectAsState()
    val paymentHistory by viewModel.paymentHistory.collectAsState()
    val bookmarkedItems by viewModel.bookmarkedItems.collectAsState()
    val userName by viewModel.userName.collectAsState()

    val totalBookings = bookings.size
    val totalSpent = paymentHistory.sumOf { it.amount }
    val avgRatingGiven = if (bookings.isNotEmpty()) bookings.map { 4.5f }.average().toFloat() else 0f
    val bookmarkedCount = bookmarkedItems.size
    val memberSince = "Janvier 2025"

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
            Text("Mes Statistiques", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Summary Stats Grid
        val stats = listOf(
            Triple(Icons.Rounded.BookOnline, "$totalBookings", "Réservations"),
            Triple(Icons.Rounded.Payments, formatPriceCfa(totalSpent), "Total dépensé"),
            Triple(Icons.Rounded.Star, String.format("%.1f", avgRatingGiven), "Note moyenne donnée"),
            Triple(Icons.Rounded.Favorite, "$bookmarkedCount", "Articles favoris"),
            Triple(Icons.Rounded.CalendarMonth, memberSince, "Membre depuis")
        )

        stats.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { (icon, value, label) ->
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PrimaryGreen.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                            }
                            Text(value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                            Text(label, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                        }
                    }
                }
                if (row.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Recent Bookings
        if (bookings.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Dernières réservations", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            bookings.take(3).forEach { booking ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
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
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.05f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Receipt, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(20.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(booking.rentalItemTitle, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(booking.status, color = if (booking.status == "Payé") PrimaryGreen else Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                        }
                        Text(formatPriceCfa(booking.totalPrice), color = PrimaryGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
