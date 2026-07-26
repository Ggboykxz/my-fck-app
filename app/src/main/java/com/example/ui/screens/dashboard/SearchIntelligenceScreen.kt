package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SavedSearch
import com.example.data.model.SearchSuggestion
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchIntelligenceScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val searchQuery by viewModel.intelligentSearchQuery.collectAsState()
    val suggestions by viewModel.searchSuggestions.collectAsState()
    val savedSearches by viewModel.savedSearches.collectAsState()
    val trendingSearches by viewModel.trendingSearches.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
    val isVoiceSearching by viewModel.isVoiceSearching.collectAsState()
    val voiceSearchResult by viewModel.voiceSearchResult.collectAsState()
    val searchAnalytics by viewModel.searchAnalytics.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()

    var showSuggestions by remember { mutableStateOf(false) }
    var fuzzyResults by remember { mutableStateOf(emptyList<com.example.data.model.RentalItem>()) }
    var priceRange by remember { mutableStateOf(0f..150000f) }

    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 2) {
            fuzzyResults = viewModel.fuzzySearch(searchQuery)
            showSuggestions = true
        } else {
            fuzzyResults = emptyList()
            showSuggestions = false
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
            Text("Recherche Intelligente", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Box(modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                ) {
                    Column {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = {
                                viewModel.setIntelligentSearchQuery(it)
                                viewModel.logSearchToAnalytics(it)
                            },
                            placeholder = { Text("Recherche floue, intelligente...", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp) },
                            leadingIcon = { SmoothIcon(Icons.Rounded.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), backgroundColor = Color.White.copy(alpha = 0.08f), modifier = Modifier.size(32.dp), iconSize = 18.dp) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.setIntelligentSearchQuery("") }) {
                                        Icon(Icons.Rounded.Clear, contentDescription = null, tint = Color.White.copy(alpha = 0.5f))
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                cursorColor = PrimaryGreen
                            ),
                            singleLine = true
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                onClick = { viewModel.startVoiceSearch() },
                                color = if (isVoiceSearching) PrimaryGreen.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.06f),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, if (isVoiceSearching) PrimaryGreen else Color.White.copy(alpha = 0.12f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (isVoiceSearching) {
                                        CircularProgressIndicator(modifier = Modifier.size(14.dp), color = PrimaryGreen, strokeWidth = 2.dp)
                                    } else {
                                        Icon(Icons.Rounded.Mic, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                                    }
                                    Text(
                                        if (isVoiceSearching) "Écoute..." else "Vocal",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryGreen
                                    )
                                }
                            }

                            Surface(
                                onClick = { viewModel.saveCurrentSearch() },
                                color = Color.White.copy(alpha = 0.06f),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Rounded.Bookmark, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                                    Text("Sauvegarder", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB300))
                                }
                            }
                        }
                    }
                }
            }

            if (voiceSearchResult != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C2417)),
                        border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Rounded.Mic, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(24.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Résultat vocal", color = PrimaryGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(voiceSearchResult ?: "", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            if (showSuggestions && suggestions.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Suggestions", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(8.dp))
                            suggestions.take(5).forEach { s ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.setIntelligentSearchQuery(s.query)
                                            viewModel.setSearchQuery(s.query)
                                            showSuggestions = false
                                        }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(Icons.Rounded.TrendingUp, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(s.query, fontSize = 13.sp, color = Color.White)
                                    }
                                    Text("${s.searchCount}", fontSize = 11.sp, color = Color.White.copy(alpha = 0.4f))
                                }
                            }
                        }
                    }
                }
            }

            if (fuzzyResults.isNotEmpty()) {
                item {
                    Text("Résultats flous (${fuzzyResults.size})", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                items(fuzzyResults.take(5), key = { it.id }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF0D2137)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.Search, contentDescription = null, tint = PrimaryGreen.copy(alpha = 0.5f))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("${item.neighborhood}, ${item.city}", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                            }
                            Text(formatPriceCfa(item.pricePerDay), color = PrimaryGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (savedSearches.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Recherches sauvegardées", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("${savedSearches.size}", color = PrimaryGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
                items(savedSearches, key = { it.id }) { search ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4A3515)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.Bookmark, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(18.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(search.query, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    search.category?.let { Text(it, fontSize = 10.sp, color = PrimaryGreen) }
                                    search.city?.let { Text(it, fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f)) }
                                }
                            }
                            Switch(
                                checked = search.alertEnabled,
                                onCheckedChange = { viewModel.toggleSearchAlert(search.id, it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = PrimaryGreen, checkedTrackColor = PrimaryGreen.copy(alpha = 0.3f))
                            )
                            IconButton(onClick = { viewModel.deleteSavedSearch(search.id) }) {
                                Icon(Icons.Rounded.Delete, contentDescription = null, tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            item {
                Text("Recherches récentes", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            items(searchHistory.take(10), key = { it.id }) { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.setIntelligentSearchQuery(entry.query)
                            viewModel.setSearchQuery(entry.query)
                        }
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Rounded.History, contentDescription = null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(18.dp))
                    Text(entry.query, fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f))
                }
            }

            if (trendingSearches.isNotEmpty()) {
                item {
                    Text("Recherches tendance", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(trendingSearches, key = { it.id }) { s ->
                            val isActive = searchQuery == s.query
                            Surface(
                                onClick = {
                                    viewModel.setIntelligentSearchQuery(s.query)
                                    viewModel.setSearchQuery(s.query)
                                },
                                color = if (isActive) PrimaryGreen else PrimaryGreen.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, if (isActive) PrimaryGreen else PrimaryGreen.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Rounded.TrendingUp, contentDescription = null, tint = if (isActive) BrandNavy else PrimaryGreen, modifier = Modifier.size(14.dp))
                                    Text(s.query, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isActive) BrandNavy else PrimaryGreen)
                                    Text("${s.searchCount}", fontSize = 10.sp, color = if (isActive) BrandNavy.copy(alpha = 0.7f) else PrimaryGreen.copy(alpha = 0.7f))
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Filtres de recherche", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Catégorie", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(listOf("Tous", "Immobilier", "Véhicules", "Équipements", "Services"), key = { it }) { cat ->
                                val isSelected = selectedCategory == cat
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) PrimaryGreen else Color.White.copy(alpha = 0.06f))
                                        .border(1.dp, if (isSelected) Color.Transparent else Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                                        .clickable { viewModel.setSelectedCategory(cat) }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Text(cat, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) BrandNavy else Color.White.copy(alpha = 0.7f))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Ville", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(listOf("Tous", "Libreville", "Port-Gentil", "Franceville", "Oyem"), key = { it }) { city ->
                                val isSelected = selectedCity == city
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) PrimaryGreen else Color.White.copy(alpha = 0.06f))
                                        .border(1.dp, if (isSelected) Color.Transparent else Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                                        .clickable { viewModel.setSelectedCity(city) }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Text(city, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) BrandNavy else Color.White.copy(alpha = 0.7f))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Prix max: ${formatPriceCfa(priceRange.endInclusive.toInt())}", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        RangeSlider(
                            value = priceRange,
                            onValueChange = { priceRange = it },
                            valueRange = 0f..150000f,
                            colors = SliderDefaults.colors(
                                thumbColor = PrimaryGreen,
                                activeTrackColor = PrimaryGreen,
                                inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }

            if (searchAnalytics.isNotEmpty()) {
                item {
                    Text("Analyse de recherche", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val maxCount = searchAnalytics.maxOfOrNull { it.searchCount } ?: 1
                            searchAnalytics.take(7).forEach { s ->
                                val fraction = s.searchCount.toFloat() / maxCount.toFloat()
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        s.query,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.width(90.dp),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(12.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color.White.copy(alpha = 0.06f))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(fraction)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(PrimaryGreen)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("${s.searchCount}", fontSize = 11.sp, color = PrimaryGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
