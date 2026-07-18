package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NeighborhoodReview
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel

@Composable
fun NeighborhoodReviewsScreen(viewModel: RentalViewModel, onBack: () -> Unit) {
    val selectedCity by viewModel.neighborhoodReviewsCity.collectAsState()
    val reviews by viewModel.neighborhoodReviews.collectAsState()
    var showWriteReview by remember { mutableStateOf(false) }
    var reviewNeighborhood by remember { mutableStateOf("") }
    var reviewComment by remember { mutableStateOf("") }
    var safetyRating by remember { mutableIntStateOf(3) }
    var noiseRating by remember { mutableIntStateOf(3) }
    var accessibilityRating by remember { mutableIntStateOf(3) }

    val cities = listOf("Libreville", "Port-Gentil", "Franceville", "Oyem", "Lambaréné", "Mouila")
    val neighborhoods = listOf(
        Triple("La Sablière", 4.5f, 4.2f),
        Triple("Batterie IV", 4.3f, 4.0f),
        Triple("Nkembo", 4.1f, 3.8f),
        Triple("Nzeng-Ayong", 4.4f, 4.3f),
        Triple("Oloumi", 4.0f, 3.5f),
        Triple("Akanda", 4.2f, 4.1f)
    )

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
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Avis du Quartier", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            cities.forEach { city ->
                FilterChip(
                    selected = selectedCity == city,
                    onClick = { viewModel.setNeighborhoodCity(city) },
                    label = { Text(city, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryGreen.copy(alpha = 0.2f),
                        selectedLabelColor = PrimaryGreen,
                        containerColor = Color(0xFF162133),
                        labelColor = Color.White.copy(alpha = 0.6f)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = Color.White.copy(alpha = 0.12f),
                        selectedBorderColor = PrimaryGreen.copy(alpha = 0.4f),
                        enabled = true,
                        selected = selectedCity == city
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text("Top quartiers à $selectedCity", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(neighborhoods, key = { it.first }) { (name, safety, accessibility) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(selectedCity, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                Icon(Icons.Rounded.Star, contentDescription = "Étoile", tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                                Text(String.format("%.1f", safety), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val ratings = listOf("Sécurité" to safety, "Calme" to (5f - (safety - 2f).coerceIn(0f, 5f)), "Accessibilité" to accessibility)
                        ratings.forEach { (label, value) ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(label, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, modifier = Modifier.width(90.dp))
                                Box(
                                    modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)).background(Color.White.copy(alpha = 0.1f))
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(value / 5f).fillMaxHeight().clip(RoundedCornerShape(3.dp)).background(PrimaryGreen)
                                    )
                                }
                                Text(String.format("%.1f", value), color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp, modifier = Modifier.width(30.dp))
                            }
                        }
                    }
                }
            }

            if (reviews.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Avis récents", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(reviews, key = { it.id }) { review ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(review.neighborhood, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    repeat(review.safetyRating) {
                                        Icon(Icons.Rounded.Star, contentDescription = "Étoile", tint = Color(0xFFFFB300), modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                            Text(review.comment, fontSize = 12.sp, color = Color.White.copy(alpha = 0.65f), lineHeight = 18.sp)
                        }
                    }
                }
            }
        }

        Button(
            onClick = { showWriteReview = true },
            modifier = Modifier.fillMaxWidth().height(48.dp).padding(bottom = 12.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
        ) {
            Icon(Icons.Rounded.RateReview, contentDescription = "Écrire un avis", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Écrire un avis", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandNavy)
        }
    }

    if (showWriteReview) {
        AlertDialog(
            onDismissRequest = { showWriteReview = false },
            containerColor = Color(0xFF162133),
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(alpha = 0.8f),
            title = { Text("Écrire un avis", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = reviewNeighborhood,
                        onValueChange = { reviewNeighborhood = it },
                        placeholder = { Text("Nom du quartier", color = Color.White.copy(alpha = 0.4f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedContainerColor = Color(0xFF0D1B2A),
                            unfocusedContainerColor = Color(0xFF0D1B2A)
                        )
                    )

                    listOf("Sécurité" to safetyRating, "Calme" to noiseRating, "Accessibilité" to accessibilityRating).forEachIndexed { index, (label, rating) ->
                        Column {
                            Text("$label: $rating/5", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Slider(
                                value = rating.toFloat(),
                                onValueChange = { newValue ->
                                    when (index) {
                                        0 -> safetyRating = newValue.toInt()
                                        1 -> noiseRating = newValue.toInt()
                                        2 -> accessibilityRating = newValue.toInt()
                                    }
                                },
                                valueRange = 1f..5f,
                                steps = 3,
                                colors = SliderDefaults.colors(
                                    thumbColor = PrimaryGreen,
                                    activeTrackColor = PrimaryGreen,
                                    inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        placeholder = { Text("Votre commentaire", color = Color.White.copy(alpha = 0.4f)) },
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedContainerColor = Color(0xFF0D1B2A),
                            unfocusedContainerColor = Color(0xFF0D1B2A)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (reviewNeighborhood.isNotBlank() && reviewComment.isNotBlank()) {
                            viewModel.insertNeighborhoodReview(
                                NeighborhoodReview(
                                    neighborhood = reviewNeighborhood,
                                    city = selectedCity,
                                    userId = 1,
                                    safetyRating = safetyRating,
                                    noiseRating = noiseRating,
                                    accessibilityRating = accessibilityRating,
                                    comment = reviewComment
                                )
                            )
                            showWriteReview = false
                            reviewNeighborhood = ""
                            reviewComment = ""
                            safetyRating = 3
                            noiseRating = 3
                            accessibilityRating = 3
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Publier", fontWeight = FontWeight.Bold, color = BrandNavy)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWriteReview = false }) {
                    Text("Annuler", color = Color.White.copy(alpha = 0.6f))
                }
            }
        )
    }
}
