package com.example.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.graphics.RectangleShape
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.RentalItem
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.model.RentalCategory
import com.example.ui.viewmodel.RentalViewModel
import kotlinx.coroutines.delay

@Composable
fun ExploreScreen(viewModel: RentalViewModel) {
    val items by viewModel.filteredRentalItems.collectAsState()
    val rawItems by viewModel.rawRentalItems.collectAsState()
    val selectedCat by viewModel.selectedCategory.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedMaxPrice by viewModel.selectedMaxPrice.collectAsState()

    val hasActiveFilters = searchQuery.isNotEmpty() || selectedCat != "Tous" || selectedCity != "Tous" || selectedMaxPrice != 0

    var sortOption by remember { mutableStateOf(SortOption.RECENT) }
    var showPriceFilterDialog by remember { mutableStateOf(false) }
    var selectedItemForModal by remember { mutableStateOf<RentalItem?>(null) }
    var showBookingFromModal by remember { mutableStateOf<RentalItem?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    val isOwnerMode by viewModel.isOwnerMode.collectAsState()

    LaunchedEffect(Unit) { delay(1500); isLoading = false }
    LaunchedEffect(isRefreshing) { if (isRefreshing) { delay(1500); isRefreshing = false } }

    val sortedItems = items
    val displayItems = if (isOwnerMode) sortedItems.filter { it.ownerName == "Vous" || it.ownerName == "User" } else sortedItems

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Welcome Header & Options button
        item {
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Bienvenue au Gabon",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.60f)
                    )
                    Text(
                        "Trouvez votre bonheur !",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                UserAvatar(name = "Utilisateur", size = 36.dp)

                BadgedBox(
                    badge = {
                        val unreadCount by viewModel.unreadNotificationCount.collectAsState()
                        if (unreadCount > 0) {
                            Badge(containerColor = Color.Red, contentColor = Color.White) {
                                Text("$unreadCount", fontSize = 9.sp)
                            }
                        }
                    }
                ) {
                    IconButton(onClick = { viewModel.navigateTo("profile") }) {
                        Icon(Icons.Rounded.Notifications, "Notifications", tint = Color.White.copy(alpha = 0.7f))
                    }
                }
            }
        }

        // Custom Search Bar & Filters Trigger Click
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        Color.White.copy(alpha = 0.12f),
                        RoundedCornerShape(16.dp)
                    )
                    .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(16.dp))
                    .testTag("search_container"),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Quartier, villa, SUV...", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp) },
                    leadingIcon = { SmoothIcon(Icons.Rounded.Search, contentDescription = "Rechercher", tint = Color.White.copy(alpha = 0.5f), backgroundColor = Color.White.copy(alpha = 0.08f), modifier = Modifier.size(32.dp), iconSize = 18.dp) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Rounded.Clear, contentDescription = "Effacer la recherche", tint = Color.White.copy(alpha = 0.5f))
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("search_input_field"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White.copy(alpha = 0.18f),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.10f),
                        focusedContainerColor = Color.White.copy(alpha = 0.07f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                        cursorColor = PrimaryGreen
                    ),
                    singleLine = true
                )

                SmoothIconButton(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = "Filtres de prix",
                    onClick = { showPriceFilterDialog = true },
                    tint = BrandNavy,
                    backgroundColor = PrimaryGreen,
                    modifier = Modifier.size(54.dp).testTag("price_filter_button"),
                    iconSize = 22.dp
                )

                SortDropdown(
                    selected = sortOption,
                    onSelect = { sortOption = it; viewModel.setSortOption(it) }
                )
            }
        }

        // Popular search tags and quick reset
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Suggestions :",
                    color = Color.White.copy(alpha = 0.45f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                
                val popularTags = listOf("Sablière", "Prado", "Piscine", "Moins cher")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(popularTags) { tag ->
                        Surface(
                            onClick = {
                                if (tag == "Moins cher") {
                                    viewModel.setSelectedMaxPrice(40000)
                                } else {
                                    viewModel.setSearchQuery(tag)
                                }
                            },
                            color = Color.White.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                            modifier = Modifier.height(28.dp).testTag("popular_tag_$tag")
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tag,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    
                    if (hasActiveFilters) {
                        item {
                            Surface(
                                onClick = {
                                viewModel.setSearchQuery("")
                                viewModel.setSelectedCategory("Tous")
                                viewModel.setSelectedCity("Tous")
                                viewModel.setSelectedMaxPrice(0)
                                },
                                color = PrimaryGreen.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f)),
                                modifier = Modifier.height(28.dp).testTag("filters_reset_tag")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Clear,
                                        contentDescription = "Effacer les filtres",
                                        tint = PrimaryGreen,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Text(
                                        text = "Réinitialiser",
                                        color = PrimaryGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Horizontal Cities row
        item {
            val cities = listOf("Tous", "Libreville", "Port-Gentil", "Franceville", "Oyem", "Akanda")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().testTag("cities_filter_row")
            ) {
                items(cities) { city ->
                    val isSelected = selectedCity == city
                    Box(
                        modifier = Modifier
                            .testTag("city_chip_$city")
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) PrimaryGreen else Color(0xFF162133))
                            .border(
                                1.dp,
                                if (isSelected) Color.Transparent else Color.White.copy(alpha = 0.12f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.setSelectedCity(city) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = city,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) BrandNavy else Color.White.copy(alpha = 0.70f)
                        )
                    }
                }
            }
        }

        // Beautiful visual Hero card banner "Louez tout partout"
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2.5f),
                shape = RoundedCornerShape(22.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://images.unsplash.com/photo-1570129477492-45c003edd2be?auto=format&fit=crop&w=800&q=80")
                            .crossfade(true)
                            .build(),
                        contentDescription = "Belles locations au Gabon",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Overlay gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        BrandNavy.copy(alpha = 0.9f),
                                        BrandNavy.copy(alpha = 0.4f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            "Offres Vérifiées 🌟",
                            color = PrimaryGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Louez en direct\nen toute sécurité",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }

        // Annonce du jour Featured Section
        item {
            val featuredItem = items.firstOrNull()
            if (featuredItem != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SectionHeader(title = "Annonce du jour")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(2f)
                            .clickable { selectedItemForModal = featuredItem },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(featuredItem.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = featuredItem.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.85f)
                                            )
                                        )
                                    )
                            )
                            // Spotlight badge
                            Surface(
                                color = PrimaryGreen,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Star,
                                        contentDescription = null,
                                        tint = BrandNavy,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        "Spotlight",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandNavy
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = featuredItem.title,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.LocationOn,
                                        contentDescription = null,
                                        tint = PrimaryGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        "${featuredItem.neighborhood}, ${featuredItem.city}",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        formatPriceCfa(featuredItem.pricePerDay) + " / Jour",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PrimaryGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Horizontal Category Tabs with Dynamic Counters and Icons
        item {
            Column(
                modifier = Modifier.fillMaxWidth().testTag("categories_section"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SectionHeader(title = "Rechercher par Catégorie")

                val categoriesWithIcons = listOf(
                    Triple("Tous", Icons.Rounded.Apps, "Tous")
                ) + RentalCategory.entries.map { Triple(it.displayName, it.icon, it.displayName) }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("categories_row")
                ) {
                    items(categoriesWithIcons) { (catName, icon, label) ->
                        val isSelected = selectedCat == catName
                        val count = if (catName == "Tous") {
                            rawItems.size
                        } else {
                            rawItems.count { it.category.equals(catName, ignoreCase = true) }
                        }
                        
                        CategoryIcon(
                            icon = icon,
                            label = label,
                            count = count,
                            isSelected = isSelected,
                            onClick = { viewModel.setSelectedCategory(catName) },
                            modifier = Modifier.testTag("category_filter_$catName")
                        )
                    }
                }
            }
        }

        // Grid/List elements containing rental listings
        if (isLoading) {
            items(3) { SkeletonCard() }
        } else if (isRefreshing) {
            items(3) { SkeletonCard() }
        } else if (displayItems.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            "Aucun bien ne correspond à vos filtres.",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = {
                            isRefreshing = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Rafraîchir",
                            tint = PrimaryGreen
                        )
                    }
                }
            }
            items(displayItems) { item ->
                RentalCard(
                    item = item,
                    onSelect = {
                        selectedItemForModal = item
                    },
                    onBookmarkToggle = { viewModel.toggleBookmark(item) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Modal Price Filter Dialog
    if (showPriceFilterDialog) {
        PriceFilterDialog(
            currentMaxPrice = viewModel.selectedMaxPrice.collectAsState().value,
            onDismiss = { showPriceFilterDialog = false },
            onApply = { maxPrice ->
                viewModel.setSelectedMaxPrice(maxPrice)
                showPriceFilterDialog = false
            }
        )
    }

    // Beautiful Details Modal Dialog
    if (selectedItemForModal != null) {
        RentalDetailModalDialog(
            item = selectedItemForModal!!,
            viewModel = viewModel,
            onDismissRequest = { selectedItemForModal = null },
            onBookNow = {
                showBookingFromModal = selectedItemForModal
                selectedItemForModal = null
            }
        )
    }

    // Modal Payment / Booking Dialog
    if (showBookingFromModal != null) {
        BookingInteractiveDialog(
            item = showBookingFromModal!!,
            viewModel = viewModel,
            onDismiss = { showBookingFromModal = null }
        )
    }
}

@Composable
fun RentalCard(
    item: RentalItem,
    onSelect: () -> Unit,
    onBookmarkToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("rental_card_${item.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            ) {
                // Item photo image
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Top Floating row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Chip + Badge stickers
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = item.category,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        // Badge: Nouveau (for items with id >= 10)
                        if (item.id >= 10) {
                            Surface(
                                color = Color(0xFF4FC3F7).copy(alpha = 0.9f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Nouveau",
                                    color = BrandNavy,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                        // Badge: Populaire (for items with rating >= 4.8)
                        if (item.ownerRating >= 4.8f) {
                            Surface(
                                color = Color(0xFFFFB300).copy(alpha = 0.9f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Populaire",
                                    color = BrandNavy,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    // Bookmark heart button custom styled
                    AnimatedHeartButton(
                        isFavorite = item.isBookmarked,
                        onClick = onBookmarkToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .testTag("bookmark_toggle_button_${item.id}")
                    )
                }

                // Verified sticker tag overlay on picture
                if (item.isVerified) {
                    Surface(
                        color = PrimaryGreen,
                        shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = BrandNavy,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                "PROFIL VÉRIFIÉ",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )
                        }
                    }
                }
            }

            // Description and details block bottom
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = String.format(java.util.Locale.US, "%.1f", item.ownerRating),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                // Neighborhood and city row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${item.neighborhood}, ${item.city}",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.12f), thickness = 1.dp)

                // Price display in CFA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = formatPriceCfa(item.pricePerDay),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryGreen
                        )
                        Text(
                            text = " / Jour",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Louer",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                }
            }
        }
    }
}

@Composable
fun RentalDetailModalDialog(
    item: RentalItem,
    viewModel: RentalViewModel,
    onDismissRequest: () -> Unit,
    onBookNow: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .background(BrandNavy)
                .testTag("rental_detail_modal"),
            shape = RectangleShape,
            colors = CardDefaults.cardColors(containerColor = BrandNavy)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BrandNavy)
            ) {
                // Header with photo and buttons
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.8f)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(item.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = item.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Top Gradient Overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.5f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        // Buttons upper row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onDismissRequest,
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                    .testTag("close_detail_modal")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Fermer",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = { viewModel.toggleBookmark(item) },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                    .testTag("bookmark_toggle_modal_button")
                            ) {
                                Icon(
                                    imageVector = if (item.isBookmarked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                    contentDescription = "Ajouter aux favoris",
                                    tint = if (item.isBookmarked) Color.Red else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Content
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Category and validation status
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = PrimaryGreen,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = item.category,
                                    color = BrandNavy,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            if (item.isVerified) {
                                Surface(
                                    color = Color.White.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.CheckCircle,
                                            contentDescription = "Vérifié",
                                            tint = PrimaryGreen,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = "Profil Vérifié",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        // Title
                        Text(
                            text = item.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 26.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Location
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.LocationOn,
                                contentDescription = "Localisation",
                                tint = PrimaryGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${item.neighborhood}, ${item.city} — Gabon",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

                        // Price
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Tarif journalier",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = formatPriceCfa(item.pricePerDay),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PrimaryGreen
                                )
                            }

                            Surface(
                                color = PrimaryGreen.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "Disponible",
                                    color = PrimaryGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

                        // Description
                        SectionHeader(title = "Description")
                        Text(
                            text = item.description,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            lineHeight = 22.sp
                        )

                        HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

                        // Landlord
                        SectionHeader(title = "Propriétaire")

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(modifier = Modifier.size(46.dp)) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80")
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Avatar propriétaire",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.ownerName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Star,
                                            contentDescription = "Note",
                                            tint = Color(0xFFFFB300),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = "${item.ownerRating}/5",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(3.dp)
                                                .background(Color.White.copy(alpha = 0.4f), CircleShape)
                                        )
                                        Text(
                                            text = maskPhoneNumber(item.ownerPhone),
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.5f)
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        viewModel.selectItem(item)
                                        viewModel.openChatFor(item)
                                        viewModel.navigateTo("chat")
                                        onDismissRequest()
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.08f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Chat,
                                        contentDescription = "Discuter",
                                        tint = PrimaryGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Book now button inside modal!
                        Button(
                            onClick = onBookNow,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("modal_book_now_button")
                        ) {
                            Text(
                                text = "Louer maintenant",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun PriceFilterDialog(
    currentMaxPrice: Int,
    onDismiss: () -> Unit,
    onApply: (Int) -> Unit
) {
    var filterValue by remember { mutableStateOf(currentMaxPrice) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            border = BorderStroke(1.dp, Color(0xFFEEEEEE))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Filtrer par Prix Max",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )

                Text(
                    text = if (filterValue == 0) "Aucune limite de prix" else "Maximum: " + formatPriceCfa(filterValue) + " / Jour",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (filterValue == 0) Color.Gray else PrimaryGreen
                )

                // Slider
                Slider(
                    value = if (filterValue == 0) 160000f else filterValue.toFloat(),
                    onValueChange = {
                        val v = it.toInt()
                        filterValue = if (v >= 15500) v else 0 // snap to 0 at bottom
                    },
                    valueRange = 10000f..150000f,
                    colors = SliderDefaults.colors(
                        thumbColor = BrandNavy,
                        activeTrackColor = PrimaryGreen,
                        inactiveTrackColor = Color.LightGray.copy(alpha = 0.3f)
                    )
                )

                // Choices quick selections
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presets = listOf(30000, 60000, 100000)
                    presets.forEach { preset ->
                        OutlinedButton(
                            onClick = { filterValue = preset },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, if (filterValue == preset) PrimaryGreen else Color.LightGray)
                        ) {
                            Text(
                                preset.toString().substring(0, preset.toString().length-3) + "K",
                                fontSize = 11.sp,
                                color = BrandNavy,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // CTA Rows
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { onApply(0) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Reset", color = Color.Gray, fontSize = 14.sp)
                    }

                    Button(
                        onClick = { onApply(filterValue) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("apply_filter_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Appliquer", color = Color.White, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
