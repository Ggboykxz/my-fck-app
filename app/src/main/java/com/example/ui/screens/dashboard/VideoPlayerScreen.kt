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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.RentalViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    videoUrl: String,
    onBack: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(false) }
    var currentTime by remember { mutableIntStateOf(0) }
    var isFullscreen by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val totalTime = 345

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isPlaying && currentTime < totalTime) {
                delay(1000)
                currentTime++
            }
            isPlaying = false
        }
    }

    fun formatTime(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        return "%d:%02d".format(m, s)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lecteur vidéo", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A1628))
            )
        },
        containerColor = Color(0xFF0A1628)
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color(0xFF111827))
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Rounded.Videocam,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(80.dp)
                    )
                    Text("Vidéo de démonstration", color = Color.White.copy(alpha = 0.3f), fontSize = 14.sp)
                }

                Box(
                    modifier = Modifier.fillMaxSize().clickable { isPlaying = !isPlaying },
                    contentAlignment = Alignment.Center
                ) {
                    if (!isPlaying) {
                        Surface(
                            modifier = Modifier.size(64.dp).clip(CircleShape),
                            color = Color.White.copy(alpha = 0.9f),
                            shape = CircleShape
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Rounded.PlayArrow, contentDescription = "Play", tint = Color(0xFF0A1628), modifier = Modifier.size(40.dp))
                            }
                        }
                    } else {
                        Surface(
                            modifier = Modifier.size(64.dp).clip(CircleShape),
                            color = Color.White.copy(alpha = 0.7f),
                            shape = CircleShape
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Rounded.Pause, contentDescription = "Pause", tint = Color(0xFF0A1628), modifier = Modifier.size(40.dp))
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(Color.Black.copy(alpha = 0.6f)).padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Slider(
                        value = currentTime.toFloat(),
                        onValueChange = { currentTime = it.toInt() },
                        valueRange = 0f..totalTime.toFloat(),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF00D4AA),
                            activeTrackColor = Color(0xFF00D4AA),
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(formatTime(currentTime), color = Color.White, fontSize = 12.sp)
                        Text(formatTime(totalTime), color = Color.White, fontSize = 12.sp)
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text("Appartement Vue Mer - Visite Virtuelle", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Visibility, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("2 345 vues", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(Icons.Rounded.Schedule, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Il y a 3 jours", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Découvrez cet appartement exceptionnel avec une vue panoramique sur la mer. Cette visite virtuelle vous permet d'explorer chaque pièce en détail avant votre réservation.",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))
                Text("Vidéos similaires", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(12.dp))

                val relatedVideos = listOf(
                    Triple("Villa La Sablière - Tour complet", "1 234 vues", "4:32"),
                    Triple("Toyota Hilux - Présentation", "890 vues", "2:15"),
                    Triple("Salle de fête Élégance - Aperçu", "567 vues", "3:48"),
                    Triple("Terrain Owendo - Drone vue", "2 100 vues", "5:02"),
                    Triple("Caméra Canon R5 - Démo", "445 vues", "1:55")
                )

                relatedVideos.forEach { (title, views, duration) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF162133)).padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(100.dp, 60.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF1E293B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.PlayCircleOutline, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(28.dp))
                            Surface(
                                modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp),
                                color = Color.Black.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(duration, color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(views, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
