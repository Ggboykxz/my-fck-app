package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.model.RentalCategory
import com.example.ui.viewmodel.RentalViewModel

@Composable
fun PostListingScreen(viewModel: RentalViewModel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(RentalCategory.IMMOBILIER.displayName) }
    var priceStr by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Libreville") }
    var neighborhood by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var ownerPhone by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var photoTaken by remember { mutableStateOf(false) }
    var isSuccessPost by remember { mutableStateOf(false) }
    var showErrorField by remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text("Publier une annonce", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Mettez en location vos biens au Gabon.", fontSize = 14.sp, color = Color.White.copy(alpha = 0.5f))
        }

        if (isSuccessPost) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(PrimaryGreen.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = "Annonce publiée", tint = PrimaryGreen, modifier = Modifier.size(36.dp))
                        }
                        Text("Annonce publiée !", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 20.sp)
                        Text(
                            "Votre annonce est maintenant visible dans l'exploration.",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { isSuccessPost = false; viewModel.navigateTo("home") },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text("Voir dans l'exploration", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        } else {
            // Step Indicator
            item {
                val currentStep = when {
                    title.isBlank() -> 1
                    neighborhood.isBlank() -> 2
                    description.isBlank() -> 3
                    else -> 4
                }
                StepIndicator(
                    currentStep = currentStep,
                    totalSteps = 4,
                    stepLabels = listOf("Détails", "Localisation", "Description", "Publier")
                )
            }

            // Image Preview
            item {
                if (imageUrl.isNotBlank()) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUrl)
                                .crossfade(true)
                                .size(Size.ORIGINAL)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .build(),
                            contentDescription = "Aperçu",
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                            error = painterResource(android.R.drawable.ic_menu_close_clear_cancel)
                        )
                    }
                } else {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                        border = BorderStroke(2.dp, Color.White.copy(alpha = 0.08f))
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                SmoothIcon(icon = Icons.Rounded.AddAPhoto, tint = Color.White.copy(alpha = 0.3f), backgroundColor = Color.White.copy(alpha = 0.06f), size = 56.dp, iconSize = 40.dp)
                                Text("Ajouter une image (URL)", color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // Title
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("TITRE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it; showErrorField = false },
                        placeholder = { Text("Ex: Villa F5, Toyota Hilux...", color = Color.White.copy(alpha = 0.3f)) },
                        modifier = Modifier.fillMaxWidth().testTag("post_title_input"),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                            focusedContainerColor = Color(0xFF162133),
                            unfocusedContainerColor = Color(0xFF162133)
                        )
                    )
                }
            }

            // Category
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("CATÉGORIE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.heightIn(max = 400.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(RentalCategory.entries.toList(), key = { it.name }) { catEntry ->
                            val cat = catEntry.displayName
                            val isSelected = category == cat
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { category = cat },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) catEntry.color.copy(alpha = 0.2f) else Color(0xFF162133)
                                ),
                                border = BorderStroke(1.5.dp, if (isSelected) catEntry.color else Color.White.copy(alpha = 0.08f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) catEntry.color else catEntry.color.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = catEntry.icon,
                                            contentDescription = cat,
                                            tint = if (isSelected) Color.White else catEntry.color,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Text(
                                        cat,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) catEntry.color else Color.White.copy(alpha = 0.6f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Price
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("PRIX PAR JOUR (F CFA)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it; showErrorField = false },
                        placeholder = { Text("Ex: 15000", color = Color.White.copy(alpha = 0.3f)) },
                        modifier = Modifier.fillMaxWidth().testTag("post_price_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                            focusedContainerColor = Color(0xFF162133),
                            unfocusedContainerColor = Color(0xFF162133)
                        )
                    )
                }
            }

            // Location
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("LOCALISATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Ville", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Medium)
                            var expanded by remember { mutableStateOf(false) }
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { expanded = true },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(city, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Medium)
                                    Icon(Icons.Rounded.ArrowDropDown, contentDescription = null, tint = Color.White.copy(alpha = 0.5f))
                                }
                            }
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(Color(0xFF162133))) {
                                listOf("Libreville", "Port-Gentil", "Franceville", "Oyem", "Akanda").forEach { c ->
                                    DropdownMenuItem(
                                        text = { Text(c, color = if (c == city) PrimaryGreen else Color.White) },
                                        onClick = { city = c; expanded = false }
                                    )
                                }
                            }
                        }
                        Column(modifier = Modifier.weight(1.2f)) {
                            Text("Quartier", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Medium)
                            OutlinedTextField(
                                value = neighborhood,
                                onValueChange = { neighborhood = it; showErrorField = false },
                                placeholder = { Text("Ex: Sablière...", color = Color.White.copy(alpha = 0.3f)) },
                                modifier = Modifier.fillMaxWidth().testTag("post_neighborhood_input"),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = PrimaryGreen,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                    focusedContainerColor = Color(0xFF162133),
                                    unfocusedContainerColor = Color(0xFF162133)
                                )
                            )
                        }
                    }
                }
            }

            // Description
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("DESCRIPTION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it; showErrorField = false },
                        placeholder = { Text("Commodités, état, superficie...", color = Color.White.copy(alpha = 0.3f)) },
                        modifier = Modifier.fillMaxWidth().height(120.dp).testTag("post_description_input"),
                        shape = RoundedCornerShape(14.dp),
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                            focusedContainerColor = Color(0xFF162133),
                            unfocusedContainerColor = Color(0xFF162133)
                        )
                    )
                }
            }

            // Contact
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("CONTACT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Votre nom", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Medium)
                            OutlinedTextField(
                                value = ownerName,
                                onValueChange = { ownerName = it },
                                placeholder = { Text("Marc", color = Color.White.copy(alpha = 0.3f)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = PrimaryGreen,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                    focusedContainerColor = Color(0xFF162133),
                                    unfocusedContainerColor = Color(0xFF162133)
                                )
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Téléphone", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Medium)
                            OutlinedTextField(
                                value = ownerPhone,
                                onValueChange = { ownerPhone = it },
                                placeholder = { Text("077...", color = Color.White.copy(alpha = 0.3f)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = PrimaryGreen,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                    focusedContainerColor = Color(0xFF162133),
                                    unfocusedContainerColor = Color(0xFF162133)
                                )
                            )
                        }
                    }
                }
            }

            // Photo capture simulation
            item {
                // Simulated photo capture
                Card(
                    modifier = Modifier.fillMaxWidth().height(120.dp).clickable { photoTaken = !photoTaken },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = if (photoTaken) PrimaryGreen.copy(alpha = 0.08f) else Color(0xFF162133)),
                    border = BorderStroke(1.dp, if (photoTaken) PrimaryGreen else Color.White.copy(alpha = 0.1f))
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (photoTaken) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Rounded.CheckCircle, contentDescription = "Photo capturée", tint = PrimaryGreen, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Photo capturée !", color = PrimaryGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Rounded.AddAPhoto, contentDescription = "Prendre une photo", tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Appuyez pour simuler une prise de photo", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Image URL
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("IMAGE (OPTIONNEL)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        placeholder = { Text("URL de l'image...", color = Color.White.copy(alpha = 0.3f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                            focusedContainerColor = Color(0xFF162133),
                            unfocusedContainerColor = Color(0xFF162133)
                        )
                    )
                }
            }

            if (showErrorField) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f)), shape = RoundedCornerShape(12.dp)) {
                        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Warning, contentDescription = "Erreur", tint = Color.Red, modifier = Modifier.size(18.dp))
                            Text("Remplissez les champs obligatoires (Titre, Prix, Quartier, Description).", color = Color.Red, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Submit
            item {
                Button(
                    onClick = {
                        val price = priceStr.toIntOrNull()
                        if (title.isNotBlank() && price != null && neighborhood.isNotBlank() && description.isNotBlank()) {
                            viewModel.postNewListing(
                                title = title, description = description, category = category,
                                price = price, city = city, neighborhood = neighborhood,
                                ownerName = if (ownerName.isBlank()) "Anonyme" else ownerName,
                                ownerPhone = if (ownerPhone.isBlank()) "077000000" else ownerPhone,
                                imageUrl = imageUrl
                            )
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            isSuccessPost = true; showErrorField = false
                            title = ""; description = ""; priceStr = ""; neighborhood = ""; imageUrl = ""
                        } else { showErrorField = true }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(56.dp).testTag("submit_post_button")
                ) {
                    Icon(Icons.Rounded.Publish, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Publier l'annonce", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(48.dp)) }
    }
}
