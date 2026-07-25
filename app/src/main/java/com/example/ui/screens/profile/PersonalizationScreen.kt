package com.example.ui.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import com.example.preferences.UserPreferences
import com.example.ui.components.SmoothIcon
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonalizationScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val userName by viewModel.userName.collectAsState()
    val isPhoneVerified by viewModel.isPhoneVerified.collectAsState()
    val profileCity by viewModel.profileCity.collectAsState()
    val profileDob by viewModel.profileDob.collectAsState()
    val profileLanguage by viewModel.profileLanguage.collectAsState()

    var darkMode by remember { mutableStateOf(UserPreferences.isDarkMode(context)) }
    var notificationsEnabled by remember { mutableStateOf(UserPreferences.notificationsEnabled(context)) }
    var selectedCurrency by remember { mutableStateOf(UserPreferences.getCurrency(context)) }
    var priceAlertsEnabled by remember { mutableStateOf(UserPreferences.priceAlertsEnabled(context)) }
    var dataSavingMode by remember { mutableStateOf(UserPreferences.dataSavingMode(context)) }
    var priceThreshold by remember { mutableStateOf(UserPreferences.getPriceAlertThreshold(context).toString()) }
    var showCurrencyDropdown by remember { mutableStateOf(false) }

    val categories = listOf("Appartement", "Maison", "Villa", "Studio", "Terrain", "Véhicule", "Équipement", "Salle de fête")
    val savedFavorites = remember { mutableStateOf(UserPreferences.getFavoriteCategories(context)) }

    val hasPhoto = true
    val hasName = userName.isNotBlank()
    val hasCity = profileCity.isNotBlank()
    val hasPhoneVerified = isPhoneVerified
    val hasEmail = true
    val completedItems = listOf(hasPhoto, hasName, hasCity, hasPhoneVerified, hasEmail)
    val completionPercent = (completedItems.count { it } * 100 / completedItems.size)

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
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.08f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Personnalisation", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SmoothIcon(icon = Icons.Rounded.DarkMode, tint = Color(0xFF4FC3F7), backgroundColor = Color(0xFF4FC3F7).copy(alpha = 0.12f))
                        Column {
                            Text("Thème", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Mode sombre activé", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                        }
                    }
                    Switch(
                        checked = darkMode,
                        onCheckedChange = {
                            darkMode = it
                            UserPreferences.setDarkMode(context, it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BrandNavy,
                            checkedTrackColor = PrimaryGreen,
                            uncheckedTrackColor = Color.White.copy(alpha = 0.15f)
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SmoothIcon(icon = Icons.Rounded.Notifications, tint = Color(0xFFFFB300), backgroundColor = Color(0xFFFFB300).copy(alpha = 0.12f))
                            Column {
                                Text("Notifications", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Alertes push et emails", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                            }
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = {
                                notificationsEnabled = it
                                UserPreferences.setNotificationsEnabled(context, it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = BrandNavy,
                                checkedTrackColor = PrimaryGreen,
                                uncheckedTrackColor = Color.White.copy(alpha = 0.15f)
                            )
                        )
                    }

                    if (notificationsEnabled) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                        Spacer(modifier = Modifier.height(12.dp))

                        listOf(
                            Triple("Nouvelles annonces", "Annonces correspondant à vos critères", true),
                            Triple("Messages privés", "Réponses des propriétaires", true),
                            Triple("Rappels de réservation", "24h avant le check-in", true),
                            Triple("Promotions", "Offres et réductions", false)
                        ).forEach { (title, subtitle, default) ->
                            var enabled by remember { mutableStateOf(default) }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(title, color = Color.White, fontSize = 13.sp)
                                    Text(subtitle, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                                }
                                Switch(
                                    checked = enabled,
                                    onCheckedChange = { enabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = BrandNavy,
                                        checkedTrackColor = PrimaryGreen,
                                        uncheckedTrackColor = Color.White.copy(alpha = 0.15f)
                                    )
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Box {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCurrencyDropdown = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SmoothIcon(icon = Icons.Rounded.AttachMoney, tint = PrimaryGreen, backgroundColor = PrimaryGreen.copy(alpha = 0.12f))
                            Column {
                                Text("Devise", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(selectedCurrency, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                            }
                        }
                        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = Color.White.copy(alpha = 0.3f))
                    }
                }
                DropdownMenu(
                    expanded = showCurrencyDropdown,
                    onDismissRequest = { showCurrencyDropdown = false },
                    modifier = Modifier.background(Color(0xFF162133))
                ) {
                    listOf("FCFA", "EUR", "USD").forEach { currency ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    currency,
                                    color = if (currency == selectedCurrency) PrimaryGreen else Color.White,
                                    fontWeight = if (currency == selectedCurrency) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                selectedCurrency = currency
                                UserPreferences.setCurrency(context, currency)
                                showCurrencyDropdown = false
                            },
                            leadingIcon = if (currency == selectedCurrency) {
                                { Icon(Icons.Rounded.Check, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(18.dp)) }
                            } else null
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SmoothIcon(icon = Icons.Rounded.Category, tint = Color(0xFFAB47BC), backgroundColor = Color(0xFFAB47BC).copy(alpha = 0.12f))
                        Text("Catégories favorites", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { category ->
                            val isSelected = category in savedFavorites.value
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    val newSet = savedFavorites.value.toMutableSet()
                                    if (isSelected) newSet.remove(category) else newSet.add(category)
                                    savedFavorites.value = newSet
                                    UserPreferences.setFavoriteCategories(context, newSet)
                                },
                                label = { Text(category, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFAB47BC).copy(alpha = 0.2f),
                                    selectedLabelColor = Color(0xFFAB47BC),
                                    containerColor = Color.White.copy(alpha = 0.05f),
                                    labelColor = Color.White.copy(alpha = 0.6f)
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = Color.White.copy(alpha = 0.1f),
                                    selectedBorderColor = Color(0xFFAB47BC).copy(alpha = 0.4f),
                                    enabled = true,
                                    selected = isSelected
                                )
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SmoothIcon(icon = Icons.Rounded.PriceChange, tint = Color(0xFFFF6F00), backgroundColor = Color(0xFFFF6F00).copy(alpha = 0.12f))
                            Column {
                                Text("Alertes prix", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Soyez alerté des bonnes affaires", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                            }
                        }
                        Switch(
                            checked = priceAlertsEnabled,
                            onCheckedChange = {
                                priceAlertsEnabled = it
                                UserPreferences.setPriceAlertsEnabled(context, it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = BrandNavy,
                                checkedTrackColor = PrimaryGreen,
                                uncheckedTrackColor = Color.White.copy(alpha = 0.15f)
                            )
                        )
                    }
                    if (priceAlertsEnabled) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Seuil de prix max", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = priceThreshold,
                            onValueChange = {
                                priceThreshold = it
                                it.toIntOrNull()?.let { v -> UserPreferences.setPriceAlertThreshold(context, v) }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            placeholder = { Text("100000", color = Color.White.copy(alpha = 0.3f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFF6F00),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                                focusedContainerColor = Color.White.copy(alpha = 0.05f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SmoothIcon(icon = Icons.Rounded.DataSaverOn, tint = Color(0xFFCE93D8), backgroundColor = Color(0xFFCE93D8).copy(alpha = 0.12f))
                        Column {
                            Text("Mode économie de données", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Réduire le chargement des images", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                        }
                    }
                    Switch(
                        checked = dataSavingMode,
                        onCheckedChange = {
                            dataSavingMode = it
                            UserPreferences.setDataSavingMode(context, it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BrandNavy,
                            checkedTrackColor = PrimaryGreen,
                            uncheckedTrackColor = Color.White.copy(alpha = 0.15f)
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("language") },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SmoothIcon(icon = Icons.Rounded.GTranslate, tint = Color(0xFF4FC3F7), backgroundColor = Color(0xFF4FC3F7).copy(alpha = 0.12f))
                        Column {
                            Text("Langue", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(profileLanguage, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                        }
                    }
                    Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = Color.White.copy(alpha = 0.3f))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Complétion du profil", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { completionPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (completionPercent >= 80) PrimaryGreen else Color(0xFFFFB300),
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "$completionPercent% complété",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val checkItems = listOf(
                        Triple("Photo de profil", hasPhoto, Icons.Rounded.CameraAlt),
                        Triple("Nom complet", hasName, Icons.Rounded.Person),
                        Triple("Ville", hasCity, Icons.Rounded.LocationOn),
                        Triple("Téléphone vérifié", hasPhoneVerified, Icons.Rounded.Phone),
                        Triple("Email vérifié", hasEmail, Icons.Rounded.Email)
                    )

                    checkItems.forEach { (label, done, icon) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                icon,
                                contentDescription = label,
                                tint = if (done) PrimaryGreen else Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(label, color = Color.White, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            if (done) {
                                Icon(Icons.Rounded.CheckCircle, contentDescription = "Complété", tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                            } else {
                                Icon(Icons.Rounded.Cancel, contentDescription = "Non complété", tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
