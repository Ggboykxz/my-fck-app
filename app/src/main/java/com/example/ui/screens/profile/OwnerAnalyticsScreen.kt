package com.example.ui.screens

import androidx.compose.animation.core.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OwnerAnalytics
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel
import kotlinx.coroutines.delay

@Composable
fun OwnerAnalyticsScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val analytics by viewModel.ownerAnalytics.collectAsState()
    val isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) { delay(800) }

    val mockWeeklyViews = listOf(120, 185, 210, 195, 340, 280, 160)
    val mockSources = listOf(
        Triple("Recherche", 45f, Color(0xFF13EC5B)),
        Triple("Direct", 30f, Color(0xFF4FC3F7)),
        Triple("Partage", 25f, Color(0xFFFFB300))
    )
    val mockTopListings = listOf(
        Triple("Villa de Luxe La Sablière", 342, 4.9f),
        Triple("Toyota Prado VXR 2023", 218, 4.8f),
        Triple("Pack Sono Concert Pro", 156, 4.6f),
        Triple("Appartement Vue Mer", 134, 4.7f)
    )

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
                Text("Analytique Propriétaire", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Box(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnalyticsOverviewCard(
                    modifier = Modifier.weight(1f),
                    title = "Vues totales",
                    value = "${analytics?.totalViews ?: 1847}",
                    icon = Icons.Rounded.Visibility,
                    color = Color(0xFF4FC3F7),
                    gradientStart = Color(0xFF0D2137)
                )
                AnalyticsOverviewCard(
                    modifier = Modifier.weight(1f),
                    title = "Demandes",
                    value = "${analytics?.totalInquiries ?: 124}",
                    icon = Icons.Rounded.Email,
                    color = Color(0xFFFFB300),
                    gradientStart = Color(0xFF1A1507)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnalyticsOverviewCard(
                    modifier = Modifier.weight(1f),
                    title = "Taux conversion",
                    value = "${String.format(java.util.Locale.US, "%.1f", analytics?.conversionRate ?: 6.7f)}%",
                    icon = Icons.Rounded.TrendingUp,
                    color = PrimaryGreen,
                    gradientStart = Color(0xFF0C2417)
                )
                AnalyticsOverviewCard(
                    modifier = Modifier.weight(1f),
                    title = "Temps réponse",
                    value = analytics?.averageResponseTime ?: "12 min",
                    icon = Icons.Rounded.Schedule,
                    color = Color(0xFFCE93D8),
                    gradientStart = Color(0xFF1A0D24)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Vues hebdomadaires", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val maxVal = mockWeeklyViews.max().toFloat()
                        for (i in 1..4) {
                            val y = h * (i / 4f)
                            drawLine(Color.White.copy(alpha = 0.04f), Offset(0f, y), Offset(w, y), 2f)
                        }
                        val points = mockWeeklyViews.mapIndexed { i, v ->
                            Offset(w * (0.05f + i * 0.14f), h * (1f - v / maxVal * 0.8f))
                        }
                        val fillPath = Path().apply {
                            moveTo(points[0].x, h)
                            for (p in points) lineTo(p.x, p.y)
                            lineTo(points.last().x, h)
                            close()
                        }
                        drawPath(fillPath, Brush.verticalGradient(listOf(Color(0xFF4FC3F7).copy(alpha = 0.2f), Color.Transparent)))
                        val linePath = Path().apply {
                            moveTo(points[0].x, points[0].y)
                            for (i in 1 until points.size) {
                                val prev = points[i - 1]
                                val curr = points[i]
                                cubicTo((prev.x + curr.x) / 2, prev.y, (prev.x + curr.x) / 2, curr.y, curr.x, curr.y)
                            }
                        }
                        drawPath(linePath, Color(0xFF4FC3F7), style = Stroke(width = 4f))
                        for (p in points) {
                            drawCircle(BrandNavy, 8f, p)
                            drawCircle(Color(0xFF4FC3F7), 5f, p)
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim").forEach {
                            Text(it, fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Sources d'inquiries", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    mockSources.forEach { (name, pct, color) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(name, modifier = Modifier.weight(1f), fontSize = 13.sp, color = Color.White)
                            Box(
                                modifier = Modifier.width(120.dp).height(10.dp).clip(RoundedCornerShape(5.dp)).background(Color.White.copy(alpha = 0.06f))
                            ) {
                                Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(pct / 100f).clip(RoundedCornerShape(5.dp)).background(color))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("${pct.toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))

            Text("Annonces performantes", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(mockTopListings, key = { it.first }) { (title, views, rating) ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF0C2417)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.TrendingUp, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Rounded.Visibility, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(12.dp))
                            Text("$views vues", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                            Icon(Icons.Rounded.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(12.dp))
                            Text("$rating", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AnalyticsOverviewCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    gradientStart: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            Text(title, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}
