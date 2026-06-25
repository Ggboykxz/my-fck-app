package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.data.model.ReceivedReservation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip

import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.components.*
import com.example.ui.components.TrustScore
import com.example.ui.components.BadgeChip
import com.example.ui.components.PointsChip
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel
import java.text.NumberFormat
import java.util.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Internal data models for high-fidelity UI representation
data class NotificationItem(
    val id: Int,
    val type: String,
    val title: String,
    val description: String,
    val timestamp: String,
    val isRead: Boolean
)

val mockNotifications = listOf(
    NotificationItem(1, "reservation", "Réservation confirmée", "Votre réservation Toyota Hilux a été confirmée pour le 15 juillet", "Il y a 2h", false),
    NotificationItem(2, "message", "Nouveau message", "Kofi Mensah a envoyé un message concernant la Villa La Sablière", "Il y a 4h", false),
    NotificationItem(3, "payment", "Paiement reçu", "45 000 F CFA reçus pour la location Toyota Hilux - 3 jours", "Il y a 1 jour", true),
    NotificationItem(4, "system", "Mise à jour", "LocAll v1.0.1 est maintenant disponible avec de nouvelles fonctionnalités", "Il y a 2 jours", true),
    NotificationItem(5, "reservation", "Réservation annulée", "La réservation de Paul pour le Mitsubishi L200 a été annulée", "Il y a 3 jours", true),
    NotificationItem(6, "payment", "Remboursé", "15 000 F CFA remboursés - Annulation précoce Groupe Electrogène", "Il y a 5 jours", true),
    NotificationItem(7, "message", "Demande de réservation", "Sophie Nguema souhaite réserver l'Appartement Vue Mer du 20 au 25 juin", "Il y a 6 jours", false),
    NotificationItem(8, "system", "Vérification réussie", "Votre identité a été vérifiée avec succès. Badge Vérifié activé !", "Il y a 1 semaine", true),
    NotificationItem(9, "reservation", "Rappel de retour", "N'oubliez pas de retourner le Pack Sono Concert demain avant 18h", "Il y a 1 semaine", false),
    NotificationItem(10, "payment", "Point de fidélité", "Vous avez gagné 250 points pour votre dernière réservation !", "Il y a 2 semaines", true),
    NotificationItem(11, "promotion", "Offre spéciale", "Réduction de 15% sur toutes les locations Immobilier ce week-end !", "Il y a 2 semaines", true),
    NotificationItem(12, "system", "Sécurité", "Nouveau mot de passe configuré avec succès sur votre compte", "Il y a 3 semaines", true),
    NotificationItem(13, "reservation", "Modification acceptée", "Votre demande de changement de dates pour la Villa Sablière a été acceptée", "Il y a 3 semaines", true),
    NotificationItem(14, "message", "Relance propriétaire", "Marie-Claire vous a envoyé un rappel concernant la visite de l'appartement", "Il y a 1 mois", false),
    NotificationItem(15, "payment", "Facture disponible", "Votre facture pour la location Toyota Hilux est disponible en téléchargement", "Il y a 1 mois", true),
    NotificationItem(16, "system", "Nouvelle fonctionnalité", "Le chat vidéo est maintenant disponible pour les visites à distance !", "Il y a 1 mois", true),
    NotificationItem(17, "promotion", "Parrainage réussi", "Votre ami Rodrigue a rejoint LocAll avec votre code. +5 000 F CFA !", "Il y a 1 mois", true),
    NotificationItem(18, "reservation", "Réservation refusée", "La réservation de Jean pour le Van Hiace a été refusée (indisponible)", "Il y a 2 mois", true),
    NotificationItem(19, "alert", "Litige en cours", "Un litige a été ouvert pour la location Moto NMAX. Un médiateur suit votre dossier.", "Il y a 2 mois", false),
    NotificationItem(20, "payment", "Retrait effectué", "50 000 F CFA retirés de votre portefeuille via Airtel Money", "Il y a 2 mois", true)
)

data class DisputeItem(
    val id: String,
    val date: String,
    val type: String,
    val claimAmount: Int,
    val status: String, // "En cours", "Résolu", "Clos"
    val description: String,
    val decision: String? = null
)

data class EarnerTransaction(
    val id: String,
    val date: String,
    val ref: String,
    val amount: Int,
    val channel: String, // "Airtel Money", "Moov Money"
    val status: String // "Réussi", "Échoué"
)

@Composable
fun ProfileNavigator(viewModel: RentalViewModel) {
    var subScreen by remember { mutableStateOf("main") } // "main", "dashboard", "earnings", "wallet", "listings", "calendar", "bookings_received", "identity", "disputes", "tenant_bookings", "language", "security", "notifications", "help", "payment_methods", "damage", "review_tenant", "edit_profile", "about", "advanced_search", "settings", "invite_friend", "rating", "reservation_detail", "payment_history", "leaderboard", "achievements", "flash_offers", "loyalty_redeem", "rewards_coupons", "dispute", "insurance", "digital_deposit", "realtime_verification", "interactive_calendar"
    
    // Dispute state helpers
    var selectedDisputeId by remember { mutableStateOf<String?>(null) }
    var activeDisputeType by remember { mutableStateOf("Frais additionnels") }
    var activeDamageSelection by remember { mutableStateOf<ReceivedReservation?>(null) }
    var activeReviewSelection by remember { mutableStateOf<ReceivedReservation?>(null) }

    val isOwnerMode by viewModel.isOwnerMode.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissSnackbar()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BrandNavy)
        ) {
            AnimatedContent(
            targetState = subScreen,
            transitionSpec = {
                slideInHorizontally { width -> if (targetState == "main") -width else width } + fadeIn() togetherWith
                        slideOutHorizontally { width -> if (targetState == "main") width else -width } + fadeOut()
            },
            label = "ProfileSubscreenTransition"
        ) { screen ->
            when (screen) {
                "main" -> ProfileMainScreen(
                    viewModel = viewModel,
                    onNavigate = { dest -> subScreen = dest },
                    onEditProfile = { subScreen = "edit_profile" }
                )
                "edit_profile" -> EditProfileScreen(
                    viewModel = viewModel,
                    onBack = { subScreen = "main" },
                    onSave = { subScreen = "main" }
                )
                "dashboard" -> OwnerDashboardScreen(
                    viewModel = viewModel,
                    onBack = { subScreen = "main" },
                    onNavigate = { dest -> subScreen = dest }
                )
                "earnings" -> EarningsHistoryScreen(
                    viewModel = viewModel,
                    onBack = { subScreen = "dashboard" }
                )
                "wallet" -> WalletAndWithdrawalScreen(
                    viewModel = viewModel,
                    onBack = { subScreen = "dashboard" }
                )
                "listings" -> OwnerListingsScreen(
                    viewModel = viewModel,
                    onBack = { subScreen = "dashboard" }
                )
                "calendar" -> AvailabilityCalendarScreen(
                    viewModel = viewModel,
                    onBack = { subScreen = "dashboard" }
                )
                "bookings_received" -> ReceivedBookingsScreen(
                    viewModel = viewModel,
                    onBack = { subScreen = "dashboard" },
                    onReportDamage = { res -> 
                        activeDamageSelection = res
                        subScreen = "damage"
                    },
                    onReviewTenant = { res ->
                        activeReviewSelection = res
                        subScreen = "review_tenant"
                    }
                )
                "identity" -> IdentityVerificationScreen(
                    viewModel = viewModel,
                    onBack = { subScreen = "main" }
                )
                "disputes" -> DisputesHistoryScreen(
                    viewModel = viewModel,
                    onBack = { subScreen = "main" },
                    onSelectDispute = { id ->
                        selectedDisputeId = id
                        subScreen = "mediation"
                    }
                )
                "mediation" -> MediationDetailsScreen(
                    viewModel = viewModel,
                    disputeId = selectedDisputeId ?: "#LIT-8492",
                    onBack = { subScreen = "disputes" }
                )
                "tenant_bookings" -> TenantBookingsScreen(
                    viewModel = viewModel,
                    onBack = { subScreen = "main" },
                    onReportDamage = { res ->
                        activeDamageSelection = res
                        subScreen = "damage"
                    }
                )
                "language" -> LanguageSelectionScreen(
                    viewModel = viewModel,
                    onBack = { subScreen = "main" }
                )
                "security" -> SecuritySettingsScreen(
                    onBack = { subScreen = "main" }
                )
                "notifications" -> NotificationsScreen(
                    viewModel = viewModel,
                    onBack = { subScreen = "main" }
                )
                "help" -> HelpAndSupportScreen(
                    onBack = { subScreen = "main" }
                )
                "payment_methods" -> PaymentMethodsScreen(
                    onBack = { subScreen = "main" }
                )
                "damage" -> DamageReportingScreen(
                    reservation = activeDamageSelection,
                    onBack = { subScreen = if (isOwnerMode) "bookings_received" else "tenant_bookings" },
                    onSubmitted = {
                        subScreen = if (isOwnerMode) "bookings_received" else "tenant_bookings"
                    }
                )
                "review_tenant" -> TenantReviewScreen(
                    reservation = activeReviewSelection,
                    onBack = { subScreen = "bookings_received" },
                    onSubmitted = { subScreen = "bookings_received" }
                )
                "about" -> AboutScreen(
                    onBack = { subScreen = "main" }
                )
                "advanced_search" -> AdvancedSearchScreen(
                    viewModel = viewModel,
                    onBack = { subScreen = "main" }
                )
                "settings" -> SettingsScreen(
                    onBack = { subScreen = "main" }
                )
                "invite_friend" -> InviteFriendScreen(
                    viewModel = viewModel,
                    onBack = { subScreen = "main" }
                )
                "rating" -> RatingScreen(
                    rentalItemTitle = viewModel.selectedItem.value?.title ?: "Annonce",
                    onBack = { subScreen = "main" },
                    onSubmitted = { subScreen = "main" }
                )
                "reservation_detail" -> {
                    val booking = viewModel.bookings.collectAsState().value.firstOrNull()
                    if (booking != null) {
                        ReservationDetailScreen(
                            booking = booking,
                            onBack = { subScreen = "main" },
                            onCancel = { subScreen = "main" }
                        )
                    }
                }
                "payment_history" -> PaymentHistoryScreen(
                    viewModel = viewModel,
                    onBack = { subScreen = "main" }
                )
                "leaderboard" -> LeaderboardScreen(
                    onBack = { subScreen = "main" }
                )
                "achievements" -> AchievementsScreen(
                    onBack = { subScreen = "main" }
                )
                "flash_offers" -> FlashOffersScreen(
                    onBack = { subScreen = "main" }
                )
                "loyalty_redeem" -> LoyaltyRedeemScreen(
                    viewModel = viewModel,
                    onBack = { subScreen = "main" }
                )
                "rewards_coupons" -> RewardsCouponsScreen(
                    onBack = { subScreen = "main" }
                )
                "dispute" -> DisputeScreen(
                    onBack = { subScreen = "main" }
                )
                "insurance" -> InsuranceScreen(
                    onBack = { subScreen = "main" }
                )
                "digital_deposit" -> DigitalDepositScreen(
                    onBack = { subScreen = "main" }
                )
                "realtime_verification" -> RealTimeVerificationScreen(
                    onBack = { subScreen = "main" }
                )
                "interactive_calendar" -> InteractiveCalendarScreen(
                    onBack = { subScreen = "main" }
                )
            }
        }
    }
    }
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
    var showLogoutDialog by remember { mutableStateOf(false) }

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
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=250&q=80")
                                .crossfade(true)
                                .build(),
                            contentDescription = "User profile picture",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .border(2.dp, PrimaryGreen, CircleShape),
                            contentScale = ContentScale.Crop
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
                                "Marie-Claire Nzamba",
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

// ---------------- OWNER DASHBOARD SCREEN ----------------

@Composable
fun OwnerDashboardScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val balance by viewModel.withdrawableBalance.collectAsState()
    val isLoading by viewModel.isHomeLoading.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            // Navigation Back Header
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

                Text(
                    "Tableau Propriétaire",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
        if (isLoading) {
            items(3) { SkeletonBookingItem() }
        } else {
        item {

            // Wallet balance display card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0C2417)),
                border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "PORTEFEUILLE DISPONIBLE",
                        color = PrimaryGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )

                    Text(
                        formatPriceCfa(balance),
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onNavigate("wallet") },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Rounded.CallMade, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Retirer fds", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onNavigate("earnings") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f), contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Rounded.History, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Historique", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Stat columns / grid blocks
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatPillCard(
                    modifier = Modifier.weight(1f),
                    title = "Locations Actives",
                    value = "4 loués",
                    icon = Icons.Rounded.CheckCircle,
                    color = PrimaryGreen
                )
                StatPillCard(
                    modifier = Modifier.weight(1f),
                    title = "Annulation",
                    value = "0 %",
                    icon = Icons.Rounded.Close,
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Beautiful interactive Chart section
            Text(
                text = "Évolution des Revenus",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        
                        // Draw horizontal background lines
                        for (i in 1..4) {
                            val y = h * (i / 4f)
                            drawLine(
                                color = Color.White.copy(alpha = 0.04f),
                                start = Offset(0f, y),
                                end = Offset(w, y),
                                strokeWidth = 2f
                            )
                        }

                        // Coordinates for points representing monthly revenue
                        val points = listOf(
                            Offset(w * 0.05f, h * 0.85f), // Sep
                            Offset(w * 0.23f, h * 0.70f), // Oct
                            Offset(w * 0.41f, h * 0.75f), // Nov
                            Offset(w * 0.59f, h * 0.50f), // Dec
                            Offset(w * 0.77f, h * 0.30f), // Jan
                            Offset(w * 0.95f, h * 0.15f)  // Feb
                        )

                        // Draw path under line with gradient fill
                        val fillPath = Path().apply {
                            moveTo(points[0].x, h)
                            for (p in points) {
                                lineTo(p.x, p.y)
                            }
                            lineTo(points.last().x, h)
                            close()
                        }
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(PrimaryGreen.copy(alpha = 0.20f), Color.Transparent),
                                startY = 0f,
                                endY = h
                            )
                        )

                        // Draw line curve
                        val linePath = Path().apply {
                            moveTo(points[0].x, points[0].y)
                            for (i in 1 until points.size) {
                                val pPrev = points[i-1]
                                val pCurr = points[i]
                                cubicTo(
                                    (pPrev.x + pCurr.x)/2, pPrev.y,
                                    (pPrev.x + pCurr.x)/2, pCurr.y,
                                    pCurr.x, pCurr.y
                                )
                            }
                        }
                        drawPath(
                            path = linePath,
                            color = PrimaryGreen,
                            style = Stroke(width = 6f)
                        )

                        // Draw point coordinates
                        for (p in points) {
                            drawCircle(
                                color = BrandNavy,
                                radius = 10f,
                                center = p
                            )
                            drawCircle(
                                color = PrimaryGreen,
                                radius = 6f,
                                center = p
                            )
                        }
                    }

                    // Floating text overlay representing months
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Sep", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                        Text("Oct", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                        Text("Nov", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                        Text("Dec", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                        Text("Jan", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                        Text("Feb", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Pending Actions Notification Bubble
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("bookings_received") },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF381519)),
                border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Red),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.NotificationsActive, contentDescription = null, tint = Color.White)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("En attente de validation", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Vous avez 1 demande de location en attente.", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    }

                    Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = Color.White.copy(alpha = 0.4f))
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
        }
    }
}

@Composable
fun StatPillCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

// ---------------- EARNINGS HISTORY SCREEN ----------------

@Composable
fun EarningsHistoryScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val earnings by viewModel.earnings.collectAsState()

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
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Historique des Gains", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(earnings) { tx ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
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
                                    .clip(CircleShape)
                                    .background(Color(0xFF381519)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "E",
                                    color = PrimaryGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }

                            Column {
                                Text(tx.source, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(tx.date, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("+ ${formatPriceCfa(tx.amount)}", color = PrimaryGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Surface(
                                color = Color(0xFF0C2417),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    tx.status,
                                    color = PrimaryGreen,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------- WALLET & WITHDRAWAL SCREEN ----------------

@Composable
fun WalletAndWithdrawalScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val balance by viewModel.withdrawableBalance.collectAsState()
    var withdrawAmount by remember { mutableStateOf("") }
    var selectedChannel by remember { mutableStateOf("Airtel Money") }
    var numberInput by remember { mutableStateOf("") }
    var withdrawSuccess by remember { mutableStateOf(false) }
    var isLoadingWithdraw by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    if (withdrawSuccess) {
        Dialog(onDismissRequest = { withdrawSuccess = false }) {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE9F5EC)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.CloudDone, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(36.dp))
                    }

                    Text(
                        "Retrait Réussi !",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        "Votre demande de transfert de fonds a été exécutée avec succès vers votre portefeuille Mobile Money.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = { 
                            withdrawSuccess = false
                            onBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Fermer", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Retirer des Fonds", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Balance reminder
        Text("Solde Retirable", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(formatPriceCfa(balance), color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)

        Spacer(modifier = Modifier.height(24.dp))

        // Form Fields
        Text("Montant du Retrait (F CFA)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = withdrawAmount,
            onValueChange = { withdrawAmount = it },
            placeholder = { Text("Ex: 50000", color = Color.White.copy(alpha = 0.35f)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Choose Phone operators
        Text("Canal de Réception", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { selectedChannel = "Airtel Money" },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedChannel == "Airtel Money") Color(0xFF381519) else Color(0xFF162133)
                ),
                border = BorderStroke(
                    width = 2.dp,
                    color = if (selectedChannel == "Airtel Money") Color.Red else Color.Transparent
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color.Red))
                    Text("AirtelMoney", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { selectedChannel = "Moov Money" },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedChannel == "Moov Money") Color(0xFF0E2235) else Color(0xFF162133)
                ),
                border = BorderStroke(
                    width = 2.dp,
                    color = if (selectedChannel == "Moov Money") Color.Cyan else Color.Transparent
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color.Cyan))
                    Text("MoovMoney", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Phone Input
        Text("Numéro de Téléphone Gabonais", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = numberInput,
            onValueChange = { numberInput = it },
            placeholder = { Text("Ex: 077 12 34 56", color = Color.White.copy(alpha = 0.35f)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        val blockWithdraw = withdrawAmount.isBlank() || numberInput.isBlank() || isLoadingWithdraw
        Button(
            onClick = {
                val amtInt = withdrawAmount.toIntOrNull() ?: 0
                if (amtInt > balance) return@Button
                
                isLoadingWithdraw = true
                coroutineScope.launch {
                    delay(2000)
                    viewModel.withdrawFunds(amtInt)
                    isLoadingWithdraw = false
                    withdrawSuccess = true
                }
            },
            enabled = !blockWithdraw,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy, disabledContainerColor = Color.White.copy(alpha = 0.08f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (isLoadingWithdraw) {
                CircularProgressIndicator(color = BrandNavy, modifier = Modifier.size(24.dp))
            } else {
                Text("Lancer la demande de retrait", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

// ---------------- OWNER LISTINGS SCREEN ----------------

@Composable
fun OwnerListingsScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val listings by viewModel.rawRentalItems.collectAsState()

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
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Mes Annonces", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Separators Tab views (Active, En attente, Inactive)
        var selectedItemIndex by remember { mutableStateOf(0) }
        TabRow(
            selectedTabIndex = selectedItemIndex,
            containerColor = Color.Transparent,
            contentColor = PrimaryGreen,
            divider = {}
        ) {
            Tab(selected = selectedItemIndex == 0, onClick = { selectedItemIndex = 0 }, text = { Text("Actives (5)", fontWeight = FontWeight.Bold) })
            Tab(selected = selectedItemIndex == 1, onClick = { selectedItemIndex = 1 }, text = { Text("En révision (1)", fontWeight = FontWeight.Bold) })
            Tab(selected = selectedItemIndex == 2, onClick = { selectedItemIndex = 2 }, text = { Text("Suspendues (0)", fontWeight = FontWeight.Bold) })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display listings
        if (selectedItemIndex == 0) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                items(listings.take(5)) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(item.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("${item.neighborhood}, ${item.city}", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(formatPriceCfa(item.pricePerDay) + " / jour", color = PrimaryGreen, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Surface(color = Color(0xFF0C2417), shape = RoundedCornerShape(6.dp)) {
                                    Text("Actif", color = PrimaryGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                onClick = { },
                                color = Color(0xFF1A3324),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.Edit, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Modifier", color = PrimaryGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Surface(
                                onClick = { viewModel.deleteListing(item.id) },
                                color = Color.Red.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f))
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Supprimer", color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        } else if (selectedItemIndex == 1) {
            // Seeding review listing mock exactly
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://images.unsplash.com/photo-1549399542-7e3f8b79c341?auto=format&fit=crop&w=350&q=80")
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Mitsubishi L200 Pick-Up Double Cabine", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("Akanda, Gabon", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("45 000 F / jour", color = PrimaryGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Surface(color = Color(0xFF4A3515), shape = RoundedCornerShape(6.dp)) {
                        Text("Examen H24", color = Color(0xFFFFB300), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aucune annonce inactive.", color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp)
            }
        }
    }
}

// ---------------- AVAILABILITY CALENDAR SCREEN ----------------

@Composable
fun AvailabilityCalendarScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    var selectedDaysList by remember { mutableStateOf(setOf(4, 9, 10, 11, 19, 21)) } // dates toggled / blocked by Owner

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
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Calendrier Disponibilité", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Février 2026", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("Touchez un jour pour basculer son statut (Vert: Libre, Rouge: Bloqué / Reservé)", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, modifier = Modifier.padding(bottom = 16.dp))

        // Week Headers Row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            val weekDays = listOf("Lu", "Ma", "Me", "Je", "Ve", "Sa", "Di")
            for (day in weekDays) {
                Text(
                    text = day,
                    color = Color.White.copy(alpha = 0.45f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Month Grid days simulation
        val daysInMonth = 28
        val startOffset = 6 // Feb 2026 starts on Sunday
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            // empty slots for padding
            items(startOffset) {
                Box(modifier = Modifier.aspectRatio(1f))
            }

            items(daysInMonth) { index ->
                val day = index + 1
                val isBlocked = selectedDaysList.contains(day)
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isBlocked) Color(0xFF381519) else Color(0xFF0C2417))
                        .border(
                            width = 1.dp,
                            color = if (isBlocked) Color.Red.copy(alpha = 0.5f) else PrimaryGreen.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            selectedDaysList = if (isBlocked) {
                                selectedDaysList - day
                            } else {
                                selectedDaysList + day
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "$day",
                        color = if (isBlocked) Color.Red else PrimaryGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

// ---------------- RECEIVED BOOKINGS SCREEN (OWNER) ----------------

@Composable
fun ReceivedBookingsScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit,
    onReportDamage: (ReceivedReservation) -> Unit,
    onReviewTenant: (ReceivedReservation) -> Unit
) {
    var tabIndex by remember { mutableStateOf(0) }
    val receivedBookings by viewModel.receivedBookings.collectAsState()

    // Handover check modals
    var activeHandoverCheck by remember { mutableStateOf<ReceivedReservation?>(null) }
    var isHandoverConfirmed by remember { mutableStateOf(false) }
    var pendingRefuseReservation by remember { mutableStateOf<ReceivedReservation?>(null) }

    if (activeHandoverCheck != null) {
        Dialog(onDismissRequest = { activeHandoverCheck = null }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(Icons.Rounded.Handshake, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(48.dp))
                    
                    Text("Gestion de la Remise", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                    Text("Vous êtes sur le point de confirmer la remise du bien clé en main avec le locataire ${activeHandoverCheck?.tenantName}.", fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center)

                    Button(
                        onClick = {
                            val target = activeHandoverCheck
                            if (target != null) {
                                viewModel.acceptReceivedBooking(target.id)
                            }
                            activeHandoverCheck = null
                            isHandoverConfirmed = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirmer la remise des clés", color = Color.White)
                    }
                }
            }
        }
    }

    if (isHandoverConfirmed) {
        Dialog(onDismissRequest = { isHandoverConfirmed = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(Icons.Rounded.CloudDone, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(48.dp))
                    Text("Remise Validée !", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                    Text("Le statut de la réservation est désormais 'En Cours'. L'assurance LocAll couvre désormais les transactions.", fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    Button(onClick = { isHandoverConfirmed = false }, colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)) {
                        Text("Super", color = Color.White)
                    }
                }
            }
        }
    }

    if (pendingRefuseReservation != null) {
        ConfirmDialog(
            title = "Refuser la réservation",
            message = "Êtes-vous sûr de vouloir refuser cette réservation ?",
            confirmText = "Refuser",
            onConfirm = {
                if (pendingRefuseReservation != null) {
                    viewModel.refuseReceivedBooking(pendingRefuseReservation!!.id)
                }
                pendingRefuseReservation = null
            },
            onDismiss = { pendingRefuseReservation = null },
            isDestructive = true
        )
    }

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
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Réservations Reçues", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TabRow(
            selectedTabIndex = tabIndex,
            containerColor = Color.Transparent,
            contentColor = PrimaryGreen,
            divider = {}
        ) {
            Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("En attente", fontWeight = FontWeight.Bold) })
            Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("Confirmées", fontWeight = FontWeight.Bold) })
            Tab(selected = tabIndex == 2, onClick = { tabIndex = 2 }, text = { Text("Terminées", fontWeight = FontWeight.Bold) })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display reservations matching tab
        val statusFilter = when (tabIndex) {
            0 -> "En attente"
            1 -> "Confirmé"
            else -> "Terminé"
        }

        val filtered = receivedBookings.filter { it.status == statusFilter }

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aucune réservation dans cette catégorie.", color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filtered) { res ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Rounded.Person, contentDescription = null, tint = PrimaryGreen)
                                    Column {
                                        Text(res.tenantName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Rounded.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(11.dp))
                                            Text("${res.tenantRating}/5", color = Color.LightGray, fontSize = 10.sp)
                                        }
                                    }
                                }

                                Surface(
                                    color = if (res.status == "En attente") Color(0xFF4A3515) else Color(0xFF0C2417),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        res.status,
                                        color = if (res.status == "En attente") Color(0xFFFFB300) else PrimaryGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

                            Text(res.itemTitle, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(res.dates, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Durée", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                                Text("${res.days} Jours", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total à recevoir", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                                Text(formatPriceCfa(res.totalPrice), color = PrimaryGreen, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                            }

                            // Interactive Operations depending on state
                            Spacer(modifier = Modifier.height(6.dp))

                            if (res.status == "En attente") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.acceptReceivedBooking(res.id)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Accepter", fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            pendingRefuseReservation = res
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.15f), contentColor = Color.Red),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Refuser", fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else if (res.status == "Confirmé") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = { activeHandoverCheck = res },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f), contentColor = Color.White),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Gérer remise", fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.acceptReceivedBooking(res.id)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Clôturer", fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                // Completed actions
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = { onReviewTenant(res) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f), contentColor = Color.White),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Évaluer locataire", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = { onReportDamage(res) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.15f), contentColor = Color.Red),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Signaler dommage", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------- IDENTITY VERIFICATION SCREEN (TENANT) ----------------

@Composable
fun IdentityVerificationScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val currentStatus by viewModel.identityVerificationStatus.collectAsState()
    var selectedIdType by remember { mutableStateOf("CNI (Carte Nationale d'Identité)") }
    var inputDocNumber by remember { mutableStateOf("") }
    
    // Interactive Scanner States
    var showScanOverlay by remember { mutableStateOf(false) }
    var scanStep by remember { mutableStateOf("voyer") } // "voyer", "scanning", "ocr_reading", "scanned_success"
    var scanLaserOffset by remember { mutableStateOf(0f) }
    var documentPhotoTaken by remember { mutableStateOf(false) }
    
    // Interactive Biometric States
    var showSelfieGuide by remember { mutableStateOf(false) }
    var selfieInstruction by remember { mutableStateOf("Alignez votre visage") }
    var isSelfieTaking by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Scanning visual loop
    LaunchedEffect(showScanOverlay, scanStep) {
        if (showScanOverlay && scanStep == "scanning") {
            var goingDown = true
            while (scanStep == "scanning") {
                delay(16)
                if (goingDown) {
                    scanLaserOffset += 0.02f
                    if (scanLaserOffset >= 1f) goingDown = false
                } else {
                    scanLaserOffset -= 0.02f
                    if (scanLaserOffset <= 0f) goingDown = true
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Vérification d'Identité", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (currentStatus == "Non vérifié") {
            Text("Configurez vos documents officiels pour gagner en confiance auprès des annonceurs LocAll.", color = Color.White.copy(alpha = 0.65f), fontSize = 13.sp, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(20.dp))

            Text("Type de document", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            var isDocDropdownEx by remember { mutableStateOf(false) }
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isDocDropdownEx = !isDocDropdownEx },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))
                ) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(selectedIdType, color = Color.White, fontSize = 14.sp)
                        Icon(Icons.Rounded.ArrowDropDown, contentDescription = null, tint = Color.White)
                    }
                }
            }

            Text("Numéro du document", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                value = inputDocNumber,
                onValueChange = { inputDocNumber = it },
                placeholder = { Text("Ex: 104278429", color = Color.White.copy(alpha = 0.35f)) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Front Card Box photo Simulation container
            Text("Photo Recto du document", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(vertical = 8.dp)
                    .clickable {
                        showScanOverlay = true
                        scanStep = "scanning"
                        coroutineScope.launch {
                            delay(2500)
                            scanStep = "ocr_reading"
                            delay(2000)
                            scanStep = "scanned_success"
                            documentPhotoTaken = true
                        }
                    },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, if (documentPhotoTaken) PrimaryGreen else Color.White.copy(alpha = 0.10f))
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (documentPhotoTaken) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(28.dp))
                            }
                            Text("CNI de NGUEMA Pierre scannée avec succès !", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("ID: " + inputDocNumber.ifBlank { "CNI-84729" } + " | OCR Valide", color = PrimaryGreen, fontSize = 11.sp)
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Rounded.AddAPhoto, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(32.dp))
                            Text("Lancer le Scanner Intelligent LocAll (Vision AI)", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Reconnaissance OCR de la CNI / Passeport", color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { 
                    viewModel.setIdentityVerificationStatus("Documents soumis") 
                },
                enabled = inputDocNumber.isNotBlank() && documentPhotoTaken,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Valider l'étape du document", fontWeight = FontWeight.Bold)
            }
        } else if (currentStatus == "Documents soumis") {
            // STEP 2: Selfie Validation live capture simulated view
            Text("Vérification Faciale Selfie", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Cadrez votre visage à l'intérieur du cercle vert ci-dessous pour confirmer votre identité face à la fraude.", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 20.dp))

            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .border(4.dp, if (isSelfieTaking) PrimaryGreen else Color.White.copy(alpha = 0.3f), CircleShape)
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                // Show face outline / target circle mockup
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(Color.White.copy(alpha = 0.05f), radius = size.minDimension * 0.45f)
                    drawCircle(PrimaryGreen.copy(alpha = 0.08f), radius = size.minDimension * 0.4f)
                }

                if (isSelfieTaking) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(color = PrimaryGreen)
                        Text(selfieInstruction, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp))
                    }
                } else {
                    Icon(
                        Icons.Rounded.Face,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(120.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    isSelfieTaking = true
                    coroutineScope.launch {
                        selfieInstruction = "Alignement du visage..."
                        delay(1500)
                        selfieInstruction = "Veuillez cligner des yeux..."
                        delay(1500)
                        selfieInstruction = "Veuillez sourire doucement..."
                        delay(1500)
                        selfieInstruction = "Liveness 3D en cours..."
                        delay(1500)
                        isSelfieTaking = false
                        viewModel.setIdentityVerificationStatus("En révision")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Lancer la vérification biométrique", fontWeight = FontWeight.Bold)
            }
        } else if (currentStatus == "En révision") {
            // Step 3: Pending review status screen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4A3515)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Shield, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(40.dp))
                    }

                    Text("Dossier en cours d'examen", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Vos documents et votre selfie ont été soumis avec succès. Les modérateurs LocAll Gabon examinent actuellement votre demande. Temps de validation moyen : 12-24 heures.",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f), contentColor = Color.White)
                    ) {
                        Text("Découvrir l'application")
                    }

                    // DEMO ADMIN BYPASS BUTTON
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                            .border(1.dp, Color(0xFFFFB300).copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1508))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "FONCTIONALITÉ DE DÉMO LOCALL",
                                color = Color(0xFFFFB300),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                "En mode démo, vous pouvez forcer la validation automatique instantanée pour débloquer votre badge 'Vérifié'.",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = { viewModel.setIdentityVerificationStatus("Vérifié") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300), contentColor = Color.Black),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Rounded.VerifiedUser, contentDescription = null, tint = Color.Black)
                                    Text("Approuver instantanément", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // VERIFIED GREEN SCREEN
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(Icons.Rounded.Verified, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(80.dp))
                    Text("Compte Entièrement Vérifié !", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Vous avez validé toutes les étapes d'identité de haut niveau.", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp, textAlign = TextAlign.Center)
                    Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy)) {
                        Text("Retour", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
    }

    // HIGH FIDELITY SCANNER MODAL OVERLAY
    if (showScanOverlay) {
        Dialog(onDismissRequest = { showScanOverlay = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .border(2.dp, PrimaryGreen, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF090E17))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "SCANNER DE DOCUMENT LOCALL AI",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    // Viewfinder Mock
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.5f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        // Scanner layout viewfinder guides
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeW = 4.dp.toPx()
                            val lineL = 20.dp.toPx()
                            // Top Left Corner
                            drawPath(
                                path = Path().apply {
                                    moveTo(strokeW, strokeW + lineL)
                                    lineTo(strokeW, strokeW)
                                    lineTo(strokeW + lineL, strokeW)
                                },
                                color = PrimaryGreen,
                                style = Stroke(width = strokeW)
                            )
                            // Top Right Corner
                            drawPath(
                                path = Path().apply {
                                    moveTo(size.width - strokeW, strokeW + lineL)
                                    lineTo(size.width - strokeW, strokeW)
                                    lineTo(size.width - strokeW - lineL, strokeW)
                                },
                                color = PrimaryGreen,
                                style = Stroke(width = strokeW)
                            )
                            // Bottom Left Corner
                            drawPath(
                                path = Path().apply {
                                    moveTo(strokeW, size.height - strokeW - lineL)
                                    lineTo(strokeW, size.height - strokeW)
                                    lineTo(strokeW + lineL, size.height - strokeW)
                                },
                                color = PrimaryGreen,
                                style = Stroke(width = strokeW)
                            )
                            // Bottom Right Corner
                            drawPath(
                                path = Path().apply {
                                    moveTo(size.width - strokeW, size.height - strokeW - lineL)
                                    lineTo(size.width - strokeW, size.height - strokeW)
                                    lineTo(size.width - strokeW - lineL, size.height - strokeW)
                                },
                                color = PrimaryGreen,
                                style = Stroke(width = strokeW)
                            )

                            // Laser scanning bar
                            if (scanStep == "scanning") {
                                val laserY = size.height * scanLaserOffset
                                drawLine(
                                    color = PrimaryGreen,
                                    start = Offset(0f, laserY),
                                    end = Offset(size.width, laserY),
                                    strokeWidth = 3.dp.toPx()
                                )
                            }
                        }

                        // Status messages on scan screen
                        when (scanStep) {
                            "scanning" -> {
                                Text(
                                    "ANALYSE DE LA PIECE D'IDENTITE...",
                                    color = PrimaryGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            "ocr_reading" -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(color = PrimaryGreen, modifier = Modifier.size(28.dp))
                                    Text(
                                        "RECONNAISSANCE OPTIQUE (OCR)...",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            "scanned_success" -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Rounded.Check, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(36.dp))
                                    Text(
                                        "NOM: NGUEMA PIERRE",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "SCAN OK - INTÉGRITÉ CLÉ VALIDÉE",
                                        color = PrimaryGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Type: " + selectedIdType.split(" ").first(),
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 11.sp
                        )

                        if (scanStep == "scanned_success") {
                            Button(
                                onClick = { showScanOverlay = false },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Terminer le scanner", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        } else {
                            Text(
                                "Veuillez ne pas bouger...",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------- DISPUTES & MEDIATION GRAPHICS ----------------

@Composable
fun DisputesHistoryScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit,
    onSelectDispute: (String) -> Unit
) {
    var disputeFormEx by remember { mutableStateOf(false) }
    var inputDesc by remember { mutableStateOf("") }
    var trackingCodeSubmit by remember { mutableStateOf<String?>(null) }

    val disputes by viewModel.disputes.collectAsState()

    if (disputeFormEx) {
        Dialog(onDismissRequest = { disputeFormEx = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Ouvrir un Litige", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandNavy)

                    Text("Raison du litige", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                    var selectedReason by remember { mutableStateOf("Bien endommagé") }
                    val reasons = listOf("Bien endommagé", "Clés non remises", "Nettoyage insuffisant", "Autre")
                    reasons.forEach { rs ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { selectedReason = rs }) {
                            RadioButton(selected = selectedReason == rs, onClick = { selectedReason = rs })
                            Text(rs, color = Color.DarkGray)
                        }
                    }

                    Text("Description détaillée", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                    OutlinedTextField(
                        value = inputDesc,
                        onValueChange = { inputDesc = it },
                        placeholder = { Text("Décrivez précisément le litige...") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        colors = OutlinedTextFieldDefaults.colors()
                    )

                    Button(
                        onClick = {
                            disputeFormEx = false
                            trackingCodeSubmit = "#LIT-" + (1000..9999).random()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Soumettre le dossier", color = Color.White)
                    }
                }
            }
        }
    }

    if (trackingCodeSubmit != null) {
        Dialog(onDismissRequest = { trackingCodeSubmit = null }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(52.dp))
                    Text("Litige Soumis !", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                    Text("Votre dossier a été enregistré sous le numéro de suivi ${trackingCodeSubmit}. Un médiateur LocAll va se mettre en relation avec vous sous 4 heures.", fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    Button(onClick = { trackingCodeSubmit = null }, colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)) {
                        Text("Entendu", color = Color.White)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
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

            Text("Mes Litiges", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            IconButton(
                onClick = { disputeFormEx = true },
                modifier = Modifier
                    .size(40.dp)
                    .background(PrimaryGreen.copy(alpha = 0.15f), CircleShape)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Créer litige", tint = PrimaryGreen)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(disputes) { disp ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectDispute(disp.id) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(disp.id, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Surface(
                                color = if (disp.status == "En cours") Color(0xFF4A3515) else Color(0xFF0C2417),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    disp.status,
                                    color = if (disp.status == "En cours") Color(0xFFFFB300) else PrimaryGreen,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

                        Text(disp.type, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(disp.description, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Indemnité réclamée", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                            Text(formatPriceCfa(disp.claimAmount), color = PrimaryGreen, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }
}

// ---------------- MEDIATION RECIPIENTS TIMELINES ----------------

@Composable
fun MediationDetailsScreen(
    viewModel: RentalViewModel,
    disputeId: String,
    onBack: () -> Unit
) {
    val messages by viewModel.mediationMessages.collectAsState()
    var chatHistory by remember { mutableStateOf(messages.map { Pair(it.sender, it.message) }) }
    var replyText by remember { mutableStateOf("") }
    var isMediatorWriting by remember { mutableStateOf(false) }

    // Amicable Settlement States
    var selectedAgreementValue by remember { mutableStateOf(25000f) }
    var isSettledSuccess by remember { mutableStateOf(false) }
    var isSettlementProposed by remember { mutableStateOf(false) }
    var timelineStep3Completed by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    fun sendReply(text: String) {
        if (text.isBlank()) return
        chatHistory = chatHistory + Pair("Moi", text)
        replyText = ""
        isMediatorWriting = true

        coroutineScope.launch {
            delay(1500)
            isMediatorWriting = false
            
            val responseText = when {
                text.contains("facture", ignoreCase = true) || text.contains("prix", ignoreCase = true) -> {
                    "Merci pour l'envoi de la facture de chez SOGAFRIC. L'expert évalue la dépréciation restante à 35 000 F CFA. Êtes-vous disposé à proposer un règlement à l'amiable via le widget ci-dessous ?"
                }
                text.contains("accord", ignoreCase = true) || text.contains("amiable", ignoreCase = true) || text.contains("d'accord", ignoreCase = true) -> {
                    "Parfait. Veuillez ajuster le curseur de proposition de règlement à l'amiable ci-dessous sur le montant qui vous convient pour clore la médiation."
                }
                else -> {
                    "J'ai pris note de vos remarques. Nous attendons le retour de la partie adverse. En attendant, proposer un règlement amiable reste le moyen le plus rapide de débloquer la caution en séquestre."
                }
            }
            chatHistory = chatHistory + Pair("Médiateur", responseText)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Dossier $disputeId", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("État d'avancement de la Médiation", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                
                TimelineStep(title = "Dossier enregistré", desc = "06 Fév - Preuves reçues", isCompleted = true)
                TimelineStep(title = "Analyse de la l'assurance", desc = "Conformité du contrat de bail validée", isCompleted = true)
                TimelineStep(
                    title = if (isSettledSuccess) "Médiation résolue à l'amiable !" else "Décision de l'expert en cours",
                    desc = if (isSettledSuccess) "Accord mutuel enregistré par LocAll" else "En attente du rapport technique GabAsur ou accord",
                    isCompleted = timelineStep3Completed
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Simulated chat box with LocAll Support Team
        Text("Conversation avec le Médiateur", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    val listState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(listState)
                    ) {
                        chatHistory.forEach { msg ->
                            DisputeBubble(sender = msg.first, text = msg.second)
                        }
                        
                        if (isMediatorWriting) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                CircularProgressIndicator(color = PrimaryGreen, modifier = Modifier.size(12.dp), strokeWidth = 2.dp)
                                Text("L'arbitre analyse...", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                            }
                        }
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        placeholder = { Text("Écrire au médiateur LocAll...", color = Color.White.copy(alpha = 0.3f), fontSize = 12.sp) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
                        ),
                        singleLine = true
                    )
                    IconButton(
                        onClick = { sendReply(replyText) },
                        enabled = replyText.isNotBlank(),
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (replyText.isNotBlank()) PrimaryGreen else Color.White.copy(alpha = 0.05f),
                                RoundedCornerShape(10.dp)
                            )
                    ) {
                        Icon(
                            Icons.Rounded.Send,
                            contentDescription = "Envoyer",
                            tint = if (replyText.isNotBlank()) BrandNavy else Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // INTERACTIVE AMICABLE SETTLEMENT PANEL
        Text("Règlement Amiable d'Arbitrage (Optionnel)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A2A)),
            border = BorderStroke(1.dp, if (isSettledSuccess) PrimaryGreen else Color.White.copy(alpha = 0.08f))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isSettledSuccess) {
                    Icon(Icons.Rounded.Handshake, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(48.dp))
                    Text(
                        "ACCORD GAGNANT-GAGNANT CONCLU !",
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        "Vous avez convenu d'un dédommagement mutuel de " + formatPriceCfa(selectedAgreementValue.toInt()) + " F CFA. Le reliquat de caution sera débloqué et transféré immédiatement sur vos comptes Airtel Money.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                } else {
                    Text(
                        "En proposant un accord, vous fixez une somme compensatoire à l'amiable. Si la partie accepte, la caution est reversée au prorata instantanément.",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        "Votre proposition : " + formatPriceCfa(selectedAgreementValue.toInt()),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Slider(
                        value = selectedAgreementValue,
                        onValueChange = { if (!isSettlementProposed) selectedAgreementValue = it },
                        valueRange = 5000f..50000f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = PrimaryGreen,
                            activeTrackColor = PrimaryGreen,
                            inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            isSettlementProposed = true
                            chatHistory = chatHistory + Pair("Moi", "Je propose de régler le litige à l'amiable pour un montant forfaitaire de " + formatPriceCfa(selectedAgreementValue.toInt()) + ".")
                            coroutineScope.launch {
                                delay(2000)
                                chatHistory = chatHistory + Pair("Médiateur", "La proposition de " + formatPriceCfa(selectedAgreementValue.toInt()) + " a été acceptée par la partie adverse ! Clôture de l'incident.")
                                delay(1000)
                                isSettledSuccess = true
                                timelineStep3Completed = true
                            }
                        },
                        enabled = !isSettlementProposed,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSettlementProposed) Color.Gray else PrimaryGreen,
                            contentColor = BrandNavy
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (isSettlementProposed) "Attente d'acceptation adverse..." else "Soumettre l'offre amiable",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DisputeBubble(sender: String, text: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()) {
        Text(sender, fontSize = 10.sp, color = PrimaryGreen, fontWeight = FontWeight.Bold)
        Surface(color = Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(8.dp)) {
            Text(text, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun TimelineStep(title: String, desc: String, isCompleted: Boolean) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(if (isCompleted) PrimaryGreen else Color.Gray)
        )
        Column {
            Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(desc, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

// ---------------- TENANT BOOKINGS SCREEN ----------------

@Composable
fun TenantBookingsScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit,
    onReportDamage: (ReceivedReservation) -> Unit
) {
    val bookings by viewModel.bookings.collectAsState()

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
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Mes Réservations", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (bookings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(imageVector = Icons.Rounded.Task, contentDescription = null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(48.dp))
                    Text("Aucune réservation à venir pour l'instant.", color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.weight(1f)) {
                items(bookings) { b ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("#RES-${b.id}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Surface(color = Color(0xFF0C2417), shape = RoundedCornerShape(8.dp)) {
                                    Text(b.status, color = PrimaryGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                                }
                            }

                            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

                            Text(b.rentalItemTitle, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Durée: ${b.days} Jours", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("Montant Payé", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                                    Text(formatPriceCfa(b.totalPrice), color = PrimaryGreen, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                                }

                                Button(
                                    onClick = {
                                        onReportDamage(
                                            ReceivedReservation(
                                                id = "#RES-${b.id}",
                                                tenantName = "Moi",
                                                tenantRating = 5f,
                                                itemTitle = b.rentalItemTitle,
                                                category = b.rentalItemCategory,
                                                status = b.status,
                                                dates = "Aujourd'hui",
                                                days = b.days,
                                                totalPrice = b.totalPrice,
                                                phone = b.paymentPhone
                                            )
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.15f), contentColor = Color.Red),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Signaler un problème", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------- MOYENS DE PAIEMENT & SECURITY & OTHER FORMS ----------------

@Composable
fun LanguageSelectionScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val languages = listOf("Français", "English", "Fang (Gabon)", "Yipunu (Gabon)")
    val currentLang by viewModel.profileLanguage.collectAsState()

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
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Sélection de la Langue", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        languages.forEach { lng ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { viewModel.setProfileLanguage(lng) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, if (currentLang == lng) PrimaryGreen.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(lng, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    if (currentLang == lng) {
                        Icon(Icons.Rounded.Check, contentDescription = null, tint = PrimaryGreen)
                    }
                }
            }
        }
    }
}

// ---------------- NOTIFICATIONS SCREEN ----------------

@Composable
fun NotificationsScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount = notifications.count { !it.isRead }

    fun markAsRead(id: Int) {
        // Notifications are managed via ViewModel
    }

    fun markAllAsRead() {
        // Notifications are managed via ViewModel
    }

    fun notificationIcon(type: String) = when (type) {
        "reservation" -> Icons.Rounded.EventAvailable
        "message" -> Icons.Rounded.Mail
        "payment" -> Icons.Rounded.Payments
        "system" -> Icons.Rounded.Info
        else -> Icons.Rounded.Notifications
    }

    fun notificationColor(type: String) = when (type) {
        "reservation" -> PrimaryGreen
        "message" -> Color(0xFF4FC3F7)
        "payment" -> Color(0xFFFFB300)
        "system" -> Color(0xFFCE93D8)
        else -> Color.White
    }

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
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Notifications", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        "Aucune notification",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Vous serez notifié des nouvelles activités",
                        color = Color.White.copy(alpha = 0.35f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            if (unreadCount > 0) {
                Surface(
                    onClick = { markAllAsRead() },
                    color = PrimaryGreen.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.25f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Rounded.DoneAll, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                        Text(
                            "Tout marquer comme lu",
                            color = PrimaryGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Surface(
                            color = PrimaryGreen,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                "$unreadCount",
                                color = BrandNavy,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(notifications) { notif ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { markAsRead(notif.id) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (notif.isRead) Color(0xFF162133) else Color(0xFF1A2740)
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (notif.isRead) Color.White.copy(alpha = 0.05f) else notificationColor(notif.type).copy(alpha = 0.25f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (notif.isRead) Color.White.copy(alpha = 0.05f)
                                        else notificationColor(notif.type).copy(alpha = 0.15f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = notificationIcon(notif.type),
                                    contentDescription = null,
                                    tint = if (notif.isRead) Color.White.copy(alpha = 0.4f) else notificationColor(notif.type),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        notif.title,
                                        color = if (notif.isRead) Color.White.copy(alpha = 0.7f) else Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = if (notif.isRead) FontWeight.Medium else FontWeight.Bold
                                    )
                                    if (!notif.isRead) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(PrimaryGreen)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    notif.message,
                                    color = if (notif.isRead) Color.White.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.65f),
                                    fontSize = 12.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    notif.time,
                                    color = if (notif.isRead) Color.White.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.4f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun SecuritySettingsScreen(
    onBack: () -> Unit
) {
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    if (isSuccess) {
        Dialog(onDismissRequest = { isSuccess = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Rounded.CloudDone, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(48.dp))
                    Text("Mot de passe mis à jour !", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                    Text("Votre mot de passe de connexion confidentiel a été mis à jour avec succès.", fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    Button(onClick = { isSuccess = false; onBack() }, colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)) {
                        Text("Super", color = Color.White)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Sécurité du Compte", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Changer le mot de passe confidentiel", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = oldPass,
            onValueChange = { oldPass = it },
            visualTransformation = PasswordVisualTransformation(),
            placeholder = { Text("Ancien mot de passe", color = Color.White.copy(alpha = 0.35f)) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen, unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedTextColor = Color.White, unfocusedTextColor = Color.White
            )
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = newPass,
            onValueChange = { newPass = it },
            visualTransformation = PasswordVisualTransformation(),
            placeholder = { Text("Nouveau mot de passe", color = Color.White.copy(alpha = 0.35f)) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen, unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedTextColor = Color.White, unfocusedTextColor = Color.White
            )
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = confirmPass,
            onValueChange = { confirmPass = it },
            visualTransformation = PasswordVisualTransformation(),
            placeholder = { Text("Confirmer le nouveau mot de passe", color = Color.White.copy(alpha = 0.35f)) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen, unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedTextColor = Color.White, unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { isSuccess = true },
            enabled = oldPass.isNotBlank() && newPass.isNotBlank() && newPass == confirmPass,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Changer mon mot de passe", fontWeight = FontWeight.Bold)
        }
    }
}

data class SupportMessage(
    val sender: String, // "Bot" or "User"
    val text: String,
    val time: String
)

@Composable
fun HelpAndSupportScreen(
    onBack: () -> Unit
) {
    val faqs = listOf(
        Pair("Comment signaler un problème ?", "Si vous rencontrez un dysfonctionnement avec la location, un bouton 'Signaler' vous permet d'ouvrir un litige avec des preuves photos sous 24h."),
        Pair("Fonctionnement de Mobile Money", "Les paiements s'effectuent par validation USSD (sms de push direct de Airtel Money ou Moov Money). Les fonds restent sous séquestre jusqu'à la remise du bien."),
        Pair("Garantie de Protection LocAll", "LocAll Gabon couvre les sinistres et dégradations matérielles jusqu'à hauteur de 5,000,000 F CFA grâce à nos partenaires de réassurance basés à Libreville."),
        Pair("Conditions d'annulation", "L'annulation est gratuite jusqu'à 24h avant le début planifié de la remise des clés. Passé ce délai, des frais de dédommagement de 35% s'appliquent.")
    )

    var showChatbot by remember { mutableStateOf(false) }
    var chatMessages by remember {
        mutableStateOf(
            listOf(
                SupportMessage(
                    sender = "Bot",
                    text = "Bonjour ! Je suis Kassa, votre conseiller virtuel LocAll Gabon 🇬🇦. Comment puis-je vous aider dans vos locations de matériel aujourd'hui ?",
                    time = "À l'instant"
                )
            )
        )
    }
    var chatbotInput by remember { mutableStateOf("") }
    var isBotTyping by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val quickQuestions = listOf(
        "Délai versement ?",
        "Comment marche la caution ?",
        "Est-ce sécurisé ?",
        "Parler à un humain"
    )

    fun handleSend(messageText: String) {
        if (messageText.isBlank()) return
        
        // Add User Message
        val newMsg = SupportMessage(sender = "User", text = messageText, time = "Maintenant")
        chatMessages = chatMessages + newMsg
        chatbotInput = ""
        isBotTyping = true

        coroutineScope.launch {
            delay(1500)
            isBotTyping = false
            
            val responseText = when {
                messageText.contains("versement", ignoreCase = true) || messageText.contains("retrait", ignoreCase = true) || messageText.contains("payout", ignoreCase = true) -> {
                    "Pour les propriétaires, le transfert vers votre compte Airtel Money ou Moov Money s'effectue sous 2 à 4 heures ouvrées dès la validation de la remise des clés par le locataire."
                }
                messageText.contains("caution", ignoreCase = true) || messageText.contains("séquestre", ignoreCase = true) || messageText.contains("garantie", ignoreCase = true) -> {
                    "Le dépôt de garantie (caution) est bloqué en toute sécurité par LocAll Gabon. Il n'est reversé au propriétaire qu'en cas de dommage avéré constaté dans les 24h suivant le retour."
                }
                messageText.contains("sécur", ignoreCase = true) || messageText.contains("fiable", ignoreCase = true) -> {
                    "Absolument ! Tous nos utilisateurs passent par une vérification biométrique instantanée (CNI et Selfie 3D). De plus, nos baux de location sont conformes au droit OHADA en vigueur au Gabon."
                }
                messageText.contains("humain", ignoreCase = true) || messageText.contains("agent", ignoreCase = true) || messageText.contains("téléphone", ignoreCase = true) -> {
                    "Je viens de notifier un de nos conseillers humains de notre bureau de Libreville (Alibandeng). Un agent va prendre le relais ici. Vous pouvez aussi nous appeler direct au +241 07 12 34 56."
                }
                else -> {
                    "C'est bien noté ! Pour toute question spécifique à une annonce, vous pouvez lancer un chat direct avec le propriétaire depuis les détails de l'annonce d'intérêt."
                }
            }
            chatMessages = chatMessages + SupportMessage(sender = "Bot", text = responseText, time = "Maintenant")
        }
    }

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
            IconButton(
                onClick = {
                    if (showChatbot) showChatbot = false else onBack()
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = if (showChatbot) "Discuter avec l'Assistant LocAll" else "Centre d'Aide & FAQ",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (!showChatbot) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Questions Fréquentes (FAQ)",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                faqs.forEach { f ->
                    var expanded by remember { mutableStateOf(false) }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { expanded = !expanded },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(f.first, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                    contentDescription = null,
                                    tint = PrimaryGreen
                                )
                            }
                            if (expanded) {
                                Text(f.second, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, lineHeight = 18.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A2A)),
                    border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(PrimaryGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.SupportAgent, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(32.dp))
                        }

                        Text("Toujours besoin de réponses ?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            "Notre assistant client virtuel répond en direct à toutes vos interrogations techniques ou juridiques.",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Button(
                            onClick = { showChatbot = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Rounded.Forum, contentDescription = null)
                                Text("Lancer le Chatbot en direct", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            // HIGH-FIDELITY INTERACTIVE SUPPORT CHAT WINDOW
            Column(modifier = Modifier.weight(1f)) {
                // Chats listing flow
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(chatMessages) { msg ->
                            val isBot = msg.sender == "Bot"
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isBot) Arrangement.Start else Arrangement.End
                            ) {
                                Box(
                                    modifier = Modifier
                                        .widthIn(max = 280.dp)
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 16.dp,
                                                topEnd = 16.dp,
                                                bottomStart = if (isBot) 4.dp else 16.dp,
                                                bottomEnd = if (isBot) 16.dp else 4.dp
                                            )
                                        )
                                        .background(
                                            if (isBot) Color(0xFF162133) else PrimaryGreen
                                        )
                                        .padding(14.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = msg.text,
                                            color = if (isBot) Color.White else BrandNavy,
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            Text(
                                                text = msg.time,
                                                color = if (isBot) Color.White.copy(alpha = 0.4f) else BrandNavy.copy(alpha = 0.6f),
                                                fontSize = 9.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (isBotTyping) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Card(
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                                        modifier = Modifier.padding(start = 4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(
                                                color = PrimaryGreen,
                                                modifier = Modifier.size(14.dp),
                                                strokeWidth = 2.dp
                                            )
                                            Text("Kassa écrit...", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Quick Inquiry Chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(quickQuestions) { q ->
                        LocalInquiryChip(text = q, onClick = { handleSend(q) })
                    }
                }

                // Chat text input box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = chatbotInput,
                        onValueChange = { chatbotInput = it },
                        placeholder = { Text("Écrire à Kassa...", color = Color.White.copy(alpha = 0.3f), fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("support_chat_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f)
                        ),
                        singleLine = true
                    )

                    IconButton(
                        onClick = { handleSend(chatbotInput) },
                        enabled = chatbotInput.isNotBlank(),
                        modifier = Modifier
                            .size(52.dp)
                            .background(
                                if (chatbotInput.isNotBlank()) PrimaryGreen else Color.White.copy(alpha = 0.08f),
                                RoundedCornerShape(14.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Send,
                            contentDescription = "Envoyer",
                            tint = if (chatbotInput.isNotBlank()) BrandNavy else Color.White.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LocalInquiryChip(text: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.White.copy(alpha = 0.06f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.height(34.dp)
    ) {
        Box(modifier = Modifier.padding(horizontal = 14.dp), contentAlignment = Alignment.Center) {
            Text(text, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// ---------------- DAMAGE REPORTING FORM ----------------

@Composable
fun DamageReportingScreen(
    reservation: ReceivedReservation?,
    onBack: () -> Unit,
    onSubmitted: () -> Unit
) {
    var detailsInput by remember { mutableStateOf("") }
    var compensValue by remember { mutableStateOf("") }
    var photoTaken by remember { mutableStateOf(false) }
    var isSubmittedSuccess by remember { mutableStateOf(false) }

    if (isSubmittedSuccess) {
        Dialog(onDismissRequest = { isSubmittedSuccess = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Rounded.CloudDone, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(52.dp))
                    Text("Problème Soumis !", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                    Text("Votre rapport de dommage a été transmis au département de médiation de LocAll Gabon. Nous étudierons les preuves fournies.", fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    Button(onClick = { isSubmittedSuccess = false; onSubmitted() }, colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)) {
                        Text("Entendu", color = Color.White)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Signaler un Problème", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Référence Réservation: ${reservation?.id ?: "#RES-XXXX"}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
        Text(reservation?.itemTitle ?: "Bien loué", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        Text("Description des anomalies contractées", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            value = detailsInput,
            onValueChange = { detailsInput = it },
            placeholder = { Text("Détaillez précisément les rayures, pannes, ou bris rencontrés...", color = Color.White.copy(alpha = 0.3f)) },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen, unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedTextColor = Color.White, unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text("Demande d'Indemnisation Souhaitée (F CFA)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            value = compensValue,
            onValueChange = { compensValue = it },
            placeholder = { Text("Ex: 50000", color = Color.White.copy(alpha = 0.3f)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen, unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedTextColor = Color.White, unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text("Preuves Photo", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .padding(vertical = 10.dp)
                .clickable { photoTaken = true },
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.10f))
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (photoTaken) {
                    Icon(Icons.Rounded.LinkedCamera, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(36.dp))
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Rounded.AddAPhoto, contentDescription = null, tint = PrimaryGreen)
                        Text("Prendre une photo du sinistre", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { isSubmittedSuccess = true },
            enabled = detailsInput.isNotBlank() && compensValue.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Soumettre le litige à LocAll", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}

// ---------------- TENANT EVALUATION SCREEN ----------------

@Composable
fun TenantReviewScreen(
    reservation: ReceivedReservation?,
    onBack: () -> Unit,
    onSubmitted: () -> Unit
) {
    var noteStars by remember { mutableStateOf(5) }
    var reviewTextInput by remember { mutableStateOf("") }
    var isSubmittedSuccess by remember { mutableStateOf(false) }

    if (isSubmittedSuccess) {
        Dialog(onDismissRequest = { isSubmittedSuccess = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Rounded.SentimentSatisfiedAlt, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(52.dp))
                    Text("Avis publié !", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                    Text("Votre recommandation a été enregistrée sur le profil de ${reservation?.tenantName}.", fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    Button(onClick = { isSubmittedSuccess = false; onSubmitted() }, colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)) {
                        Text("Terminer", color = Color.White)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Évaluer le Locataire", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Tenant description
        Text("Évaluer l'expérience avec ${reservation?.tenantName}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text("Sélectionnez votre note générale", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, modifier = Modifier.padding(bottom = 16.dp))

        // Interactive Stars
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            for (i in 1..5) {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = null,
                    tint = if (i <= noteStars) Color(0xFFFFB300) else Color.White.copy(alpha = 0.2f),
                    modifier = Modifier
                        .size(44.dp)
                        .clickable { noteStars = i }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Laissez un commentaire sur la ponctualité & le respect du bien", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            value = reviewTextInput,
            onValueChange = { reviewTextInput = it },
            placeholder = { Text("Ex: Locataire très ponctuel et arrangeant, bien restitué dans un état impeccable. Je recommande vivement !", color = Color.White.copy(alpha = 0.3f)) },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen, unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedTextColor = Color.White, unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { isSubmittedSuccess = true },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Publier l'évaluation", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun EditProfileScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    var name by remember { mutableStateOf("Jean Dupont") }
    var phone by remember { mutableStateOf("+241 77 12 34 56") }
    
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
        
        Text("NOM", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.6f), letterSpacing = 1.sp)
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
                viewModel.updateUserProfile(name, phone)
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

// ---------------- ABOUT SCREEN ----------------

@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    var showCguDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showLicensesDialog by remember { mutableStateOf(false) }

    if (showCguDialog) {
        Dialog(onDismissRequest = { showCguDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Conditions Générales d'Utilisation", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                    Text("Dernière mise à jour : Février 2026", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        "Les présentes Conditions Générales d'Utilisation (CGU) régissent l'accès et l'utilisation de l'application LocAll, éditée par LocAll Gabon SARL, dont le siège social est situé à Libreville, Gabon.\n\n" +
                        "En utilisant LocAll, vous acceptez sans réserve les dispositions des présentes CGU. LocAll met en relation des particuliers pour la location de biens entre eux sur le territoire gabonais.\n\n" +
                        "1. Objet : LocAll est une plateforme de mise en relation entre locataires et propriétaires. LocAll n'est pas partie au contrat de location conclu entre les utilisateurs.\n\n" +
                        "2. Inscription : L'inscription est ouverte à toute personne physique majeure résidant au Gabon. L'identification par CNI et selfie biométrique est obligatoire.\n\n" +
                        "3. Obligations : Les utilisateurs s'engagent à fournir des informations exactes et à respecter les biens loués. Tout manquement pourra entraîner la suspension du compte.\n\n" +
                        "4. Paiements : Les transactions s'effectuent via Mobile Money (Airtel Money, Moov Money) sous séquestre sécurisé LocAll.",
                        color = Color.DarkGray,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                    Button(onClick = { showCguDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = BrandNavy), modifier = Modifier.fillMaxWidth()) {
                        Text("Fermer", color = Color.White)
                    }
                }
            }
        }
    }

    if (showPrivacyDialog) {
        Dialog(onDismissRequest = { showPrivacyDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Politique de Confidentialité", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                    Text("Dernière mise à jour : Février 2026", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        "LocAll Gabon SARL attache une importance particulière à la protection de vos données personnelles. Cette politique de confidentialité décrit comment nous collectons, utilisons et protégeons vos informations.\n\n" +
                        "1. Données collectées : Nom, numéro de téléphone, photographie de profil, pièce d'identité (CNI/Passeport), selfie biométrique, données de géolocalisation, historique de transactions.\n\n" +
                        "2. Finalités : Vérification d'identité, mise en relation entre utilisateurs, traitement des paiements, prévention de la fraude, amélioration du service.\n\n" +
                        "3. Partage : Vos données ne sont jamais vendues à des tiers. Elles peuvent être partagées avec nos partenaires de paiement (Airtel Money, Moov Money) et d'assurance (GabAsur) uniquement dans le cadre des transactions.\n\n" +
                        "4. Sécurité : Toutes les données sont chiffrées (AES-256) et stockées sur des serveurs sécurisés conformes aux normes ISO 27001.\n\n" +
                        "5. Droits : Conformément à la loi gabonaise sur la protection des données, vous disposez d'un droit d'accès, de rectification et de suppression de vos données.",
                        color = Color.DarkGray,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                    Button(onClick = { showPrivacyDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = BrandNavy), modifier = Modifier.fillMaxWidth()) {
                        Text("Fermer", color = Color.White)
                    }
                }
            }
        }
    }

    if (showLicensesDialog) {
        Dialog(onDismissRequest = { showLicensesDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Licences Open-Source", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                    Text("LocAll utilise les bibliothèques open-source suivantes :", fontSize = 12.sp, color = Color.Gray)

                    val licenses = listOf(
                        Triple("Jetpack Compose", "Apache License 2.0", "Google"),
                        Triple("Kotlin", "Apache License 2.0", "JetBrains"),
                        Triple("Room Database", "Apache License 2.0", "Google"),
                        Triple("Coil", "Apache License 2.0", "Coil Contributors"),
                        Triple("Material Icons Extended", "Apache License 2.0", "Google"),
                        Triple("Navigation Compose", "Apache License 2.0", "Google"),
                        Triple("ViewModel Compose", "Apache License 2.0", "Google"),
                        Triple("Coroutines", "Apache License 2.0", "JetBrains")
                    )

                    licenses.forEach { (name, license, author) ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                                Text("$license · $author", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }

                    Button(onClick = { showLicensesDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = BrandNavy), modifier = Modifier.fillMaxWidth()) {
                        Text("Fermer", color = Color.White)
                    }
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.08f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("À propos", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Logo Section
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(PrimaryGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "LocAll Logo",
                    tint = PrimaryGreen,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                "LocAll",
                color = PrimaryGreen,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                "v1.0.0 (Prototype)",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Description
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Description", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "LocAll est une application de location entre particuliers conçue pour le marché gabonais. Louez tout, partout au Gabon.",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Credits
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Crédits", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Développé avec ❤️ au Gabon",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                    Text(
                        "Technologies: Kotlin, Jetpack Compose, Room DB",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Legal Links
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCguDialog = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Rounded.Gavel, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                            Text("Conditions Générales d'Utilisation", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = Color.White.copy(alpha = 0.3f))
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showPrivacyDialog = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Rounded.Shield, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                            Text("Politique de Confidentialité", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = Color.White.copy(alpha = 0.3f))
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLicensesDialog = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Rounded.Code, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                            Text("Licences open-source", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = Color.White.copy(alpha = 0.3f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Social / Contact
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Suivez-nous", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Instagram
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF1A1A2E).copy(alpha = 0.8f))
                                    .clickable { },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.CameraAlt, contentDescription = "Instagram", tint = Color(0xFFE1306C), modifier = Modifier.size(22.dp))
                            }
                            Text("Instagram", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                        }

                        // Twitter / X
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF1A1A2E).copy(alpha = 0.8f))
                                    .clickable { },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.Tag, contentDescription = "Twitter", tint = Color(0xFF1DA1F2), modifier = Modifier.size(22.dp))
                            }
                            Text("Twitter", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                        }

                        // Facebook
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF1A1A2E).copy(alpha = 0.8f))
                                    .clickable { },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.Public, contentDescription = "Facebook", tint = Color(0xFF1877F2), modifier = Modifier.size(22.dp))
                            }
                            Text("Facebook", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Copyright
            Text(
                "© 2026 LocAll. Tous droits réservés.",
                color = Color.White.copy(alpha = 0.3f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp)
            )
        }
    }
}

// ---------------- PAYMENT METHODS SCREEN ----------------

@Composable
fun PaymentMethodsScreen(
    onBack: () -> Unit
) {
    var airtelIsDefault by remember { mutableStateOf(true) }
    var moovIsDefault by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Moyens de Paiement", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Airtel Money Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
            border = BorderStroke(
                width = 2.dp,
                color = if (airtelIsDefault) BrandAirtel.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.05f)
            )
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF381519)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = BrandAirtel, modifier = Modifier.size(22.dp))
                        }

                        Column {
                            Text("Airtel Money", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(maskPhoneNumber("+241 07 45 81 29 5"), color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp)
                        }
                    }

                    Surface(
                        color = if (airtelIsDefault) Color(0xFF0C2417) else Color(0xFF4A3515),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (airtelIsDefault) "Principal" else "Secondaire",
                            color = if (airtelIsDefault) PrimaryGreen else Color(0xFFFFB300),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.06f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!airtelIsDefault) {
                        Surface(
                            onClick = {
                                airtelIsDefault = true
                                moovIsDefault = false
                            },
                            color = PrimaryGreen.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Rounded.Star, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(14.dp))
                                Text("Définir par défaut", color = PrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                            Text("Par défaut", color = PrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Moov Money Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
            border = BorderStroke(
                width = 2.dp,
                color = if (moovIsDefault) BrandMoov.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.05f)
            )
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0E2235)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = BrandMoov, modifier = Modifier.size(22.dp))
                        }

                        Column {
                            Text("Moov Money", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(maskPhoneNumber("+241 06 64 59 12 3"), color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp)
                        }
                    }

                    Surface(
                        color = if (moovIsDefault) Color(0xFF0C2417) else Color(0xFF4A3515),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (moovIsDefault) "Principal" else "Secondaire",
                            color = if (moovIsDefault) PrimaryGreen else Color(0xFFFFB300),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.06f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!moovIsDefault) {
                        Surface(
                            onClick = {
                                moovIsDefault = true
                                airtelIsDefault = false
                            },
                            color = PrimaryGreen.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Rounded.Star, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(14.dp))
                                Text("Définir par défaut", color = PrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                            Text("Par défaut", color = PrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Add payment method button
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 0.08f),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ajouter un moyen de paiement", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Fees info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A2A)),
            border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Rounded.Info, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                Column {
                    Text("Frais de transaction", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("2.5%", color = PrimaryGreen, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Appliqué sur chaque transaction via Mobile Money", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

// ==================== ADVANCED SEARCH SCREEN ====================
@Composable
fun AdvancedSearchScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Tous") }
    var selectedCity by remember { mutableStateOf("Tous") }
    var maxPrice by remember { mutableIntStateOf(0) }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var maxDistance by remember { mutableFloatStateOf(50f) }
    val items by viewModel.filteredRentalItems.collectAsState()

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
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Recherche Avancée", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it; viewModel.setSearchQuery(it) },
            placeholder = { Text("Rechercher...", color = Color.White.copy(alpha = 0.3f)) },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.5f)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                focusedBorderColor = PrimaryGreen, unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                focusedContainerColor = Color(0xFF162133), unfocusedContainerColor = Color(0xFF162133)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("CATÉGORIE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Tous", "Immobilier", "Véhicules", "Équipements").forEach { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    onClick = { selectedCategory = cat; viewModel.setSelectedCategory(cat) },
                    color = if (isSelected) PrimaryGreen.copy(alpha = 0.15f) else Color(0xFF162133),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (isSelected) PrimaryGreen else Color.White.copy(alpha = 0.08f))
                ) {
                    Text(cat, color = if (isSelected) PrimaryGreen else Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("VILLE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Tous", "Libreville", "Port-Gentil", "Franceville").forEach { city ->
                val isSelected = selectedCity == city
                Surface(
                    onClick = { selectedCity = city; viewModel.setSelectedCity(city) },
                    color = if (isSelected) PrimaryGreen.copy(alpha = 0.15f) else Color(0xFF162133),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (isSelected) PrimaryGreen else Color.White.copy(alpha = 0.08f))
                ) {
                    Text(city, color = if (isSelected) PrimaryGreen else Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("PRIX MAX: ${formatPriceCfa(maxPrice)}/jour", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
        Slider(
            value = maxPrice.toFloat(),
            onValueChange = { maxPrice = it.toInt() },
            onValueChangeFinished = { viewModel.setSelectedMaxPrice(maxPrice) },
            valueRange = 0f..250000f,
            colors = SliderDefaults.colors(
                thumbColor = PrimaryGreen,
                activeTrackColor = PrimaryGreen,
                inactiveTrackColor = Color.White.copy(alpha = 0.1f)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("DATES DE DISPONIBILITÉ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = startDate,
                onValueChange = { startDate = it; viewModel.setStartDate(it.ifEmpty { null }) },
                placeholder = { Text("Début (ex: 15/07)", color = Color.White.copy(alpha = 0.3f), fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    focusedBorderColor = PrimaryGreen, unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                    focusedContainerColor = Color(0xFF162133), unfocusedContainerColor = Color(0xFF162133)
                )
            )
            OutlinedTextField(
                value = endDate,
                onValueChange = { endDate = it; viewModel.setEndDate(it.ifEmpty { null }) },
                placeholder = { Text("Fin (ex: 20/07)", color = Color.White.copy(alpha = 0.3f), fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    focusedBorderColor = PrimaryGreen, unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                    focusedContainerColor = Color(0xFF162133), unfocusedContainerColor = Color(0xFF162133)
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("DISTANCE MAX: ${maxDistance.toInt()} km", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
        Slider(
            value = maxDistance,
            onValueChange = { maxDistance = it },
            onValueChangeFinished = { viewModel.setMaxDistance(maxDistance) },
            valueRange = 1f..100f,
            colors = SliderDefaults.colors(
                thumbColor = PrimaryGreen,
                activeTrackColor = PrimaryGreen,
                inactiveTrackColor = Color.White.copy(alpha = 0.1f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("${items.size} résultat(s) trouvé(s)", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(items) { item ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current).data(item.imageUrl).crossfade(true).build(),
                            contentDescription = item.title,
                            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("${item.neighborhood}, ${item.city}", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                            Text(formatPriceCfa(item.pricePerDay) + " / jour", color = PrimaryGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ==================== SETTINGS SCREEN ====================
@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(true) }
    var locationEnabled by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = Color(0xFF162133),
            title = { Text("Supprimer mon compte ?", color = Color.White) },
            text = { Text("Cette action est irréversible. Toutes vos données seront supprimées définitivement.", color = Color.White.copy(alpha = 0.7f)) },
            confirmButton = {
                Button(onClick = { showDeleteDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350))) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Annuler", color = Color.White.copy(alpha = 0.6f))
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.08f), CircleShape)) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Paramètres", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SmoothIcon(icon = Icons.Rounded.Notifications, tint = Color(0xFFFFB300), backgroundColor = Color(0xFFFFB300).copy(alpha = 0.12f))
                    Column { Text("Notifications", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold); Text("Alertes push et emails", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp) }
                }
                Switch(checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it }, colors = SwitchDefaults.colors(checkedThumbColor = BrandNavy, checkedTrackColor = PrimaryGreen, uncheckedTrackColor = Color.White.copy(alpha = 0.15f)))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SmoothIcon(icon = Icons.Rounded.DarkMode, tint = Color(0xFF4FC3F7), backgroundColor = Color(0xFF4FC3F7).copy(alpha = 0.12f))
                    Column { Text("Mode Sombre", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold); Text("Thème sombre de l'application", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp) }
                }
                Switch(checked = darkMode, onCheckedChange = { darkMode = it }, colors = SwitchDefaults.colors(checkedThumbColor = BrandNavy, checkedTrackColor = PrimaryGreen, uncheckedTrackColor = Color.White.copy(alpha = 0.15f)))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SmoothIcon(icon = Icons.Rounded.LocationOn, tint = PrimaryGreen, backgroundColor = PrimaryGreen.copy(alpha = 0.12f))
                    Column { Text("Géolocalisation", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold); Text("Autoriser l'accès à votre position", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp) }
                }
                Switch(checked = locationEnabled, onCheckedChange = { locationEnabled = it }, colors = SwitchDefaults.colors(checkedThumbColor = BrandNavy, checkedTrackColor = PrimaryGreen, uncheckedTrackColor = Color.White.copy(alpha = 0.15f)))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("COMPTE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)

        Spacer(modifier = Modifier.height(12.dp))

        listOf(
            Triple(Icons.Rounded.Password, "Changer le mot de passe", "Dernière modification il y a 3 mois"),
            Triple(Icons.Rounded.Delete, "Supprimer mon compte", "Action irréversible"),
            Triple(Icons.Rounded.Description, "Conditions Générales", "CGU v1.0"),
            Triple(Icons.Rounded.Shield, "Politique de Confidentialité", "RGPD")
        ).forEach { (icon, title, subtitle) ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable {
                if (title == "Supprimer mon compte") showDeleteDialog = true
            }, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(icon, contentDescription = null, tint = if (title == "Supprimer mon compte") Color(0xFFEF5350) else Color.White.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
                    Column(modifier = Modifier.weight(1f)) { Text(title, color = if (title == "Supprimer mon compte") Color(0xFFEF5350) else Color.White, fontSize = 14.sp); Text(subtitle, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp) }
                    Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = Color.White.copy(alpha = 0.3f))
                }
            }
        }
    }
}

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
            Icon(Icons.Rounded.CardGiftcard, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(48.dp))
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
                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(72.dp))
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
                onClick = { if (rating > 0) { submitted = true; onSubmitted() } },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (rating > 0) PrimaryGreen else Color.White.copy(alpha = 0.1f), contentColor = BrandNavy),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Publier l'avis", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==================== RESERVATION DETAIL SCREEN ====================
@Composable
fun ReservationDetailScreen(
    booking: com.example.data.model.Booking,
    onBack: () -> Unit,
    onCancel: () -> Unit
) {
    var showCancelDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.08f), CircleShape)) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Détails Réservation", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        val statusColor = when (booking.status) {
            "Payé" -> PrimaryGreen
            "Confirmé" -> Color(0xFF4FC3F7)
            "En attente" -> Color(0xFFFFB300)
            "Annulé" -> Color.Red
            "Terminé" -> Color(0xFF9E9E9E)
            else -> Color.Gray
        }

        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.1f)), border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Rounded.Receipt, contentDescription = null, tint = statusColor, modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(booking.status, color = statusColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Réservation #${booking.id}", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DetailRow("Annonce", booking.rentalItemTitle)
                DetailRow("Catégorie", booking.rentalItemCategory)
                DetailRow("Prix / jour", formatPriceCfa(booking.pricePerDay))
                DetailRow("Nombre de jours", "${booking.days} jour(s)")
                HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                DetailRow("Total payé", formatPriceCfa(booking.totalPrice))
                DetailRow("Mode de paiement", booking.paymentMethod)
                DetailRow("Téléphone", maskPhoneNumber(booking.paymentPhone))
                DetailRow("Date", java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.FRANCE).format(java.util.Date(booking.bookingTimestamp)))
            }
        }

        if (booking.status != "Annulé" && booking.status != "Terminé") {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { showCancelDialog = true },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.15f), contentColor = Color.Red),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Rounded.Cancel, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Annuler la réservation", fontWeight = FontWeight.Bold)
            }
        }

        if (showCancelDialog) {
            ConfirmDialog(
                title = "Annuler la réservation",
                message = "Êtes-vous sûr de vouloir annuler cette réservation ? Cette action est irréversible.",
                confirmText = "Annuler la réservation",
                onConfirm = { showCancelDialog = false; onCancel() },
                onDismiss = { showCancelDialog = false },
                isDestructive = true
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

// ==================== PAYMENT HISTORY SCREEN ====================
@Composable
fun PaymentHistoryScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val payments by viewModel.paymentHistory.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.08f), CircleShape)) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Historique des paiements", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.1f)), border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("TOTAL DÉPENSÉ", color = PrimaryGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text(formatPriceCfa(485000), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Text("sur 4 transactions", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(payments) { payment ->
                Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.05f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.Receipt, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(payment.description, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(payment.date, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                        }
                        StatusBadge(text = payment.method, color = PrimaryGreen)
                    }
                }
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
            items(leaderboardData.size) { index ->
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
                            Text("⭐", fontSize = 12.sp)
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
    onBack: () -> Unit
) {
    data class Achievement(val title: String, val desc: String, val unlocked: Boolean, val icon: ImageVector)
    val achievements = listOf(
        Achievement("Premier Pas", "Créez votre premier compte", true, Icons.Rounded.PersonAdd),
        Achievement("Première Location", "Effectuez votre première réservation", true, Icons.Rounded.CarRental),
        Achievement("Propriétaire Actif", "Publiez 3 annonces minimum", true, Icons.Rounded.Home),
        Achievement("Groupe Social", "Invitez 5 amis via le parrainage", false, Icons.Rounded.Group),
        Achievement("Fidélité", "Cumulez 10 réservations", false, Icons.Rounded.EmojiEvents),
        Achievement("Confiance Verte", "Obtenez la vérification d'identité", true, Icons.Rounded.Verified),
        Achievement("Super Hôte", "Maintenez une note >= 4.8 sur 10 avis", false, Icons.Rounded.Star),
        Achievement("Réactif", "Répondez en moins de 1h pendant 30 jours", false, Icons.Rounded.Timer),
        Achievement("Événementier", "Louez du matériel événementiel 5 fois", false, Icons.Rounded.Celebration),
        Achievement("Explorateur", "Louez dans 3 villes différentes", false, Icons.Rounded.Explore)
    )
    val unlocked = achievements.count { it.unlocked }

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
                Text("$unlocked / ${achievements.size}", color = PrimaryGreen, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Text("succès débloqués", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { unlocked.toFloat() / achievements.size },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = PrimaryGreen,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(achievements) { achievement ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (achievement.unlocked) PrimaryGreen.copy(alpha = 0.08f) else Color(0xFF162133)
                    ),
                    border = BorderStroke(1.dp, if (achievement.unlocked) PrimaryGreen.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.08f))
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(if (achievement.unlocked) PrimaryGreen.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.06f)), contentAlignment = Alignment.Center) {
                            Icon(achievement.icon, contentDescription = null, tint = if (achievement.unlocked) PrimaryGreen else Color.White.copy(alpha = 0.3f), modifier = Modifier.size(24.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(achievement.title, color = if (achievement.unlocked) Color.White else Color.White.copy(alpha = 0.5f), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(achievement.desc, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                        }
                        if (achievement.unlocked) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(22.dp))
                        } else {
                            Icon(Icons.Rounded.Lock, contentDescription = null, tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(20.dp))
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
    onBack: () -> Unit
) {
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
            items(flashOffers) { (title, discount, timer) ->
                Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)), border = BorderStroke(1.dp, Color(0xFFFF6F00).copy(alpha = 0.2f))) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFFF6F00).copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                            Text(discount, color = Color(0xFFFF6F00), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(timer, color = Color(0xFFFF6F00), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6F00), contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Voir", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
    val points by viewModel.referralEarnings.collectAsState()
    val rewards = listOf(
        Triple("Réduction 5 000 F", "5 000 points", Icons.Rounded.Discount),
        Triple("Location gratuite 1 jour", "15 000 points", Icons.Rounded.CardGiftcard),
        Triple("Upgrade véhicule", "10 000 points", Icons.Rounded.Upgrade),
        Triple("Assurance offerte", "20 000 points", Icons.Rounded.Shield),
        Triple("Cashback 10 000 F", "25 000 points", Icons.Rounded.Payments)
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
                Text("⭐", fontSize = 32.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("${points / 1000} 000", color = PrimaryGreen, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Text("points disponibles", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("RÉCOMPENSES DISPONIBLES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(rewards) { (title, cost, icon) ->
                val canAfford = points >= 5000
                Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(PrimaryGreen.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                            Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(24.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(cost, color = Color(0xFFFFB300), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = { viewModel.showSnackbar("Points échangés avec succès !") },
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
    onBack: () -> Unit
) {
    val coupons = listOf(
        Triple("BIENVENUE10", "10% sur votre 1ère location", "Valide jusqu'au 31/12/2026"),
        Triple("ÉTÉ2026", "15% sur les réservations > 3 jours", "Valide jusqu'au 30/09/2026"),
        Triple("PARRAINAGE", "5 000 F CFA de crédit", "Valide après 1ère utilisation")
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

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(coupons) { (code, description, expiry) ->
                Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)), border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.2f))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Rounded.LocalOffer, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                            Text(code, color = PrimaryGreen, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(description, color = Color.White, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(expiry, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// ==================== DISPUTE SCREEN ====================
@Composable
fun DisputeScreen(
    onBack: () -> Unit
) {
    var disputeType by remember { mutableStateOf("Dommage") }
    var description by remember { mutableStateOf("") }
    var showSent by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.08f), CircleShape)) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Signaler un Litige", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Icon(Icons.Rounded.Gavel, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Text("Décrivez votre problème", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text("Un médiateur LocAll examinera votre dossier sous 24-48h.", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(24.dp))

        Text("TYPE DE LITIGE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Dommage", "Retard", "Annulation", "Litige financier").forEach { type ->
                val isSelected = disputeType == type
                Surface(
                    onClick = { disputeType = type },
                    color = if (isSelected) PrimaryGreen.copy(alpha = 0.15f) else Color(0xFF162133),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (isSelected) PrimaryGreen else Color.White.copy(alpha = 0.08f))
                ) {
                    Text(type, color = if (isSelected) PrimaryGreen else Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            placeholder = { Text("Décrivez le problème en détail...", color = Color.White.copy(alpha = 0.3f)) },
            modifier = Modifier.fillMaxWidth().height(140.dp),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                focusedBorderColor = PrimaryGreen, unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                focusedContainerColor = Color(0xFF162133), unfocusedContainerColor = Color(0xFF162133)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { showSent = true },
            enabled = description.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Icon(Icons.Rounded.Send, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Envoyer la demande", fontWeight = FontWeight.Bold)
        }

        if (showSent) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.1f)), border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(28.dp))
                    Column {
                        Text("Demande envoyée !", color = PrimaryGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Un médiateur examinera votre dossier sous 24-48h", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// ==================== INSURANCE OPTIONS SCREEN ====================
@Composable
fun InsuranceScreen(
    onBack: () -> Unit
) {
    var selectedPlan by remember { mutableStateOf("basic") }
    var showSubscribed by remember { mutableStateOf(false) }
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
            onClick = { showSubscribed = true },
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

// ==================== REAL-TIME VERIFICATION SCREEN ====================
@Composable
fun RealTimeVerificationScreen(
    onBack: () -> Unit
) {
    val verificationSteps = listOf(
        Triple("Identité vérifiée", "CNI scannée et validée par OCR", true),
        Triple("Selfie validé", "Correspondance faciale confirmée", true),
        Triple("Adresse confirmée", "Justificatif de domicile vérifié", false),
        Triple("Téléphone vérifié", "Code SMS reçu et validé", true)
    )

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.08f), CircleShape)) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Vérification Temps Réel", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.08f)), border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Rounded.Verified, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Profil 75% vérifié", color = PrimaryGreen, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { 0.75f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = PrimaryGreen,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("ÉTAPES DE VÉRIFICATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(12.dp))

        verificationSteps.forEach { (title, desc, isVerified) ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = if (isVerified) PrimaryGreen.copy(alpha = 0.06f) else Color(0xFF162133))
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(if (isVerified) PrimaryGreen.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.06f)), contentAlignment = Alignment.Center) {
                        Icon(
                            if (isVerified) Icons.Rounded.CheckCircle else Icons.Rounded.Schedule,
                            contentDescription = null,
                            tint = if (isVerified) PrimaryGreen else Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(desc, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                    }
                    Text(
                        if (isVerified) "Vérifié" else "En attente",
                        color = if (isVerified) PrimaryGreen else Color(0xFFFFB300),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Rounded.Info, contentDescription = null, tint = Color(0xFF4FC3F7), modifier = Modifier.size(20.dp))
                Text("La vérification complète débloque le badge Vérifié et augmente votre confiance de 40%.", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
            }
        }
    }
}

// ==================== INTERACTIVE CALENDAR SCREEN ====================
@Composable
fun InteractiveCalendarScreen(
    onBack: () -> Unit
) {
    val bookedDates = listOf(5, 6, 7, 12, 13, 19, 20, 21, 27, 28)
    val availableDates = listOf(1, 2, 3, 4, 8, 9, 10, 11, 14, 15, 16, 17, 18, 22, 23, 24, 25, 26, 29, 30, 31)
    var selectedDate by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.08f), CircleShape)) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Calendrier", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Juillet 2026", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    listOf("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim").forEach { day ->
                        Text(day, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Calendar grid - 5 rows
                val firstDayOffset = 2 // July 2026 starts on Wednesday
                val totalDays = 31
                var dayCounter = 1
                repeat(5) { week ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        repeat(7) { dayOfWeek ->
                            val cellIndex = week * 7 + dayOfWeek
                            val dayNum = cellIndex - firstDayOffset + 1
                            if (dayNum in 1..totalDays) {
                                val isBooked = dayNum in bookedDates
                                val isSelected = dayNum == selectedDate
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isSelected -> PrimaryGreen
                                                isBooked -> Color(0xFFEF5350).copy(alpha = 0.2f)
                                                else -> Color.Transparent
                                            }
                                        )
                                        .clickable(enabled = !isBooked) { selectedDate = dayNum },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "$dayNum",
                                        color = when {
                                            isSelected -> BrandNavy
                                            isBooked -> Color(0xFFEF5350).copy(alpha = 0.5f)
                                            else -> Color.White
                                        },
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.size(38.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(PrimaryGreen))
                Text("Sélectionné", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color(0xFFEF5350).copy(alpha = 0.2f)))
                Text("Réservé", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(modifier = Modifier.size(12.dp).clip(CircleShape).border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape))
                Text("Disponible", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            }
        }

        if (selectedDate > 0) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.08f)), border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Rounded.EventAvailable, contentDescription = null, tint = PrimaryGreen)
                    Column {
                        Text("Le $selectedDate juillet 2026 est disponible", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Cliquez pour réserver cette date", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
