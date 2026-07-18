package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel
import com.example.data.model.MediaItem
import kotlinx.coroutines.delay

@Composable
fun MediaScreen(
    viewModel: RentalViewModel,
    listingId: Int,
    onBack: () -> Unit
) {
    val mediaItems by viewModel.mediaItems.collectAsState()
    var isLoading by remember { mutableStateOf(true) }
    var autoWatermark by remember { mutableStateOf(true) }
    var selectedItems by remember { mutableStateOf(setOf<Int>()) }
    var isSelectMode by remember { mutableStateOf(false) }
    var showUploadDialog by remember { mutableStateOf(false) }
    var uploadType by remember { mutableStateOf("image") }
    var isUploading by remember { mutableStateOf(false) }

    LaunchedEffect(listingId) {
        viewModel.loadMediaForListing(listingId)
        delay(600)
        isLoading = false
    }

    LaunchedEffect(isUploading) {
        if (isUploading) {
            delay(2000)
            viewModel.addMockMediaItem(listingId, uploadType)
            isUploading = false
            showUploadDialog = false
        }
    }

    if (showUploadDialog) {
        AlertDialog(
            onDismissRequest = { if (!isUploading) showUploadDialog = false },
            containerColor = Color(0xFF162133),
            title = { Text("Ajouter un média", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                if (isUploading) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(color = PrimaryGreen)
                        Text("Téléchargement en cours...", color = Color.White.copy(alpha = 0.7f))
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf(
                            Triple(Icons.Rounded.PhotoCamera, "Photo", "image"),
                            Triple(Icons.Rounded.Videocam, "Vidéo", "video"),
                            Triple(Icons.Rounded.Panorama, "Tour 360°", "360")
                        ).forEach { (icon, label, type) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        uploadType = type
                                        isUploading = true
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A2A)),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(PrimaryGreen.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                                    }
                                    Text(label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                if (!isUploading) {
                    TextButton(onClick = { showUploadDialog = false }) {
                        Text("Annuler", color = Color.White.copy(alpha = 0.6f))
                    }
                }
            }
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
            Text("Gestion des Médias", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            if (isSelectMode) {
                IconButton(onClick = { isSelectMode = false; selectedItems = emptySet() }) {
                    Icon(Icons.Rounded.Close, contentDescription = "Fermer", tint = Color.White)
                }
                if (selectedItems.isNotEmpty()) {
                    IconButton(onClick = {
                        selectedItems.forEach { viewModel.deleteMediaItem(it) }
                        selectedItems = emptySet()
                        isSelectMode = false
                    }) {
                        Icon(Icons.Rounded.Delete, contentDescription = "Supprimer", tint = Color(0xFFEF5350))
                    }
                }
            } else {
                IconButton(onClick = { isSelectMode = true }) {
                    Icon(Icons.Rounded.Checklist, contentDescription = "Sélectionner", tint = Color.White.copy(alpha = 0.6f))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { showUploadDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Ajouter", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Ajouter", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Rounded.WaterDrop, contentDescription = "Stockage", tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                    Text("Ajouter filigrane", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Switch(
                    checked = autoWatermark,
                    onCheckedChange = { autoWatermark = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = BrandNavy,
                        checkedTrackColor = PrimaryGreen,
                        uncheckedTrackColor = Color.White.copy(alpha = 0.15f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (isLoading) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(6) {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF162133))
                    )
                }
            }
        } else if (mediaItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Rounded.PhotoLibrary, contentDescription = "Galerie", tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(48.dp))
                    Text("Aucun média", color = Color.White.copy(alpha = 0.5f), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Ajoutez des photos ou vidéos à votre annonce", color = Color.White.copy(alpha = 0.35f), fontSize = 13.sp, textAlign = TextAlign.Center)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(mediaItems, key = { it.id }) { item ->
                    val isSelected = selectedItems.contains(item.id)
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF162133))
                            .clickable {
                                if (isSelectMode) {
                                    selectedItems = if (isSelected) selectedItems - item.id
                                    else selectedItems + item.id
                                }
                            }
                            .border(
                                2.dp,
                                if (isSelected) PrimaryGreen else Color.Transparent,
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF1A2740)),
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
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        val statusColor = when (item.moderationStatus) {
                            "approved" -> PrimaryGreen
                            "rejected" -> Color(0xFFEF5350)
                            else -> Color(0xFFFFB300)
                        }
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(6.dp),
                            color = statusColor.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                when (item.moderationStatus) {
                                    "approved" -> "Approuvé"
                                    "rejected" -> "Rejeté"
                                    else -> "En attente"
                                },
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        if (item.isWatermarked) {
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(6.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.WaterDrop,
                                    contentDescription = "Filigrane",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(2.dp)
                                )
                            }
                        }

                        if (isSelectMode && isSelected) {
                            Box(
                                modifier = Modifier.fillMaxSize().background(PrimaryGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(32.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
