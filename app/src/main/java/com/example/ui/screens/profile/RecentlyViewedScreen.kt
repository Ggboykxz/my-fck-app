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
fun RecentlyViewedScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val recentlyViewed by viewModel.recentlyViewed.collectAsState()
    var sortBy by remember { mutableIntStateOf(0) }
    val sortLabels = listOf("Plus récent", "Prix", "Ville")

    val sortedItems = remember(recentlyViewed, sortBy) {
        when (sortBy) {
            1 -> recentlyViewed.sortedBy { it.pricePerDay }
            2 -> recentlyViewed.sortedBy { it.city }
            else -> recentlyViewed
        }
    }

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
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Récemment consultées", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    "${recentlyViewed.size} annonces consultées",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }
            if (recentlyViewed.isNotEmpty()) {
                IconButton(onClick = { viewModel.clearRecentlyViewed() }) {
                    Icon(Icons.Rounded.DeleteSweep, contentDescription = "Effacer", tint = Color(0xFFEF5350))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (recentlyViewed.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                sortLabels.forEachIndexed { index, label ->
                    FilterChip(
                        selected = sortBy == index,
                        onClick = { sortBy = index },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGreen.copy(alpha = 0.2f),
                            selectedLabelColor = PrimaryGreen,
                            containerColor = Color.White.copy(alpha = 0.05f),
                            labelColor = Color.White.copy(alpha = 0.6f)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = Color.White.copy(alpha = 0.1f),
                            selectedBorderColor = PrimaryGreen.copy(alpha = 0.4f),
                            enabled = true,
                            selected = sortBy == index
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (recentlyViewed.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Rounded.History,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Aucune annonce consultée", color = Color.White.copy(alpha = 0.6f), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Vos annonces récentes apparaîtront ici", color = Color.White.copy(alpha = 0.35f), fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(sortedItems, key = { it.id }) { item ->
                    RecentlyViewedCard(item = item, timeAgo = "il y a ${1 + (item.id % 12)}h")
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun RecentlyViewedCard(item: RentalItem, timeAgo: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
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
                        Icon(Icons.Rounded.Image, contentDescription = null, tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(28.dp))
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(12.dp))
                    Text(
                        "${item.city} · ${item.neighborhood}",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(timeAgo, color = Color.White.copy(alpha = 0.3f), fontSize = 11.sp)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    formatPriceCfa(item.pricePerDay),
                    color = PrimaryGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text("/jour", color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
            }
        }
    }
}
