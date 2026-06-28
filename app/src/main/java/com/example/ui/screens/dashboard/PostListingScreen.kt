package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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

private const val DRAFT_PREFS = "listing_draft"

@Composable
fun PostListingScreen(viewModel: RentalViewModel) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(DRAFT_PREFS, Context.MODE_PRIVATE) }

    var title by remember { mutableStateOf(prefs.getString("title", "") ?: "") }
    var description by remember { mutableStateOf(prefs.getString("description", "") ?: "") }
    var category by remember { mutableStateOf(prefs.getString("category", RentalCategory.IMMOBILIER.displayName) ?: RentalCategory.IMMOBILIER.displayName) }
    var priceStr by remember { mutableStateOf(prefs.getString("price", "") ?: "") }
    var city by remember { mutableStateOf(prefs.getString("city", "Libreville") ?: "Libreville") }
    var neighborhood by remember { mutableStateOf(prefs.getString("neighborhood", "") ?: "") }
    var ownerName by remember { mutableStateOf(prefs.getString("ownerName", "") ?: "") }
    var ownerPhone by remember { mutableStateOf(prefs.getString("ownerPhone", "") ?: "") }
    var imageUrls by remember { mutableStateOf(listOf(
        prefs.getString("img0", "") ?: "",
        prefs.getString("img1", "") ?: "",
        prefs.getString("img2", "") ?: "",
        prefs.getString("img3", "") ?: ""
    )) }
    var editingSlot by remember { mutableIntStateOf(-1) }
    var showUrlDialog by remember { mutableStateOf(false) }
    var isSuccessPost by remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current

    val currentCat = remember(category) {
        RentalCategory.entries.find { it.displayName == category } ?: RentalCategory.IMMOBILIER
    }

    // Dynamic category-specific fields
    var specValues by remember { mutableStateOf(mutableMapOf<String, String>()) }

    // Auto-save to draft
    LaunchedEffect(title, description, category, priceStr, city, neighborhood, ownerName, ownerPhone, imageUrls) {
        prefs.edit().apply {
            putString("title", title)
            putString("description", description)
            putString("category", category)
            putString("price", priceStr)
            putString("city", city)
            putString("neighborhood", neighborhood)
            putString("ownerName", ownerName)
            putString("ownerPhone", ownerPhone)
            imageUrls.forEachIndexed { i, url -> putString("img$i", url) }
            apply()
        }
    }

    fun clearDraft() {
        prefs.edit().clear().apply()
        title = ""; description = ""; priceStr = ""; neighborhood = ""; ownerName = ""; ownerPhone = ""
        imageUrls = listOf("", "", "", ""); specValues = mutableMapOf()
    }

    // Validation
    val titleError = title.isBlank() && title.isNotEmpty()
    val priceError = priceStr.toIntOrNull() == null && priceStr.isNotEmpty()
    val neighborhoodError = neighborhood.isBlank() && neighborhood.isNotEmpty()
    val descriptionError = description.isBlank() && description.isNotEmpty()

    val currentStep = when {
        title.isBlank() -> 1
        neighborhood.isBlank() -> 2
        description.isBlank() -> 3
        else -> 4
    }

    // Main photo URL (first non-empty)
    val primaryImageUrl = imageUrls.firstOrNull { it.isNotBlank() } ?: ""

    Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
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
                            onClick = { isSuccessPost = false; clearDraft(); viewModel.navigateTo("home") },
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

            // Animated Step Indicator
            item {
                AnimatedStepIndicator(
                    currentStep = currentStep,
                    totalSteps = 4,
                    stepLabels = listOf("Détails", "Localisation", "Description", "Publier")
                )
            }

            // Image Gallery 2×2
            item {
                SectionRow(icon = Icons.Rounded.PhotoLibrary, label = "PHOTOS", color = Color(0xFF4FC3F7))
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (i in 0..1) {
                        ImageSlot(
                            url = imageUrls[i],
                            index = i,
                            modifier = Modifier.weight(1f).aspectRatio(1.2f),
                            onClick = { editingSlot = i; showUrlDialog = true },
                            onDelete = {
                                imageUrls = imageUrls.toMutableList().also { it[i] = "" }
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (i in 2..3) {
                        ImageSlot(
                            url = imageUrls[i],
                            index = i,
                            modifier = Modifier.weight(1f).aspectRatio(1.2f),
                            onClick = { editingSlot = i; showUrlDialog = true },
                            onDelete = {
                                imageUrls = imageUrls.toMutableList().also { it[i] = "" }
                            }
                        )
                    }
                }
            }

            // Title
            item {
                SectionRow(icon = Icons.Rounded.Title, label = "TITRE", color = Color(0xFFFFB300))
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Ex: Villa F5, Toyota Hilux...", color = Color.White.copy(alpha = 0.3f)) },
                    modifier = Modifier.fillMaxWidth().testTag("post_title_input"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    isError = titleError,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = if (titleError) Color.Red else PrimaryGreen,
                        unfocusedBorderColor = if (titleError) Color.Red.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f),
                        focusedContainerColor = Color(0xFF162133),
                        unfocusedContainerColor = Color(0xFF162133)
                    )
                )
                InlineError(show = titleError, text = "Le titre est obligatoire")
                TipBox(text = "Un bon titre inclut le type de bien et le quartier")
            }

            // Category Grid
            item {
                SectionRow(icon = Icons.Rounded.Category, label = "CATÉGORIE", color = PrimaryGreen)
                Spacer(modifier = Modifier.height(8.dp))
                val entries = RentalCategory.entries
                val rows = entries.chunked(2)
                rows.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                    ) {
                        row.forEach { catEntry ->
                            val cat = catEntry.displayName
                            val isSelected = category == cat
                            AnimatedCategoryCard(
                                cat = catEntry,
                                isSelected = isSelected,
                                onClick = { category = cat; hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (row.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                // Category tip
                AnimatedVisibility(visible = currentCat.tips.isNotEmpty()) {
                    TipBox(text = currentCat.tips)
                }
            }

            // Dynamic category-specific fields
            item {
                if (currentCat.specs.isNotEmpty()) {
                    SectionRow(icon = Icons.Rounded.Tune, label = "DÉTAILS ${currentCat.displayName.uppercase()}", color = currentCat.color)
                    Spacer(modifier = Modifier.height(8.dp))
                    val specRows = currentCat.specs.chunked(2)
                    specRows.forEach { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                        ) {
                            row.forEach { spec ->
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(spec.label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Medium)
                                    OutlinedTextField(
                                        value = specValues[spec.key] ?: "",
                                        onValueChange = { v -> specValues = specValues.toMutableMap().also { it[spec.key] = v } },
                                        placeholder = { Text(spec.sampleValue, color = Color.White.copy(alpha = 0.3f), fontSize = 13.sp) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedBorderColor = currentCat.color,
                                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                            focusedContainerColor = Color(0xFF162133),
                                            unfocusedContainerColor = Color(0xFF162133)
                                        )
                                    )
                                }
                            }
                            if (row.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // Price with quick suggestions
            item {
                SectionRow(icon = Icons.Rounded.AttachMoney, label = "PRIX PAR JOUR (F CFA)", color = Color(0xFF66BB6A))
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it.filter { c -> c.isDigit() } },
                    placeholder = { Text("Ex: 15000", color = Color.White.copy(alpha = 0.3f)) },
                    modifier = Modifier.fillMaxWidth().testTag("post_price_input"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    isError = priceError,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = if (priceError) Color.Red else Color(0xFF66BB6A),
                        unfocusedBorderColor = if (priceError) Color.Red.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f),
                        focusedContainerColor = Color(0xFF162133),
                        unfocusedContainerColor = Color(0xFF162133)
                    )
                )
                InlineError(show = priceError, text = "Entrez un prix valide")
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(10000, 15000, 25000, 50000, 100000).forEach { preset ->
                        Surface(
                            onClick = { priceStr = preset.toString() },
                            color = if (priceStr == preset.toString()) Color(0xFF66BB6A).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, if (priceStr == preset.toString()) Color(0xFF66BB6A) else Color.White.copy(alpha = 0.1f))
                        ) {
                            Text(
                                text = "${preset / 1000}K",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (priceStr == preset.toString()) Color(0xFF66BB6A) else Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Location
            item {
                SectionRow(icon = Icons.Rounded.LocationOn, label = "LOCALISATION", color = Color(0xFF42A5F5))
                Spacer(modifier = Modifier.height(6.dp))
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
                            onValueChange = { neighborhood = it },
                            placeholder = { Text("Ex: Sablière...", color = Color.White.copy(alpha = 0.3f)) },
                            modifier = Modifier.fillMaxWidth().testTag("post_neighborhood_input"),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            isError = neighborhoodError,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = if (neighborhoodError) Color.Red else Color(0xFF42A5F5),
                                unfocusedBorderColor = if (neighborhoodError) Color.Red.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f),
                                focusedContainerColor = Color(0xFF162133),
                                unfocusedContainerColor = Color(0xFF162133)
                            )
                        )
                        InlineError(show = neighborhoodError, text = "Le quartier est obligatoire")
                    }
                }
            }

            // Description
            item {
                SectionRow(icon = Icons.Rounded.Description, label = "DESCRIPTION", color = Color(0xFFAB47BC))
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Commodités, état, superficie...", color = Color.White.copy(alpha = 0.3f)) },
                    modifier = Modifier.fillMaxWidth().height(120.dp).testTag("post_description_input"),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 5,
                    isError = descriptionError,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = if (descriptionError) Color.Red else Color(0xFFAB47BC),
                        unfocusedBorderColor = if (descriptionError) Color.Red.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f),
                        focusedContainerColor = Color(0xFF162133),
                        unfocusedContainerColor = Color(0xFF162133)
                    )
                )
                InlineError(show = descriptionError, text = "La description est obligatoire")
                TipBox(text = "Les annonces avec description détaillée reçoivent 3x plus de messages")
            }

            // Contact
            item {
                SectionRow(icon = Icons.Rounded.Person, label = "CONTACT", color = Color(0xFFEF5350))
                Spacer(modifier = Modifier.height(6.dp))
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
                                focusedBorderColor = Color(0xFFEF5350),
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
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFEF5350),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                focusedContainerColor = Color(0xFF162133),
                                unfocusedContainerColor = Color(0xFF162133)
                            )
                        )
                    }
                }
            }

            // Live Preview
            item {
                if (title.isNotBlank() || priceStr.isNotBlank()) {
                    SectionRow(icon = Icons.Rounded.Preview, label = "APERÇU", color = Color(0xFF78909C))
                    Spacer(modifier = Modifier.height(8.dp))
                    RentalCard(
                        item = com.example.data.model.RentalItem(
                            id = 9999,
                            title = title.ifBlank { "Titre de l'annonce" },
                            description = description.ifBlank { "Description..." },
                            category = category,
                            pricePerDay = priceStr.toIntOrNull() ?: 0,
                            city = city,
                            neighborhood = neighborhood.ifBlank { "Quartier" },
                            ownerName = ownerName.ifBlank { "Vous" },
                            ownerPhone = ownerPhone.ifBlank { "077000000" },
                            ownerRating = 5.0f,
                            imageUrl = primaryImageUrl.ifBlank { "https://images.unsplash.com/photo-1570129477492-45c003edd2be?auto=format&fit=crop&w=400&q=60" },
                            isBookmarked = false,
                            isVerified = false
                        ),
                        onSelect = {},
                        onBookmarkToggle = {},
                        onChat = {},
                        onBook = {}
                    )
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
                                imageUrl = primaryImageUrl
                            )
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            isSuccessPost = true
                        }
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

    // URL Dialog
    if (showUrlDialog) {
        var tempUrl by remember { mutableStateOf(imageUrls.getOrElse(editingSlot) { "" }) }
        AlertDialog(
            onDismissRequest = { showUrlDialog = false },
            containerColor = Color(0xFF162133),
            title = { Text("Image ${editingSlot + 1}", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = tempUrl,
                    onValueChange = { tempUrl = it },
                    placeholder = { Text("URL de l'image...", color = Color.White.copy(alpha = 0.3f)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        focusedContainerColor = Color(0xFF0D1B2A),
                        unfocusedContainerColor = Color(0xFF0D1B2A)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        imageUrls = imageUrls.toMutableList().also { if (editingSlot in 0..3) it[editingSlot] = tempUrl }
                        showUrlDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) { Text("OK", color = BrandNavy, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showUrlDialog = false }) { Text("Annuler", color = Color.White.copy(alpha = 0.6f)) }
            }
        )
    }
    }
}

@Composable
private fun SectionRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier.size(28.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        }
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color, letterSpacing = 1.sp)
    }
}

@Composable
private fun InlineError(show: Boolean, text: String) {
    AnimatedVisibility(visible = show) {
        Row(
            modifier = Modifier.padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.Error, contentDescription = null, tint = Color.Red, modifier = Modifier.size(14.dp))
            Text(text, color = Color.Red, fontSize = 12.sp)
        }
    }
}

@Composable
private fun TipBox(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.06f)),
        border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(14.dp))
            Text(text, color = PrimaryGreen.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ImageSlot(
    url: String,
    index: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
        border = BorderStroke(1.5.dp, if (url.isNotBlank()) PrimaryGreen.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.08f))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (url.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(url)
                        .crossfade(true)
                        .size(Size.ORIGINAL)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = "Image ${index + 1}",
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                    error = painterResource(android.R.drawable.ic_menu_close_clear_cancel)
                )
                // Delete button
                IconButton(
                    onClick = { onDelete() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(24.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = "Supprimer", tint = Color.White, modifier = Modifier.size(14.dp))
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.AddAPhoto, contentDescription = "Ajouter", tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(28.dp))
                        Text("+${index + 1}", color = Color.White.copy(alpha = 0.3f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedStepIndicator(
    currentStep: Int,
    totalSteps: Int,
    stepLabels: List<String>
) {
    val animatedProgress by animateFloatAsState(
        targetValue = currentStep.toFloat() / totalSteps.toFloat(),
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "step_progress"
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (i in 1..totalSteps) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val stepScale by animateFloatAsState(
                        targetValue = if (i == currentStep) 1.15f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "step_scale_$i"
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .scale(stepScale)
                            .clip(CircleShape)
                            .background(
                                when {
                                    i < currentStep -> PrimaryGreen
                                    i == currentStep -> PrimaryGreen
                                    else -> Color.White.copy(alpha = 0.1f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (i < currentStep) {
                            Icon(Icons.Rounded.Check, contentDescription = null, tint = BrandNavy, modifier = Modifier.size(18.dp))
                        } else {
                            Text(
                                text = "$i",
                                color = if (i == currentStep) BrandNavy else Color.White.copy(alpha = 0.5f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    if (i <= stepLabels.size) {
                        Text(
                            text = stepLabels[i - 1],
                            color = if (i <= currentStep) Color.White else Color.White.copy(alpha = 0.4f),
                            fontSize = 10.sp,
                            fontWeight = if (i == currentStep) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
                if (i < totalSteps) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .padding(horizontal = 4.dp)
                            .align(Alignment.CenterVertically)
                            .background(if (i < currentStep) PrimaryGreen else Color.White.copy(alpha = 0.1f))
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AnimatedCategoryCard(
    cat: RentalCategory,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.03f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cat_scale_${cat.name}"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) cat.color.copy(alpha = 0.2f) else Color(0xFF162133),
        animationSpec = tween(300),
        label = "cat_bg_${cat.name}"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) cat.color else Color.White.copy(alpha = 0.08f),
        animationSpec = tween(300),
        label = "cat_border_${cat.name}"
    )
    val iconBg by animateColorAsState(
        targetValue = if (isSelected) cat.color else cat.color.copy(alpha = 0.12f),
        animationSpec = tween(300),
        label = "cat_icon_${cat.name}"
    )
    val iconRotate by animateFloatAsState(
        targetValue = if (isSelected) 360f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "cat_rotate_${cat.name}"
    )

    Card(
        modifier = modifier
            .scale(scale)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.5.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = cat.icon,
                    contentDescription = cat.displayName,
                    tint = if (isSelected) Color.White else cat.color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                cat.displayName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) cat.color else Color.White.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Text(
                cat.description,
                fontSize = 8.sp,
                color = Color.White.copy(alpha = 0.35f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}
