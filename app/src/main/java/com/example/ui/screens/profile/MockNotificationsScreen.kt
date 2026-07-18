package com.example.ui.screens.profile

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notifications.NotificationHelper
import com.example.ui.components.SmoothIcon
import com.example.ui.theme.*

data class MockNotificationEntry(
    val id: Int,
    val title: String,
    val message: String,
    val channel: String,
    val timestamp: String
)

@Composable
fun MockNotificationsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var bookingsEnabled by remember { mutableStateOf(true) }
    var messagesEnabled by remember { mutableStateOf(true) }
    var promosEnabled by remember { mutableStateOf(true) }
    var systemEnabled by remember { mutableStateOf(true) }
    var history by remember { mutableStateOf(listOf<MockNotificationEntry>()) }
    var nextId by remember { mutableIntStateOf(1) }

    fun addHistory(title: String, message: String, channel: String) {
        val now = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        history = listOf(MockNotificationEntry(nextId++, title, message, channel, now)) + history
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
            Text("Notifications Push (Démo)", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text("ENVOYER UNE NOTIFICATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
            }

            item {
                MockNotifButton(
                    icon = Icons.Rounded.EventAvailable,
                    label = "Réservation confirmée",
                    subtitle = "Notification de réservation",
                    color = PrimaryGreen,
                    enabled = bookingsEnabled,
                    onClick = {
                        NotificationHelper.showMockBookingNotification(context)
                        addHistory("Réservation confirmée", "Votre appartement a été réservé pour le 15 mars", NotificationHelper.CHANNEL_BOOKINGS)
                    }
                )
            }

            item {
                MockNotifButton(
                    icon = Icons.Rounded.Mail,
                    label = "Nouveau message",
                    subtitle = "Notification de message",
                    color = Color(0xFF4FC3F7),
                    enabled = messagesEnabled,
                    onClick = {
                        NotificationHelper.showMockMessageNotification(context)
                        addHistory("Nouveau message", "Marie vous a envoyé un message", NotificationHelper.CHANNEL_MESSAGES)
                    }
                )
            }

            item {
                MockNotifButton(
                    icon = Icons.Rounded.LocalOffer,
                    label = "Offre promotionnelle",
                    subtitle = "Notification promotion",
                    color = Color(0xFFFFB300),
                    enabled = promosEnabled,
                    onClick = {
                        NotificationHelper.showMockPromoNotification(context)
                        addHistory("Offre spéciale", "-20% sur les studios ce mois-ci !", NotificationHelper.CHANNEL_PROMOTIONS)
                    }
                )
            }

            item {
                MockNotifButton(
                    icon = Icons.Rounded.Info,
                    label = "Notification système",
                    subtitle = "Notification système",
                    color = Color(0xFFCE93D8),
                    enabled = systemEnabled,
                    onClick = {
                        NotificationHelper.showMockSystemNotification(context)
                        addHistory("Mise à jour", "LocAll a été mis à jour avec succès", NotificationHelper.CHANNEL_SYSTEM)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("PARAMÈTRES DES CANAUX", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
            }

            item {
                ChannelToggle("Réservations", "Canal par défaut", bookingsEnabled, { bookingsEnabled = it }, PrimaryGreen)
            }
            item {
                ChannelToggle("Messages", "Canal haute priorité", messagesEnabled, { messagesEnabled = it }, Color(0xFF4FC3F7))
            }
            item {
                ChannelToggle("Promotions", "Canal basse priorité", promosEnabled, { promosEnabled = it }, Color(0xFFFFB300))
            }
            item {
                ChannelToggle("Système", "Canal standard", systemEnabled, { systemEnabled = it }, Color(0xFFCE93D8))
            }

            if (history.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("HISTORIQUE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
                }

                items(history, key = { it.id }) { entry ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(alpha = 0.05f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    when (entry.channel) {
                                        NotificationHelper.CHANNEL_BOOKINGS -> Icons.Rounded.EventAvailable
                                        NotificationHelper.CHANNEL_MESSAGES -> Icons.Rounded.Mail
                                        NotificationHelper.CHANNEL_PROMOTIONS -> Icons.Rounded.LocalOffer
                                        else -> Icons.Rounded.Info
                                    },
                                    contentDescription = null,
                                    tint = when (entry.channel) {
                                        NotificationHelper.CHANNEL_BOOKINGS -> PrimaryGreen
                                        NotificationHelper.CHANNEL_MESSAGES -> Color(0xFF4FC3F7)
                                        NotificationHelper.CHANNEL_PROMOTIONS -> Color(0xFFFFB300)
                                        else -> Color(0xFFCE93D8)
                                    },
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(entry.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(entry.message, color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp)
                            }
                            Text(entry.timestamp, color = Color.White.copy(alpha = 0.3f), fontSize = 10.sp)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}

@Composable
private fun MockNotifButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    subtitle: String,
    color: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) Color(0xFF162133) else Color(0xFF162133).copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, if (enabled) color.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (enabled) color.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = if (enabled) color else Color.White.copy(alpha = 0.3f), modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(label, color = if (enabled) Color.White else Color.White.copy(alpha = 0.4f), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color.White.copy(alpha = 0.45f), fontSize = 11.sp)
            }
            Icon(Icons.Rounded.Send, contentDescription = "Envoyer", tint = if (enabled) color else Color.White.copy(alpha = 0.2f), modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun ChannelToggle(
    name: String,
    subtitle: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    color: Color
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SmoothIcon(
                    icon = when (name) {
                        "Réservations" -> Icons.Rounded.EventAvailable
                        "Messages" -> Icons.Rounded.Mail
                        "Promotions" -> Icons.Rounded.LocalOffer
                        else -> Icons.Rounded.Info
                    },
                    tint = if (enabled) color else Color.White.copy(alpha = 0.3f),
                    backgroundColor = if (enabled) color.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.05f)
                )
                Column {
                    Text(name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(subtitle, color = Color.White.copy(alpha = 0.45f), fontSize = 11.sp)
                }
            }
            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = BrandNavy,
                    checkedTrackColor = PrimaryGreen,
                    uncheckedTrackColor = Color.White.copy(alpha = 0.15f)
                )
            )
        }
    }
}
