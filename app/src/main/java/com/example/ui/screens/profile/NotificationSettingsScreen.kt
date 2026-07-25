package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PushNotificationSetting
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel

@Composable
fun NotificationSettingsScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val settings by viewModel.pushNotificationSettings.collectAsState()
    var currentSettings by remember(settings) {
        mutableStateOf(settings ?: PushNotificationSetting())
    }
    var selectedFrequency by remember { mutableIntStateOf(0) }

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
            Text("Paramètres de notifications", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Box(modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(4.dp)) {
                NotificationToggleRow(
                    icon = Icons.Rounded.AddBusiness,
                    title = "Nouvelles annonces",
                    subtitle = "Annonces correspondant à vos recherches",
                    checked = currentSettings.newListings,
                    onCheckedChange = {
                        currentSettings = currentSettings.copy(newListings = it)
                        viewModel.savePushNotificationSettings(currentSettings)
                    }
                )
                NotificationToggleRow(
                    icon = Icons.Rounded.PriceChange,
                    title = "Baisse de prix",
                    subtitle = "Sur les annonces en favoris",
                    checked = currentSettings.priceDrops,
                    onCheckedChange = {
                        currentSettings = currentSettings.copy(priceDrops = it)
                        viewModel.savePushNotificationSettings(currentSettings)
                    }
                )
                NotificationToggleRow(
                    icon = Icons.Rounded.EventAvailable,
                    title = "Rappels de réservation",
                    subtitle = "Retours et échéances",
                    checked = currentSettings.bookingReminders,
                    onCheckedChange = {
                        currentSettings = currentSettings.copy(bookingReminders = it)
                        viewModel.savePushNotificationSettings(currentSettings)
                    }
                )
                NotificationToggleRow(
                    icon = Icons.Rounded.Chat,
                    title = "Nouveaux messages",
                    subtitle = "Messages des propriétaires",
                    checked = currentSettings.messages,
                    onCheckedChange = {
                        currentSettings = currentSettings.copy(messages = it)
                        viewModel.savePushNotificationSettings(currentSettings)
                    }
                )
                NotificationToggleRow(
                    icon = Icons.Rounded.LocalOffer,
                    title = "Promotions et offres flash",
                    subtitle = "Offres spéciales et réductions",
                    checked = currentSettings.promotions,
                    onCheckedChange = {
                        currentSettings = currentSettings.copy(promotions = it)
                        viewModel.savePushNotificationSettings(currentSettings)
                    }
                )
                NotificationToggleRow(
                    icon = Icons.Rounded.Groups,
                    title = "Mises à jour communautaires",
                    subtitle = "Avis, litiges et activités",
                    checked = currentSettings.communityUpdates,
                    onCheckedChange = {
                        currentSettings = currentSettings.copy(communityUpdates = it)
                        viewModel.savePushNotificationSettings(currentSettings)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Fréquence des notifications", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                listOf("Immédiat", "Résumé horaire", "Résumé journalier").forEachIndexed { index, label ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedFrequency = index; viewModel.showSnackbar("Fréquence : $label") }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RadioButton(
                            selected = index == selectedFrequency,
                            onClick = { selectedFrequency = index; viewModel.showSnackbar("Fréquence : $label") },
                            colors = RadioButtonDefaults.colors(selectedColor = PrimaryGreen, unselectedColor = Color.White.copy(alpha = 0.4f))
                        )
                        Text(label, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = if (checked) PrimaryGreen else Color.White.copy(alpha = 0.4f), modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = PrimaryGreen,
                checkedTrackColor = PrimaryGreen.copy(alpha = 0.3f),
                uncheckedThumbColor = Color.White.copy(alpha = 0.5f),
                uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
            )
        )
    }
}
