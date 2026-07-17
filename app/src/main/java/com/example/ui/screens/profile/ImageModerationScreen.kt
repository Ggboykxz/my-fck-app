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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel

@Composable
fun ImageModerationScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("En attente", "Approuvées", "Rejetées")

    val pendingItems by viewModel.getPendingMediaItems().collectAsState(initial = emptyList())
    val approvedItems by viewModel.getApprovedMediaItems().collectAsState(initial = emptyList())
    val rejectedItems by viewModel.getRejectedMediaItems().collectAsState(initial = emptyList())

    val currentItems = when (selectedTab) {
        0 -> pendingItems
        1 -> approvedItems
        2 -> rejectedItems
        else -> emptyList()
    }

    var showGuidelines by remember { mutableStateOf(false) }

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
            Text("Modération d'Images", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tabs.forEachIndexed { index, tab ->
                    val count = when (index) {
                        0 -> pendingItems.size
                        1 -> approvedItems.size
                        2 -> rejectedItems.size
                        else -> 0
                    }
                    Surface(
                        onClick = { selectedTab = index },
                        color = if (selectedTab == index) PrimaryGreen.copy(alpha = 0.15f) else Color.Transparent,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                tab,
                                color = if (selectedTab == index) PrimaryGreen else Color.White.copy(alpha = 0.5f),
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                            )
                            if (count > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = if (selectedTab == index) PrimaryGreen else Color.White.copy(alpha = 0.15f),
                                    shape = CircleShape
                                ) {
                                    Text(
                                        "$count",
                                        color = if (selectedTab == index) BrandNavy else Color.White.copy(alpha = 0.5f),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            onClick = { showGuidelines = !showGuidelines },
            color = Color(0xFF162133),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Rounded.Info, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                Text("Directives de modération", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Icon(
                    if (showGuidelines) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.4f)
                )
            }
        }

        if (showGuidelines) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A2A))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "Images nettes et bien éclairées",
                        "Pas de contenu violent ou explicite",
                        "Respecter les droits d'auteur",
                        "Photos réelles du bien concerné",
                        "Résolution minimale: 800x600px",
                        "Pas de filtres excessifs"
                    ).forEach { rule ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(14.dp))
                            Text(rule, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (currentItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        when (selectedTab) {
                            0 -> Icons.Rounded.HourglassEmpty
                            1 -> Icons.Rounded.CheckCircle
                            else -> Icons.Rounded.Cancel
                        },
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        when (selectedTab) {
                            0 -> "Aucune image en attente"
                            1 -> "Aucune image approuvée"
                            else -> "Aucune image rejetée"
                        },
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(currentItems, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF0F1A2A)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    when (item.mediaType) {
                                        "video" -> Icons.Rounded.Videocam
                                        "360" -> Icons.Rounded.Panorama
                                        else -> Icons.Rounded.Image
                                    },
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.3f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    when (item.mediaType) {
                                        "video" -> "Vidéo"
                                        "360" -> "Tour 360°"
                                        else -> "Image"
                                    },
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Surface(
                                        color = Color(0xFF0C2417),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            "Contenu approprié",
                                            color = PrimaryGreen,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Surface(
                                        color = Color(0xFF0C2417),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            "Résolution: élevée",
                                            color = PrimaryGreen,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            if (selectedTab == 0) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = { viewModel.moderateMediaItem(item.id, "approved") },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(PrimaryGreen.copy(alpha = 0.12f), CircleShape)
                                    ) {
                                        Icon(Icons.Rounded.Check, contentDescription = "Approuver", tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick = { viewModel.moderateMediaItem(item.id, "rejected") },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(Color(0xFFEF5350).copy(alpha = 0.12f), CircleShape)
                                    ) {
                                        Icon(Icons.Rounded.Close, contentDescription = "Rejeter", tint = Color(0xFFEF5350), modifier = Modifier.size(18.dp))
                                    }
                                }
                            } else {
                                val statusColor = if (selectedTab == 1) PrimaryGreen else Color(0xFFEF5350)
                                Surface(
                                    color = statusColor.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        if (selectedTab == 1) "Approuvé" else "Rejeté",
                                        color = statusColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
