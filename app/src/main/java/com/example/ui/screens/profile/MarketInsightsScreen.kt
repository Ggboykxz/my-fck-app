package com.example.ui.screens

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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MarketInsight
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel
import kotlinx.coroutines.delay

@Composable
fun MarketInsightsScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val insights by viewModel.marketInsights.collectAsState()

    val mockSeasonalData = listOf(
        Triple("Jan", 65, 0.8f), Triple("Fév", 58, 0.7f), Triple("Mar", 72, 0.9f),
        Triple("Avr", 68, 0.85f), Triple("Mai", 80, 1.0f), Triple("Jun", 95, 1.15f),
        Triple("Jul", 88, 1.05f), Triple("Aoû", 76, 0.92f), Triple("Sep", 70, 0.87f),
        Triple("Oct", 62, 0.78f), Triple("Nov", 55, 0.7f), Triple("Déc", 85, 1.02f)
    )
    val mockDemandByCity = listOf(
        Triple("Libreville", "Élevée", Color(0xFF13EC5B)),
        Triple("Port-Gentil", "Moyenne", Color(0xFFFFB300)),
        Triple("Franceville", "Faible", Color(0xFFE57373)),
        Triple("Oyem", "Moyenne", Color(0xFFFFB300)),
        Triple("Akanda", "Élevée", Color(0xFF13EC5B))
    )
    val mockInvestments = listOf(
        Triple("Akanda Nord", "+18%", "Nouveau lotissement"),
        Triple("Owendo Est", "+12%", "Zone industrielle"),
        Triple("Batterie IV", "+9%", "Haut standing")
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
                Text("Aperçu du Marché", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Box(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Tendance des prix par catégorie", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.6f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                    val mockCategories = listOf(
                        Triple("Immobilier", 72000, Color(0xFF13EC5B)),
                        Triple("Véhicules", 65000, Color(0xFF4FC3F7)),
                        Triple("Équip.", 42000, Color(0xFFFFB300)),
                        Triple("Services", 55000, Color(0xFFCE93D8))
                    )
                    val maxPrice = 80000f
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val barWidth = size.width / (mockCategories.size * 2f)
                        val spacing = size.width / mockCategories.size
                        mockCategories.forEachIndexed { i, (_, price, color) ->
                            val barHeight = (price.toFloat() / maxPrice) * size.height * 0.8f
                            val x = spacing * i + spacing / 2 - barWidth / 2
                            val y = size.height - barHeight
                            drawRoundRect(
                                color = color,
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(8f, 8f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        mockCategories.forEach { (name, _, _) ->
                            Text(name, fontSize = 9.sp, color = Color.White.copy(alpha = 0.5f), textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))

            Text("Demande par ville", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(mockDemandByCity, key = { it.first }) { (city, level, color) ->
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
                    Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(city, modifier = Modifier.weight(1f), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Surface(
                        color = color.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(level, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))

            Text("Tendance saisonnière", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
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
                    val maxDemand = mockSeasonalData.maxOf { it.second }
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val barW = size.width / (mockSeasonalData.size * 1.5f)
                        val spacing = size.width / mockSeasonalData.size
                        mockSeasonalData.forEachIndexed { i, (_, demand, _) ->
                            val barH = (demand.toFloat() / maxDemand) * size.height * 0.85f
                            val x = spacing * i + spacing / 2 - barW / 2
                            val y = size.height - barH
                            drawRoundRect(
                                color = PrimaryGreen.copy(alpha = 0.7f),
                                topLeft = Offset(x, y),
                                size = Size(barW, barH),
                                cornerRadius = CornerRadius(6f, 6f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        mockSeasonalData.forEach { (m, _, _) ->
                            Text(m, fontSize = 8.sp, color = Color.White.copy(alpha = 0.4f), textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))

            Text("Opportunités d'investissement", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(mockInvestments, key = { it.first }) { (area, growth, desc) ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0C2417)),
                border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.TrendingUp, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(area, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(desc, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                    }
                    Text(growth, color = PrimaryGreen, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }

        if (insights.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Données du marché", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
            }
            items(insights, key = { it.id }) { insight ->
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(insight.category, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(insight.city, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(formatPriceCfa(insight.averagePrice), color = PrimaryGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("${insight.listingCount} annonces", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            when (insight.trend) {
                                "up" -> Icons.Rounded.ArrowUpward
                                "down" -> Icons.Rounded.ArrowDownward
                                else -> Icons.Rounded.ArrowRight
                            },
                            contentDescription = null,
                            tint = when (insight.trend) {
                                "up" -> PrimaryGreen
                                "down" -> Color.Red
                                else -> Color.White.copy(alpha = 0.5f)
                            },
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
