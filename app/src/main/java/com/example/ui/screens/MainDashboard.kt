package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.graphics.RectangleShape
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Booking
import com.example.data.model.ChatMessage
import com.example.data.model.RentalItem
import com.example.ui.viewmodel.PaymentState
import com.example.ui.viewmodel.RentalViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MainDashboardView(viewModel: RentalViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedItem by viewModel.selectedItem.collectAsState()

    // Main Layout Scaffold with M3 Bottom Navigation
    Scaffold(
        topBar = {
            Surface(
                color = Color.Yellow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "VERSION DÉMO - AUCUN PAIEMENT RÉEL",
                    color = Color.Black,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        },
        bottomBar = {
            if (currentScreen != "details" && currentScreen != "chat") {
                DashboardBottomBar(
                    currentScreen = currentScreen,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BrandNavy)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "DashboardScreenTransition"
            ) { screen ->
                when (screen) {
                    "home" -> ExploreScreen(viewModel)
                    "bookings" -> BookingsScreen(viewModel)
                    "post_listing" -> PostListingScreen(viewModel)
                    "bookmarks" -> BookmarksScreen(viewModel)
                    "messages" -> InboxScreen(viewModel)
                    "profile" -> ProfileNavigator(viewModel = viewModel)
                    "details" -> selectedItem?.let { item ->
                        ItemDetailsScreen(
                            item = item,
                            viewModel = viewModel,
                            onBack = { viewModel.navigateTo("home") }
                        )
                    }
                    "chat" -> selectedItem?.let { item ->
                        ChatRoomScreen(
                            item = item,
                            viewModel = viewModel,
                            onBack = { viewModel.navigateTo("details") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardBottomBar(
    currentScreen: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = BrandNavy,
        tonalElevation = 8.dp,
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        NavigationBarItem(
            selected = currentScreen == "home" || currentScreen == "details",
            onClick = { onNavigate("home") },
            icon = { Icon(Icons.Rounded.Search, contentDescription = "Explorer") },
            label = { Text("Explorer", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryGreen,
                selectedTextColor = PrimaryGreen,
                unselectedIconColor = Color.White.copy(alpha = 0.45f),
                unselectedTextColor = Color.White.copy(alpha = 0.45f),
                indicatorColor = Color.White.copy(alpha = 0.12f)
            )
        )

        NavigationBarItem(
            selected = currentScreen == "post_listing",
            onClick = { onNavigate("post_listing") },
            icon = { Icon(Icons.Rounded.AddCircle, contentDescription = "Ajouter", tint = PrimaryGreen, modifier = Modifier.size(28.dp)) },
            label = { Text("Publier", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryGreen,
                selectedTextColor = PrimaryGreen,
                unselectedIconColor = Color.White.copy(alpha = 0.45f),
                unselectedTextColor = Color.White.copy(alpha = 0.45f),
                indicatorColor = Color.White.copy(alpha = 0.12f)
            )
        )

        NavigationBarItem(
            selected = currentScreen == "bookmarks",
            onClick = { onNavigate("bookmarks") },
            icon = { Icon(Icons.Rounded.FavoriteBorder, contentDescription = "Favoris") },
            label = { Text("Favoris", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryGreen,
                selectedTextColor = PrimaryGreen,
                unselectedIconColor = Color.White.copy(alpha = 0.45f),
                unselectedTextColor = Color.White.copy(alpha = 0.45f),
                indicatorColor = Color.White.copy(alpha = 0.12f)
            )
        )

        NavigationBarItem(
            selected = currentScreen == "messages",
            onClick = { onNavigate("messages") },
            icon = { Icon(Icons.Default.Email, contentDescription = "Messages") },
            label = { Text("Messages", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryGreen,
                selectedTextColor = PrimaryGreen,
                unselectedIconColor = Color.White.copy(alpha = 0.45f),
                unselectedTextColor = Color.White.copy(alpha = 0.45f),
                indicatorColor = Color.White.copy(alpha = 0.12f)
            )
        )

        NavigationBarItem(
            selected = currentScreen == "profile",
            onClick = { onNavigate("profile") },
            icon = { Icon(Icons.Rounded.Person, contentDescription = "Profil") },
            label = { Text("Profil", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryGreen,
                selectedTextColor = PrimaryGreen,
                unselectedIconColor = Color.White.copy(alpha = 0.45f),
                unselectedTextColor = Color.White.copy(alpha = 0.45f),
                indicatorColor = Color.White.copy(alpha = 0.12f)
            )
        )
    }
}

// ---------------- EXPLORE SCREEN ----------------

@Composable
fun ExploreScreen(viewModel: RentalViewModel) {
    val items by viewModel.filteredRentalItems.collectAsState()
    val rawItems by viewModel.rawRentalItems.collectAsState()
    val selectedCat by viewModel.selectedCategory.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedMaxPrice by viewModel.selectedMaxPrice.collectAsState()

    val hasActiveFilters = searchQuery.isNotEmpty() || selectedCat != "Tous" || selectedCity != "Tous" || selectedMaxPrice != 0

    var showPriceFilterDialog by remember { mutableStateOf(false) }
    var selectedItemForModal by remember { mutableStateOf<RentalItem?>(null) }
    var showBookingFromModal by remember { mutableStateOf<RentalItem?>(null) }

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
                Column {
                    Text(
                        "Bienvenue au Gabon 🇬🇦",
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

                // App icon mini badge with logout button to test onboarding
                IconButton(
                    onClick = { viewModel.restartOnboarding() },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF162133))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "Restart onboarding logo",
                        tint = PrimaryGreen
                    )
                }
            }
        }

        // Custom Search Bar & Filters Trigger Click
        item {
            Row(
                modifier = Modifier.fillMaxWidth().testTag("search_container"),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    placeholder = { Text("Quartier, villa, SUV...", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = "Rechercher", tint = Color.White.copy(alpha = 0.5f)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchQuery.value = "" }) {
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
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedContainerColor = Color(0xFF162133),
                        unfocusedContainerColor = Color(0xFF162133)
                    ),
                    singleLine = true
                )

                Button(
                    onClick = { showPriceFilterDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(54.dp).testTag("price_filter_button")
                ) {
                    Icon(Icons.Rounded.Settings, contentDescription = "Filtres de prix", tint = BrandNavy)
                }
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
                                    viewModel.selectedMaxPrice.value = 40000
                                } else {
                                    viewModel.searchQuery.value = tag
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
                                    viewModel.searchQuery.value = ""
                                    viewModel.selectedCategory.value = "Tous"
                                    viewModel.selectedCity.value = "Tous"
                                    viewModel.selectedMaxPrice.value = 0
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
                            .clickable { viewModel.selectedCity.value = city }
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
                    .height(140.dp),
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

        // Horizontal Category Tabs with Dynamic Counters and Icons
        item {
            Column(
                modifier = Modifier.fillMaxWidth().testTag("categories_section"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Rechercher par Catégorie",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                val categoriesWithIcons = listOf(
                    Triple("Tous", Icons.Rounded.Category, "Tous"),
                    Triple("Immobilier", Icons.Rounded.Home, "Immobilier"),
                    Triple("Véhicules", Icons.Rounded.DirectionsCar, "Véhicules"),
                    Triple("Équipements", Icons.Rounded.Build, "Équipements")
                )

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
                        
                        Card(
                            modifier = Modifier
                                .testTag("category_filter_$catName")
                                .clickable { viewModel.selectedCategory.value = catName },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) PrimaryGreen else Color(0xFF162133)
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) PrimaryGreen else Color.White.copy(alpha = 0.12f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = "Catégorie $label",
                                    tint = if (isSelected) BrandNavy else PrimaryGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) BrandNavy else Color.White
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) BrandNavy.copy(alpha = 0.15f) 
                                            else Color.White.copy(alpha = 0.08f)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = count.toString(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) BrandNavy else Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Grid/List elements containing rental listings
        if (items.isEmpty()) {
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
                            imageVector = Icons.Default.Info,
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
            items(items) { item ->
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
                viewModel.selectedMaxPrice.value = maxPrice
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
                    .height(180.dp)
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
                    // Category Chip sticker
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

                    // Bookmark heart button custom styled
                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .testTag("bookmark_toggle_button_${item.id}")
                    ) {
                        Icon(
                            imageVector = if (item.isBookmarked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = "Bookmark button toggle",
                            tint = if (item.isBookmarked) Color.Red else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
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
                            text = String.format(Locale.US, "%.1f", item.ownerRating),
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
                        fontWeight = FontWeight.Medium
                    )
                }

                Divider(color = Color.White.copy(alpha = 0.12f), thickness = 1.dp)

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
                            .height(260.dp)
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
                            lineHeight = 26.sp
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

                        Divider(color = Color.White.copy(alpha = 0.12f))

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

                        Divider(color = Color.White.copy(alpha = 0.12f))

                        // Description
                        Text(
                            text = "Description",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = item.description,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            lineHeight = 22.sp
                        )

                        Divider(color = Color.White.copy(alpha = 0.12f))

                        // Landlord
                        Text(
                            text = "Propriétaire",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

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
                                        color = Color.White
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
                                            text = item.ownerPhone,
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.5f)
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {
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

// ----------------- ITEM DETAILS SCREEN -----------------

@Composable
fun ItemDetailsScreen(
    item: RentalItem,
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    var showBookingDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Picture header with absolute overlay controls
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
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

                // Upper Overlay Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.9f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Retour",
                            tint = BrandNavy,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.toggleBookmark(item) },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.9f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (item.isBookmarked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = "Bookmark button toggle",
                            tint = if (item.isBookmarked) Color.Red else Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Details summary card
        item {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Category & verify label
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFFF1FAF3),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = item.category,
                            color = PrimaryGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    if (item.isVerified) {
                        Surface(
                            color = Color(0xFFEEEEEE),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(12.dp))
                                Text("Vérifié", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                            }
                        }
                    }
                }

                // Title
                Text(
                    text = item.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy,
                    lineHeight = 28.sp
                )

                // Location tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Text(
                        text = "${item.neighborhood}, ${item.city} — Gabon",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Daily Price Box
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFBFB)),
                    border = BorderStroke(1.dp, Color(0xFFEDEDED)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Prix par jour", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                            Text(formatPriceCfa(item.pricePerDay), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = BrandNavy)
                        }

                        Surface(
                            color = PrimaryGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                "Dispo Immédiatement",
                                color = BrandNavy,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Description Title and contents
                Text("Description du bien", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                Text(
                    text = item.description,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Justify
                )

                // Landlord contact row
                Text("Annonceur", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Avatar
                        Box(modifier = Modifier.size(52.dp)) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80")
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Landlord avatar photo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.ownerName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Rounded.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(13.dp))
                                Text("4.9/5", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                Box(modifier = Modifier.size(3.dp).background(Color.Gray, CircleShape))
                                Text("Gabonais", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                            }
                        }

                        // Message Owner direct trigger click
                        IconButton(
                            onClick = {
                                viewModel.openChatFor(item)
                                viewModel.navigateTo("chat")
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF0F0F0))
                        ) {
                            Icon(Icons.Default.Email, contentDescription = "Contacter par message", tint = BrandNavy)
                        }
                    }
                }

                // Fake map section represent localization
                Text("Géolocalisation du bien", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Display simple colored grid mockup map representing Libreville/Akanda
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawRect(color = Color(0xFFE9F5EC))
                            // Draw grid lines
                            val gridW = size.width / 5
                            val gridH = size.height / 3
                            for (i in 1..4) {
                                drawLine(Color.White, start = androidx.compose.ui.geometry.Offset(i * gridW, 0f), end = androidx.compose.ui.geometry.Offset(i * gridW, size.height), strokeWidth = 5f)
                            }
                            for (i in 1..2) {
                                drawLine(Color.White, start = androidx.compose.ui.geometry.Offset(0f, i * gridH), end = androidx.compose.ui.geometry.Offset(size.width, i * gridH), strokeWidth = 5f)
                            }
                            // draw Libreville river/beach line blue bounds
                            val river = Path().apply {
                                moveTo(0f, size.height * 0.8f)
                                quadraticTo(size.width / 2, size.height * 0.7f, size.width, size.height * 0.9f)
                                lineTo(size.width, size.height)
                                lineTo(0f, size.height)
                                close()
                            }
                            drawPath(river, color = Color(0xFFCCE4FF))
                        }

                        // Floating map tag
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BrandNavy,
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(14.dp))
                                Text("${item.neighborhood}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom CTA action button
                Button(
                    onClick = { showBookingDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("rent_now_button")
                ) {
                    Text("Louer maintenant", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }

    if (showBookingDialog) {
        BookingInteractiveDialog(
            item = item,
            viewModel = viewModel,
            onDismiss = { showBookingDialog = false }
        )
    }
}

// FORMAT PRICE MONETARY CONVENTION
fun formatPriceCfa(amount: Int): String {
    val formatter = NumberFormat.getInstance(Locale.FRANCE)
    return "${formatter.format(amount)} F"
}

// ------------------- BOOKING INTERACTIVE DIALOG -------------------

@Composable
fun BookingInteractiveDialog(
    item: RentalItem,
    viewModel: RentalViewModel,
    onDismiss: () -> Unit
) {
    var daysCount by remember { mutableStateOf(1) }
    var selectedMethod by remember { mutableStateOf("Airtel Money") }
    var phoneNumber by remember { mutableStateOf("") }
    var isPhoneError by remember { mutableStateOf(false) }

    val paymentState by viewModel.paymentState.collectAsState()

    Dialog(onDismissRequest = {
        if (paymentState !is PaymentState.Processing) {
            viewModel.resetPaymentState()
            onDismiss()
        }
    }) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            border = BorderStroke(1.dp, Color(0xFFEEEEEE))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (val state = paymentState) {
                    is PaymentState.Idle -> {
                        Text(
                            "Détails de Réservation",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )

                        Text(
                            item.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Divider()

                        // Days Selection Bar Selector
                        Text("Durée de location (en jours)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { if (daysCount > 1) daysCount-- },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color(0xFFEEEEEE))
                            ) {
                                Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BrandNavy, modifier = Modifier.padding(bottom = 2.dp))
                            }

                            Text(
                                "$daysCount Jours",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )

                            IconButton(
                                onClick = { daysCount++ },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color(0xFFEEEEEE))
                            ) {
                                Icon(Icons.Rounded.Add, contentDescription = "Increment", tint = BrandNavy)
                            }
                        }

                        // Total sum display box
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9F8)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Prix Total", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                                Text(
                                    formatPriceCfa(item.pricePerDay * daysCount),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BrandNavy
                                )
                            }
                        }

                        // Payment operators selection row (Airtel, Moov)
                        Text("Moyen de paiement gabonais", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Airtel
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (selectedMethod == "Airtel Money") Color(0xFFFEECEE) else Color.Transparent)
                                    .border(
                                        width = 2.dp,
                                        color = if (selectedMethod == "Airtel Money") BrandAirtel else Color.LightGray.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable { selectedMethod = "Airtel Money" }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.size(18.dp).clip(CircleShape).background(BrandAirtel),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("A", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text("Airtel", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                                }
                            }

                            // Moov
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (selectedMethod == "Moov Money") Color(0xFFE4F1FA) else Color.Transparent)
                                    .border(
                                        width = 2.dp,
                                        color = if (selectedMethod == "Moov Money") BrandMoov else Color.LightGray.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable { selectedMethod = "Moov Money" }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.size(18.dp).clip(CircleShape).background(BrandMoov),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("M", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text("Moov", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                                }
                            }
                        }

                        // Phone Number
                        Text("Votre numéro de téléphone d'argent", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = {
                                phoneNumber = it
                                isPhoneError = false
                            },
                            placeholder = { Text("Ex: 077123456", color = Color.LightGray, fontSize = 14.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            isError = isPhoneError,
                            modifier = Modifier.fillMaxWidth().testTag("payment_phone_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (selectedMethod == "Airtel Money") BrandAirtel else BrandMoov
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        if (isPhoneError) {
                            Text("Veuillez saisir un numéro de téléphone gabonais valide.", color = Color.Red, fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Actions CTA
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Annuler", color = Color.Gray)
                            }

                            Button(
                                onClick = {
                                    if (phoneNumber.trim().length >= 8) {
                                        viewModel.initiateBooking(
                                            rentalItem = item,
                                            days = daysCount,
                                            paymentMethod = selectedMethod,
                                            phoneInput = phoneNumber
                                        )
                                    } else {
                                        isPhoneError = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedMethod == "Airtel Money") BrandAirtel else BrandMoov
                                ),
                                modifier = Modifier
                                    .weight(1.5f)
                                    .testTag("confirm_booking_payment"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Confirmer", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    is PaymentState.AwaitingPin -> {
                        var pinCode by remember { mutableStateOf("") }
                        var pinError by remember { mutableStateOf(false) }

                        val totalCost = state.rentalItem.pricePerDay * state.days
                        val isAirtel = state.paymentMethod == "Airtel Money"
                        val brandColor = if (isAirtel) BrandAirtel else BrandMoov
                        val bgGradient = if (isAirtel) {
                            Brush.linearGradient(listOf(Color(0xFF8C0E0E), Color(0xFF1E0E0E)))
                        } else {
                            Brush.linearGradient(listOf(Color(0xFF0D5E73), Color(0xFF0A1526)))
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, brandColor.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A2A))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Carrier banner
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(bgGradient)
                                        .padding(vertical = 10.dp, horizontal = 14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(Color.White),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                if (isAirtel) "A" else "M",
                                                color = brandColor,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                        Text(
                                            if (isAirtel) "AIRTEL MONEY GABON" else "MOOV MONEY FLOOZ GABON",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                }

                                Text(
                                    "NOTIFICATION PUSH DIRECTE",
                                    fontSize = 11.sp,
                                    color = brandColor,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                )

                                Text(
                                    "Autorisez-vous LocAll Gabon à débiter votre compte de " + formatPriceCfa(totalCost) + " pour : " + state.rentalItem.title + " (" + state.days + " jours) ?",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.85f),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )

                                OutlinedTextField(
                                    value = pinCode,
                                    onValueChange = {
                                        if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                            pinCode = it
                                            pinError = false
                                        }
                                    },
                                    placeholder = {
                                        Text(
                                            "Saisir PIN (Ex: 1234)",
                                            color = Color.White.copy(alpha = 0.25f),
                                            fontSize = 13.sp
                                        )
                                    },
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    singleLine = true,
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        letterSpacing = 6.sp
                                    ),
                                    modifier = Modifier
                                        .width(220.dp)
                                        .testTag("ussd_pin_input"),
                                    isError = pinError,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = brandColor,
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                if (pinError) {
                                    Text(
                                        "Veuillez saisir un code PIN valide à 4 chiffres.",
                                        color = Color.Red,
                                        fontSize = 11.sp
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { viewModel.resetPaymentState() },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Rejeter", color = Color.White.copy(alpha = 0.5f))
                                    }

                                    Button(
                                        onClick = {
                                            if (pinCode.length == 4) {
                                                viewModel.confirmBookingPayment(
                                                    rentalItem = state.rentalItem,
                                                    days = state.days,
                                                    paymentMethod = state.paymentMethod,
                                                    phoneInput = state.phoneInput,
                                                    pinCode = pinCode
                                                )
                                            } else {
                                                pinError = true
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = brandColor),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1.5f).testTag("submit_ussd_pin")
                                    ) {
                                        Text("Confirmer le PIN", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    is PaymentState.Processing -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // Circular Progress Indicator corresponding to selected provider
                            CircularProgressIndicator(
                                color = if (selectedMethod == "Airtel Money") BrandAirtel else BrandMoov,
                                modifier = Modifier.size(54.dp)
                            )

                            Text(
                                "Sécurisation du Paiement",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )

                            Text(
                                state.status,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }

                    is PaymentState.Success -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "Booking success symbol",
                                tint = PrimaryGreen,
                                modifier = Modifier.size(72.dp)
                            )

                            Text(
                                "Réservation Réussie !",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )

                            Text(
                                "Votre paiement de ${formatPriceCfa(state.booking.totalPrice)} a été enregistré avec succès par ${state.booking.paymentMethod}. Retrouvez vos détails de location dans l'onglet 'Réservations'.",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Button(
                                onClick = {
                                    viewModel.resetPaymentState()
                                    viewModel.navigateTo("bookings")
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().testTag("close_success_dialog")
                            ) {
                                Text("Voir mes réservations", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------- BOOKINGS ARCHIVE SCREEN -----------------

@Composable
fun BookingsScreen(viewModel: RentalViewModel) {
    val bookings by viewModel.bookings.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Mes Réservations",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Divider(color = Color.White.copy(alpha = 0.12f))

        if (bookings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.35f),
                        modifier = Modifier.size(54.dp)
                    )
                    Text(
                        "Vous n'avez pas de réservations actives pour le moment.",
                        color = Color.White.copy(alpha = 0.60f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { viewModel.navigateTo("home") },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Explorer les biens", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(bookings) { booking ->
                    BookingItemCard(booking)
                }
            }
        }
    }
}

@Composable
fun BookingItemCard(booking: Booking) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFF0C2417),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Statut: Payé",
                        color = PrimaryGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).format(Date(booking.bookingTimestamp)),
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }

            Text(
                text = booking.rentalItemTitle,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Période", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                    Text("${booking.days} jours", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Paiement via ${booking.paymentMethod}", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                    Text(booking.paymentPhone, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }
            }

            Divider(color = Color.White.copy(alpha = 0.12f), thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Payé", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White.copy(alpha = 0.5f))
                Text(
                    formatPriceCfa(booking.totalPrice),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryGreen
                )
            }
        }
    }
}

// ----------------- FAVORITES BOOKMARKS SCREEN -----------------

@Composable
fun BookmarksScreen(viewModel: RentalViewModel) {
    val items by viewModel.bookmarkedItems.collectAsState()

    var selectedItemForModal by remember { mutableStateOf<RentalItem?>(null) }
    var showBookingFromModal by remember { mutableStateOf<RentalItem?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Mes Favoris",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Divider(color = Color.White.copy(alpha = 0.12f))

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FavoriteBorder,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.35f),
                        modifier = Modifier.size(54.dp)
                    )
                    Text(
                        "Vous n'avez pas encore enregistré de biens en favoris.",
                        color = Color.White.copy(alpha = 0.60f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items) { item ->
                    RentalCard(
                        item = item,
                        onSelect = {
                            selectedItemForModal = item
                        },
                        onBookmarkToggle = { viewModel.toggleBookmark(item) }
                    )
                }
            }
        }
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

// ------------------ INBOX MESSAGES SCREEN ------------------

@Composable
fun InboxScreen(viewModel: RentalViewModel) {
    val items by viewModel.rawRentalItems.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Messagerie Sécurisée",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Divider(color = Color.White.copy(alpha = 0.12f))

        // Simply show active chat rooms for existing seed items to provide easy test navigation
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val showItems = items.take(4) // display top 4 items to simulate discussion
            items(showItems) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.selectItem(item)
                            viewModel.openChatFor(item)
                            viewModel.navigateTo("chat")
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Landlord avatar mock
                        Box(modifier = Modifier.size(44.dp)) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80")
                                    .crossfade(true)
                                    .build(),
                                contentDescription = item.ownerName,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(item.ownerName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("En ligne", fontSize = 10.sp, color = PrimaryGreen, fontWeight = FontWeight.Bold)
                            }

                            Text(
                                "Parler de: ${item.title}",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.60f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.40f)
                        )
                    }
                }
            }
        }
    }
}

// ----------------- ACTIVE CHAT ROOM SCREEN -----------------

@Composable
fun ChatRoomScreen(
    item: RentalItem,
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val messages by viewModel.activeChatMessages.collectAsState()
    var userMessageText by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        // App header containing landlord profiles
        Surface(
            color = Color.White,
            tonalElevation = 4.dp,
            border = BorderStroke(1.dp, Color(0xFFEEEEEE))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Retour", tint = BrandNavy)
                }

                Box(modifier = Modifier.size(38.dp)) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80")
                            .crossfade(true)
                            .build(),
                        contentDescription = "Contact photo avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(item.ownerName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(modifier = Modifier.size(6.dp).background(PrimaryGreen, CircleShape))
                        Text("Propriétaire Vérifié", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // Messages list history
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            items(messages) { message ->
                val isMe = message.sender == "User"
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                ) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 280.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isMe) 16.dp else 2.dp,
                                    bottomEnd = if (isMe) 2.dp else 16.dp
                                )
                            )
                            .background(if (isMe) BrandNavy else Color.White)
                            .padding(14.dp)
                    ) {
                        Text(
                            text = message.messageText,
                            fontSize = 14.sp,
                            color = if (isMe) Color.White else Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isMe) "Vous" else item.ownerName,
                        fontSize = 10.sp,
                        color = Color.LightGray,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        // Write messaging bar bottom
        Surface(
            color = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = userMessageText,
                    onValueChange = { userMessageText = it },
                    placeholder = { Text("Écrire un message sécurisé...", color = Color.Gray, fontSize = 14.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_message_input"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = Color(0xFFECECEC),
                        focusedContainerColor = Color(0xFFFBFBFB),
                        unfocusedContainerColor = Color(0xFFFBFBFB)
                    ),
                    maxLines = 3,
                    singleLine = false
                )

                IconButton(
                    onClick = {
                        if (userMessageText.isNotBlank()) {
                            viewModel.sendChatMessage(item.id, userMessageText, item.ownerName)
                            userMessageText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(PrimaryGreen)
                        .testTag("send_chat_message_button")
                ) {
                    Icon(Icons.Rounded.Send, contentDescription = "Envoyer", tint = BrandNavy)
                }
            }
        }
    }
}

// ----------------- ADD POST LISTING FORM SCREEN -----------------

@Composable
fun PostListingScreen(viewModel: RentalViewModel) {
    // Form fields
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Immobilier") }
    var priceStr by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Libreville") }
    var neighborhood by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var ownerPhone by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    var isSuccessPost by remember { mutableStateOf(false) }
    var showErrorField by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Publier une annonce",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy
            )
            Text(
                "Mettez en location vos appartements, voitures ou matériels au Gabon.",
                fontSize = 13.sp,
                color = Color.Gray
            )
            Divider(color = Color(0xFFF1F1F1))
        }

        if (isSuccessPost) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE5FCEF)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Color(0xFF107C41), modifier = Modifier.size(48.dp))
                        Text("Annonce créée avec succès !", fontWeight = FontWeight.Bold, color = BrandNavy, fontSize = 16.sp)
                        Text(
                            "Votre annonce a été enregistrée localement dans la base de données Room. Elle apparaît désormais en tête de la liste d'exploration !",
                            color = Color.DarkGray,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )

                        Button(
                            onClick = {
                                isSuccessPost = false
                                viewModel.navigateTo("home")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Retour à l'exploration")
                        }
                    }
                }
            }
        } else {
            // TITLE
            item {
                Text("Titre de l'annonce", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Ex: Villa F5 à vendre ou louer...", fontSize = 13.sp, color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth().testTag("post_title_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // CATEGORY SELECTOR
            item {
                Text("Catégorie", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val cats = listOf("Immobilier", "Véhicules", "Équipements")
                    cats.forEach { cat ->
                        val isSelected = category == cat
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PrimaryGreen else Color(0xFFEBEBEB).copy(alpha = 0.5f))
                                .clickable { category = cat }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(cat, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                        }
                    }
                }
            }

            // PRICE
            item {
                Text("Prix de location par jour (en F CFA)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    placeholder = { Text("Ex: 15000", fontSize = 13.sp, color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth().testTag("post_price_input"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // LOCATION: City Picker & neighborhood
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Ville", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                        // Simple city box select
                        var expanded by remember { mutableStateOf(false) }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                                .clickable { expanded = !expanded }
                                .padding(horizontal = 14.dp, vertical = 14.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(city, fontSize = 14.sp, color = BrandNavy, fontWeight = FontWeight.Medium)
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                val cities = listOf("Libreville", "Port-Gentil", "Franceville", "Oyem", "Akanda")
                                cities.forEach { c ->
                                    DropdownMenuItem(
                                        text = { Text(c) },
                                        onClick = {
                                            city = c
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1.2f)) {
                        Text("Quartier", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                        OutlinedTextField(
                            value = neighborhood,
                            onValueChange = { neighborhood = it },
                            placeholder = { Text("Ex: Sablière...", fontSize = 13.sp, color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth().testTag("post_neighborhood_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }
                }
            }

            // DESCRIPTION
            item {
                Text("Description complète", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Spécifiez les commodités, Wi-Fi, climatisation...", fontSize = 13.sp, color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("post_description_input"),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 5,
                    singleLine = false
                )
            }

            // CONTACTS: Owner name & phone input to simulate verifiability
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Votre Nom", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                        OutlinedTextField(
                            value = ownerName,
                            onValueChange = { ownerName = it },
                            placeholder = { Text("Ex: Marc", fontSize = 13.sp, color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("N° Téléphone", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                        OutlinedTextField(
                            value = ownerPhone,
                            onValueChange = { ownerPhone = it },
                            placeholder = { Text("077...", fontSize = 13.sp, color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }
                }
            }

            // OPTIONAL IMAGE URL PATH
            item {
                Text("Lien Image (Optionnel)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    placeholder = { Text("Saisissez une URL d'image libre...", fontSize = 13.sp, color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            if (showErrorField) {
                item {
                    Text("Veuillez remplir les informations obligatoires (Titre, Prix, Quartier, Description).", color = Color.Red, fontSize = 12.sp)
                }
            }

            // Form Submit CTA button
            item {
                Button(
                    onClick = {
                        val price = priceStr.toIntOrNull()
                        if (title.isNotBlank() && price != null && neighborhood.isNotBlank() && description.isNotBlank()) {
                            viewModel.postNewListing(
                                title = title,
                                description = description,
                                category = category,
                                price = price,
                                city = city,
                                neighborhood = neighborhood,
                                ownerName = if (ownerName.isBlank()) "Anonyme Locall" else ownerName,
                                ownerPhone = if (ownerPhone.isBlank()) "077000000" else ownerPhone,
                                imageUrl = imageUrl
                            )
                            isSuccessPost = true
                            showErrorField = false
                            // Reset input
                            title = ""
                            description = ""
                            priceStr = ""
                            neighborhood = ""
                            imageUrl = ""
                        } else {
                            showErrorField = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("submit_post_button")
                ) {
                    Text("Publier l'annonce", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
