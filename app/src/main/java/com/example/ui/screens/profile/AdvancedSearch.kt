package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.model.RentalCategory
import com.example.ui.viewmodel.RentalViewModel

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
            (listOf("Tous") + RentalCategory.allDisplayNames).forEach { cat ->
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

        if (searchQuery.isBlank()) {
            val searchHistory by viewModel.searchHistory.collectAsState()
            if (searchHistory.isNotEmpty()) {
                Text("Recherches récentes", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                searchHistory.take(5).forEach { entry ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                searchQuery = entry.query
                                viewModel.setSearchQuery(entry.query)
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Rounded.History, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
                        Text(entry.query, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Text("${items.size} résultat(s) trouvé(s)", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(items, key = { it.id }) { item ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(item.imageUrl)
                                .crossfade(true)
                                .size(Size.ORIGINAL)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .build(),
                            contentDescription = item.title,
                            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                            error = painterResource(android.R.drawable.ic_menu_close_clear_cancel)
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
