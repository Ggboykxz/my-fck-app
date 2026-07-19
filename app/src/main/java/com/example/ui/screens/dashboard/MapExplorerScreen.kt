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
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RentalItem
import com.example.ui.components.*
import com.example.ui.screens.dashboard.MapData
import com.example.ui.screens.dashboard.MapMarker
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel
import com.example.util.LocationUtils

private val GabonShapeColor = Color(0xFF0D2818)
private val GabonBorder = Color(0xFF1A5C32)
private val MapWaterColor = Color(0xFF091A2A)
private val PinRed = Color(0xFFE74C3C)
private val PinOrange = Color(0xFFF39C12)
private val PinBlue = Color(0xFF3498DB)
private val PinPurple = Color(0xFF9B59B6)
private val PinTeal = Color(0xFF1ABC9C)
private val PinPink = Color(0xFFE91E63)

private data class CityPin(
    val name: String,
    val xFraction: Float,
    val yFraction: Float,
    val color: Color,
    val size: Int
)

private val gabonCities = listOf(
    CityPin("Libreville", 0.22f, 0.18f, PinRed, 22),
    CityPin("Port-Gentil", 0.58f, 0.38f, PinOrange, 18),
    CityPin("Franceville", 0.72f, 0.72f, PinBlue, 16),
    CityPin("Oyem", 0.68f, 0.15f, PinPurple, 15),
    CityPin("Lambaréné", 0.42f, 0.48f, PinTeal, 16),
    CityPin("Makokou", 0.65f, 0.45f, PinPink, 14)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapExplorerScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit,
    onSelectItem: (RentalItem) -> Unit
) {
    val rawItems by viewModel.rawRentalItems.collectAsState()
    var selectedCity by remember { mutableStateOf<String?>(null) }
    var showCitySheet by remember { mutableStateOf(false) }
    var selectedMarker by remember { mutableStateOf<MapMarker?>(null) }
    var selectedListingId by remember { mutableIntStateOf(-1) }

    val listingsByCity = remember(rawItems) {
        rawItems.groupBy { it.city }
    }

    val cityNames = remember(rawItems) {
        rawItems.map { it.city }.distinct().sorted()
    }

    val selectedCityListings = remember(selectedCity, listingsByCity) {
        if (selectedCity != null) listingsByCity[selectedCity] ?: emptyList()
        else emptyList()
    }

    val selectedArea = remember(selectedCity) {
        MapData.cityAreas.find { it.name == selectedCity }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1526))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
            ) {
                Icon(
                    Icons.Rounded.ArrowBack,
                    contentDescription = "Retour",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Carte des annonces",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${rawItems.size} annonces au Gabon",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(PrimaryGreen.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${listingsByCity.size} villes",
                    color = PrimaryGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        BoxWithConstraints(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(400.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MapWaterColor)
        ) {
            val mapWidth = maxWidth
            val mapHeight = maxHeight
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gabonPath = Path().apply {
                    moveTo(size.width * 0.08f, size.height * 0.05f)
                    lineTo(size.width * 0.35f, size.height * 0.02f)
                    lineTo(size.width * 0.55f, size.height * 0.03f)
                    lineTo(size.width * 0.72f, size.height * 0.05f)
                    lineTo(size.width * 0.82f, size.height * 0.12f)
                    lineTo(size.width * 0.88f, size.height * 0.22f)
                    lineTo(size.width * 0.92f, size.height * 0.32f)
                    lineTo(size.width * 0.90f, size.height * 0.45f)
                    lineTo(size.width * 0.85f, size.height * 0.55f)
                    lineTo(size.width * 0.82f, size.height * 0.65f)
                    lineTo(size.width * 0.78f, size.height * 0.75f)
                    lineTo(size.width * 0.70f, size.height * 0.82f)
                    lineTo(size.width * 0.58f, size.height * 0.88f)
                    lineTo(size.width * 0.42f, size.height * 0.90f)
                    lineTo(size.width * 0.28f, size.height * 0.85f)
                    lineTo(size.width * 0.18f, size.height * 0.78f)
                    lineTo(size.width * 0.10f, size.height * 0.65f)
                    lineTo(size.width * 0.05f, size.height * 0.50f)
                    lineTo(size.width * 0.03f, size.height * 0.35f)
                    lineTo(size.width * 0.04f, size.height * 0.20f)
                    close()
                }

                drawPath(
                    path = gabonPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            GabonShapeColor,
                            Color(0xFF0A2210),
                            GabonShapeColor
                        ),
                        startY = 0f,
                        endY = size.height
                    ),
                    style = Fill
                )
                drawPath(
                    path = gabonPath,
                    color = GabonBorder,
                    style = Stroke(width = 2.dp.toPx())
                )

                drawLine(
                    color = GabonBorder.copy(alpha = 0.3f),
                    start = Offset(size.width * 0.08f, size.height * 0.05f),
                    end = Offset(size.width * 0.42f, size.height * 0.90f),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = GabonBorder.copy(alpha = 0.3f),
                    start = Offset(size.width * 0.35f, size.height * 0.02f),
                    end = Offset(size.width * 0.78f, size.height * 0.75f),
                    strokeWidth = 1.dp.toPx()
                )
            }

            gabonCities.forEach { city ->
                val cityCount = listingsByCity[city.name]?.size ?: 0
                val isSelected = selectedCity == city.name
                val pulseAnim = rememberInfiniteTransition(label = "pulse_${city.name}")
                val pulseScale by pulseAnim.animateFloat(
                    initialValue = 1f,
                    targetValue = if (cityCount > 0) 1.15f else 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulseScale_${city.name}"
                )

                Box(
                    modifier = Modifier
                        .offset(
                            x = (mapWidth * city.xFraction) - 20.dp,
                            y = (mapHeight * city.yFraction) - 20.dp
                        )
                        .size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size((36 * pulseScale).dp)
                                .offset(((36 - 36 * pulseScale) / 2).dp, ((36 - 36 * pulseScale) / 2).dp)
                                .clip(CircleShape)
                                .background(city.color.copy(alpha = 0.2f))
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(city.size.dp)
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(city.color)
                            .clickable {
                                selectedCity = if (selectedCity == city.name) null else city.name
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (cityCount > 0) {
                            Text(
                                text = "$cityCount",
                                color = Color.White,
                                fontSize = (city.size / 3).sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .offset(
                            x = (mapWidth * city.xFraction) - 30.dp,
                            y = (mapHeight * city.yFraction) + (city.size / 2 + 2).dp
                        )
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = city.name,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Gabon",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(PrimaryGreen)
                    )
                    Text(
                        text = "Annonces",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                val isAllSelected = selectedCity == null
                Surface(
                    onClick = { selectedCity = null },
                    color = if (isAllSelected) PrimaryGreen else Color.White.copy(alpha = 0.06f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(
                        1.dp,
                        if (isAllSelected) PrimaryGreen else Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Public,
                            contentDescription = null,
                            tint = if (isAllSelected) BrandNavy else Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Toutes",
                            color = if (isAllSelected) BrandNavy else Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(
                                    if (isAllSelected) BrandNavy.copy(alpha = 0.15f)
                                    else Color.White.copy(alpha = 0.08f)
                                )
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "${rawItems.size}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAllSelected) BrandNavy else Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            items(cityNames) { cityName ->
                val count = listingsByCity[cityName]?.size ?: 0
                val isActive = selectedCity == cityName
                val cityColor = gabonCities.find { it.name == cityName }?.color
                    ?: PrimaryGreen

                Surface(
                    onClick = {
                        selectedCity = if (selectedCity == cityName) null else cityName
                    },
                    color = if (isActive) cityColor.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.06f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(
                        1.dp,
                        if (isActive) cityColor.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isActive) cityColor else Color.White.copy(alpha = 0.3f))
                        )
                        Text(
                            text = cityName,
                            color = if (isActive) cityColor else Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(
                                    if (isActive) cityColor.copy(alpha = 0.2f)
                                    else Color.White.copy(alpha = 0.08f)
                                )
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "$count",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) cityColor else Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        AnimatedVisibility(
            visible = selectedCity != null,
            enter = expandVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(),
            exit = shrinkVertically(
                animationSpec = tween(250)
            ) + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF162133)
                ),
                border = BorderStroke(
                    1.dp,
                    gabonCities.find { it.name == selectedCity }?.color?.copy(alpha = 0.3f)
                        ?: Color.White.copy(alpha = 0.1f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        gabonCities.find { it.name == selectedCity }?.color
                                            ?: PrimaryGreen
                                    )
                            )
                            Column {
                                Text(
                                    text = selectedCity ?: "",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${selectedCityListings.size} annonce(s)",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        IconButton(
                            onClick = { selectedCity = null },
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.08f))
                        ) {
                            Icon(
                                Icons.Rounded.Close,
                                contentDescription = "Fermer",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    if (selectedArea != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AreaStatChip("Prix moyen", "${selectedArea.avgPrice}", Color(0xFFFFB300))
                            AreaStatChip("Annonces", "${selectedArea.listingCount}", Color(0xFF4FC3F7))
                            AreaStatChip("Demande", selectedArea.demandLevel, when(selectedArea.demandLevel) {
                                "Élevée" -> Color(0xFFF44336)
                                "Moyenne" -> Color(0xFFFFB300)
                                else -> Color(0xFF4CAF50)
                            })
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (selectedCityListings.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Aucune annonce dans cette ville",
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 260.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(selectedCityListings.take(10), key = { it.id }) { item ->
                                CityListingItem(
                                    item = item,
                                    isSelected = item.id == selectedListingId,
                                    onClick = {
                                        selectedListingId = item.id
                                        onSelectItem(item)
                                    }
                                )
                            }
                            if (selectedCityListings.size > 10) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "+${selectedCityListings.size - 10} autres annonces",
                                            color = PrimaryGreen,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (selectedCity == null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(cityNames, key = { it }) { cityName ->
                    val cityItems = listingsByCity[cityName] ?: emptyList()
                    val cityPin = gabonCities.find { it.name == cityName }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF162133)
                        ),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCity = cityName
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        (cityPin?.color ?: PrimaryGreen).copy(alpha = 0.15f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Rounded.LocationOn,
                                    contentDescription = null,
                                    tint = cityPin?.color ?: PrimaryGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = cityName,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = cityItems.joinToString(", ") {
                                        it.category
                                    }.take(50).ifEmpty { "Annonces" },
                                    color = Color.White.copy(alpha = 0.45f),
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            (cityPin?.color ?: PrimaryGreen).copy(alpha = 0.12f)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${cityItems.size}",
                                        color = cityPin?.color ?: PrimaryGreen,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "annonce(s)",
                                    color = Color.White.copy(alpha = 0.35f),
                                    fontSize = 9.sp
                                )
                            }
                            Icon(
                                Icons.Rounded.ChevronRight,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CityListingItem(
    item: RentalItem,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val marker = MapData.markers.find { it.title.contains(item.title.take(10), ignoreCase = true) }
    val markerColor = marker?.color ?: PrimaryGreen
    val distance = LocationUtils.calculateDistance(
        LocationUtils.USER_LAT, LocationUtils.USER_LNG,
        MapData.markers.firstOrNull()?.lat ?: 0.3763,
        MapData.markers.firstOrNull()?.lng ?: 9.4536
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) markerColor.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.05f)
        ),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) markerColor.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.06f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(markerColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    getCategoryIcon(item.category),
                    contentDescription = null,
                    tint = markerColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${item.neighborhood} - ${item.category}",
                        color = Color.White.copy(alpha = 0.45f),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "• ${LocationUtils.formatDistance(distance)}",
                        color = markerColor.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${item.pricePerDay}",
                    color = PrimaryGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "F/jour",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 9.sp
                )
            }
        }
    }
}

private fun getCategoryIcon(category: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (category.lowercase()) {
        "immobilier" -> Icons.Rounded.Home
        "véhicules", "vehicules" -> Icons.Rounded.DirectionsCar
        "équipements", "equipements" -> Icons.Rounded.Build
        "évenementiel", "evenementiel" -> Icons.Rounded.Celebration
        "mode & beauté", "mode", "beauté", "beaute" -> Icons.Rounded.Checkroom
        "services" -> Icons.Rounded.Handyman
        "espaces" -> Icons.Rounded.Business
        "matériel pro", "materiel pro" -> Icons.Rounded.Engineering
        "marine & fluvial", "marine", "fluvial" -> Icons.Rounded.Sailing
        "sport & loisirs", "sport", "loisirs" -> Icons.Rounded.SportsTennis
        else -> Icons.Rounded.Category
    }
}

@Composable
private fun AreaStatChip(label: String, value: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(value, color = color, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            Text(label, color = color.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}
