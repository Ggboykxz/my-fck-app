package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.LocalContext
import com.example.ui.components.*
import com.example.ui.components.SmoothIcon
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel

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
        viewModel.markNotificationRead(id)
    }

    fun markAllAsRead() {
        viewModel.markAllNotificationsRead()
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
                        contentDescription = "Aucune notification",
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
                items(notifications, key = { it.id }) { notif ->
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
                    Icon(Icons.Rounded.CloudDone, contentDescription = "Mot de passe mis à jour", tint = PrimaryGreen, modifier = Modifier.size(48.dp))
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

// ==================== SETTINGS SCREEN ====================
@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var notificationsEnabled by remember { mutableStateOf(true) }
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
                Switch(checked = isDarkMode, onCheckedChange = { isDarkMode = it; DarkModeHelper.saveDarkMode(context, it) }, colors = SwitchDefaults.colors(checkedThumbColor = BrandNavy, checkedTrackColor = PrimaryGreen, uncheckedTrackColor = Color.White.copy(alpha = 0.15f)))
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
                Icon(Icons.Rounded.Info, contentDescription = "Information", tint = PrimaryGreen, modifier = Modifier.size(20.dp))
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
            items(payments, key = { it.id }) { payment ->
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
