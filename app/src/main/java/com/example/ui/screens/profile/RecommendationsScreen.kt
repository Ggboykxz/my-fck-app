package com.example.ui.screens.profile

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
import androidx.activity.compose.BackHandler
import com.example.data.model.RentalItem
import com.example.ui.components.AppAsyncImage
import com.example.ui.components.SmoothIcon
import com.example.ui.screens.formatPriceCfa
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel

@Composable
fun RecommendationsScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val allItems by viewModel.rawRentalItems.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()

    val basedOnSearch = remember(allItems) { allItems.shuffled().take(4) }
    val popularNearby = remember(allItems) { allItems.filter { it.city == "Libreville" }.shuffled().take(4) }
    val newest = remember(allItems) { allItems.sortedByDescending { it.createdAt }.take(4) }
    val inBudget = remember(allItems) { allItems.filter { it.pricePerDay in 15000..50000 }.take(4) }

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
                Text("Recommandations", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (basedOnSearch.isNotEmpty()) {
            item {
                RecommendationSection(
                    title = "Basé sur vos recherches",
                    icon = Icons.Rounded.Search,
                    iconColor = Color(0xFF4FC3F7),
                    items = basedOnSearch
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        if (popularNearby.isNotEmpty()) {
            item {
                RecommendationSection(
                    title = "Populaire près de chez vous",
                    icon = Icons.Rounded.TrendingUp,
                    iconColor = Color(0xFFFFB300),
                    items = popularNearby
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        if (newest.isNotEmpty()) {
            item {
                RecommendationSection(
                    title = "Nouveautés",
                    icon = Icons.Rounded.NewReleases,
                    iconColor = PrimaryGreen,
                    items = newest
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        if (inBudget.isNotEmpty()) {
            item {
                RecommendationSection(
                    title = "Dans votre budget",
                    icon = Icons.Rounded.AccountBalanceWallet,
                    iconColor = Color(0xFFAB47BC),
                    items = inBudget
                )
                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        if (allItems.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Aucune recommandation", color = Color.White.copy(alpha = 0.6f), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Explorez des annonces pour recevoir des suggestions", color = Color.White.copy(alpha = 0.35f), fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun RecommendationSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    items: List<RentalItem>
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
            }
            Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))

        items.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { item ->
                    RecommendationCard(item = item, modifier = Modifier.weight(1f))
                }
                if (row.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun RecommendationCard(item: RentalItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                    .background(Color.White.copy(alpha = 0.05f))
            ) {
                if (item.imageUrl != null) {
                    AppAsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.title,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Image, contentDescription = null, tint = Color.White.copy(alpha = 0.2f))
                    }
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    item.title,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(10.dp))
                    Text(
                        item.city,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }
                Text(
                    formatPriceCfa(item.pricePerDay) + "/jour",
                    color = PrimaryGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
