package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import kotlinx.coroutines.delay

@Composable
fun VideoListingScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var recordingDuration by remember { mutableIntStateOf(0) }
    var trimPosition by remember { mutableFloatStateOf(0.3f) }
    var selectedThumbnail by remember { mutableIntStateOf(0) }
    var videoTitle by remember { mutableStateOf("") }
    var videoDescription by remember { mutableStateOf("") }
    var videoCategory by remember { mutableStateOf("Immobilier") }
    var showSaveDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (isRecording) {
                delay(1000)
                recordingDuration++
            }
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            containerColor = Color(0xFF162133),
            icon = { Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(48.dp)) },
            title = { Text("Annonce vidéo sauvegardée", color = Color.White, textAlign = TextAlign.Center) },
            text = { Text("Votre vidéo a été sauvegardée avec succès. Elle sera visible après modération.", color = Color.White.copy(alpha = 0.7f), textAlign = TextAlign.Center) },
            confirmButton = {
                Button(onClick = { showSaveDialog = false; onBack() }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen), modifier = Modifier.fillMaxWidth()) {
                    Text("Continuer", color = BrandNavy, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
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
            Text("Annonce Vidéo", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color(0xFF0D1B2A)),
                contentAlignment = Alignment.Center
            ) {
                if (isRecording) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                        )
                        Text(
                            "REC ${String.format("%02d:%02d", recordingDuration / 60, recordingDuration % 60)}",
                            color = Color.Red,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (isPlaying) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(56.dp)
                        )
                        Text("Lecture en cours...", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Videocam,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(48.dp)
                        )
                        Text("Aperçu vidéo", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { isPlaying = !isPlaying },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(PrimaryGreen.copy(alpha = 0.9f))
                    ) {
                        Icon(
                            if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Lecture",
                            tint = BrandNavy,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("ENREGISTRER", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    Triple(Icons.Rounded.Videocam, if (isRecording) "Arrêter" else "Enregistrer", if (isRecording) Color(0xFFEF5350) else PrimaryGreen),
                    Triple(Icons.Rounded.Upload, "Télécharger", Color(0xFF4FC3F7)),
                    Triple(Icons.Rounded.Panorama, "360°", Color(0xFFFFB300))
                ).forEach { (icon, label, color) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.clickable {
                            if (label == "Enregistrer" || label == "Arrêter") {
                                isRecording = !isRecording
                                if (!isRecording) recordingDuration = 0
                            }
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(color.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(22.dp))
                        }
                        Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("ROGNER LA VIDÉO", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Slider(
                    value = trimPosition,
                    onValueChange = { trimPosition = it },
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryGreen,
                        activeTrackColor = PrimaryGreen,
                        inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("0:00", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                    Text("${(trimPosition * 60).toInt()}s / 60s", color = PrimaryGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("1:00", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("MINIATURE", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            (0..3).forEach { index ->
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(16f / 9f)
                        .clickable { selectedThumbnail = index },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A2A)),
                    border = BorderStroke(
                        2.dp,
                        if (selectedThumbnail == index) PrimaryGreen else Color.Transparent
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.Image,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(20.dp)
                        )
                        if (selectedThumbnail == index) {
                            Icon(
                                Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("DÉTAILS", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = videoTitle,
            onValueChange = { videoTitle = it },
            placeholder = { Text("Titre de la vidéo", color = Color.White.copy(alpha = 0.35f)) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen, unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedTextColor = Color.White, unfocusedTextColor = Color.White
            )
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = videoDescription,
            onValueChange = { videoDescription = it },
            placeholder = { Text("Description", color = Color.White.copy(alpha = 0.35f)) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen, unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedTextColor = Color.White, unfocusedTextColor = Color.White
            )
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Immobilier", "Véhicules", "Équipements", "Services").forEach { cat ->
                Surface(
                    onClick = { videoCategory = cat },
                    color = if (videoCategory == cat) PrimaryGreen.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.06f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, if (videoCategory == cat) PrimaryGreen.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.08f))
                ) {
                    Text(
                        cat,
                        color = if (videoCategory == cat) PrimaryGreen else Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { showSaveDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(Icons.Rounded.Save, contentDescription = "Enregistrer", modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sauvegarder l'annonce vidéo", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
