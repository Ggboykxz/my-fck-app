package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import androidx.compose.ui.geometry.Offset
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
import com.example.ui.viewmodel.Screen
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import kotlinx.coroutines.delay

@Composable
fun MainDashboardView(viewModel: RentalViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedItem by viewModel.selectedItem.collectAsState()
    val unreadCount by viewModel.unreadMessageCount.collectAsState()

    // Main Layout Scaffold with M3 Bottom Navigation
    Scaffold(
        bottomBar = {
            if (currentScreen !is Screen.Details && currentScreen !is Screen.Chat) {
                DashboardBottomBar(
                    currentScreen = currentScreen,
                    onNavigate = { screen -> viewModel.navigateTo(screen) },
                    unreadCount = unreadCount
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
                    if (targetState is Screen.Details || targetState is Screen.Chat) {
                        slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                    } else if (targetState is Screen.Home) {
                        slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                    } else {
                        fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                    }
                },
                label = "DashboardScreenTransition"
            ) { screen ->
                when (screen) {
                    is Screen.Home -> ExploreScreen(viewModel)
                    is Screen.Bookings -> BookingsScreen(viewModel)
                    is Screen.PostListing -> PostListingScreen(viewModel)
                    is Screen.Bookmarks -> BookmarksScreen(viewModel)
                    is Screen.Messages -> InboxScreen(viewModel)
                    is Screen.Profile -> ProfileNavigator(viewModel = viewModel)
                    is Screen.Details -> {
                        val item = selectedItem
                        if (item != null) {
                            ItemDetailsScreen(
                                item = item,
                                viewModel = viewModel,
                                onBack = { viewModel.navigateTo("home") }
                            )
                        } else {
                            ExploreScreen(viewModel)
                        }
                    }
                    is Screen.Chat -> {
                        val item = selectedItem
                        if (item != null) {
                            ChatRoomScreen(
                                item = item,
                                viewModel = viewModel,
                                onBack = { viewModel.navigateTo("home") }
                            )
                        } else {
                            ExploreScreen(viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardBottomBar(
    currentScreen: Screen,
    onNavigate: (String) -> Unit,
    unreadCount: Int = 0
) {
    NavigationBar(
        containerColor = BrandNavy,
        tonalElevation = 8.dp,
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        NavigationBarItem(
            selected = currentScreen is Screen.Home || currentScreen is Screen.Details,
            onClick = { onNavigate("home") },
            icon = {
                val isSel = currentScreen is Screen.Home || currentScreen is Screen.Details
                SmoothIcon(Icons.Rounded.Search, contentDescription = "Explorer", tint = if (isSel) BrandNavy else Color.White.copy(alpha = 0.45f), backgroundColor = if (isSel) PrimaryGreen else Color.White.copy(alpha = 0.08f), modifier = Modifier.size(32.dp), iconSize = 18.dp)
            },
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
            selected = currentScreen is Screen.PostListing,
            onClick = { onNavigate("post_listing") },
            icon = {
                val isSel = currentScreen is Screen.PostListing
                SmoothIcon(Icons.Rounded.AddCircle, contentDescription = "Ajouter", tint = if (isSel) BrandNavy else Color.White.copy(alpha = 0.45f), backgroundColor = if (isSel) PrimaryGreen else Color.White.copy(alpha = 0.08f), modifier = Modifier.size(32.dp), iconSize = 18.dp)
            },
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
            selected = currentScreen is Screen.Bookmarks,
            onClick = { onNavigate("bookmarks") },
            icon = {
                val isSel = currentScreen is Screen.Bookmarks
                SmoothIcon(Icons.Rounded.FavoriteBorder, contentDescription = "Favoris", tint = if (isSel) BrandNavy else Color.White.copy(alpha = 0.45f), backgroundColor = if (isSel) PrimaryGreen else Color.White.copy(alpha = 0.08f), modifier = Modifier.size(32.dp), iconSize = 18.dp)
            },
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
            selected = currentScreen is Screen.Messages,
            onClick = { onNavigate("messages") },
            icon = {
                val isSel = currentScreen is Screen.Messages
                BadgedBox(
                    badge = {
                        if (unreadCount > 0) {
                            Badge(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ) {
                                Text("$unreadCount", fontSize = 9.sp)
                            }
                        }
                    }
                ) {
                    SmoothIcon(Icons.Default.Email, contentDescription = "Messages", tint = if (isSel) BrandNavy else Color.White.copy(alpha = 0.45f), backgroundColor = if (isSel) PrimaryGreen else Color.White.copy(alpha = 0.08f), modifier = Modifier.size(32.dp), iconSize = 18.dp)
                }
            },
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
            selected = currentScreen is Screen.Profile,
            onClick = { onNavigate("profile") },
            icon = {
                val isSel = currentScreen is Screen.Profile
                SmoothIcon(Icons.Rounded.Person, contentDescription = "Profil", tint = if (isSel) BrandNavy else Color.White.copy(alpha = 0.45f), backgroundColor = if (isSel) PrimaryGreen else Color.White.copy(alpha = 0.08f), modifier = Modifier.size(32.dp), iconSize = 18.dp)
            },
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
                Column {
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
                    Triple("Tous", Icons.Rounded.Apps, "Tous"),
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

// ----------------- ITEM DETAILS SCREEN -----------------

@Composable
fun ItemDetailsScreen(
    item: RentalItem,
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showBookingDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandNavy)
    ) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // Picture header with horizontal pager gallery
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
            ) {
                val galleryImages = remember(item.imageUrl) {
                    listOfNotNull(item.imageUrl) + listOf(
                        item.imageUrl?.replace("w=800", "w=801"),
                        item.imageUrl?.replace("w=800", "w=802")
                    ).filterNotNull().take(2)
                }
                val pagerState = rememberPagerState(pageCount = { galleryImages.size })

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(galleryImages[page])
                            .crossfade(true)
                            .build(),
                        contentDescription = "${item.title} - Photo ${page + 1}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Page indicator dots
                if (galleryImages.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 48.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(galleryImages.size) { index ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(if (pagerState.currentPage == index) 8.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (pagerState.currentPage == index) PrimaryGreen
                                        else Color.White.copy(alpha = 0.4f)
                                    )
                            )
                        }
                    }
                }

                // Bottom gradient overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, BrandNavy)
                            )
                        )
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
                    SmoothIconButton(
                        icon = Icons.AutoMirrored.Rounded.ArrowBack,
                        onClick = onBack,
                        tint = BrandNavy,
                        backgroundColor = Color.White.copy(alpha = 0.9f),
                        borderColor = Color.Transparent
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SmoothIconButton(
                            icon = Icons.Rounded.Share,
                            onClick = {
                                val shareText = "${shareListing(item.title, formatPriceCfa(item.pricePerDay))}\n\nVoir sur LocAll: https://locall.app/listing/${item.id}"
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Partager l'annonce"))
                            },
                            tint = BrandNavy,
                            backgroundColor = PrimaryGreen,
                            borderColor = PrimaryGreen
                        )

                        AnimatedHeartButton(
                            isFavorite = item.isBookmarked,
                            onClick = { viewModel.toggleBookmark(item) },
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        )
                        // Like counter
                        val likeCount = remember(item.id) { (item.id * 7 + 12) % 150 + 3 }
                        Box(
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Rounded.FavoriteBorder, contentDescription = null, tint = Color.Red, modifier = Modifier.size(14.dp))
                                Text("$likeCount", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Details summary card
        item {
            Column(
                modifier = Modifier.padding(20.dp).padding(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Category & verify label
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = PrimaryGreen.copy(alpha = 0.15f),
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
                            color = Color.White.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(12.dp))
                                Text("Vérifié", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }

                // Title
                Text(
                    text = item.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 28.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Location tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SmoothIcon(icon = Icons.Rounded.LocationOn, tint = Color.White, backgroundColor = PrimaryGreen, size = 28.dp, iconSize = 16.dp, cornerRadius = 8.dp)
                    Text(
                        text = "${item.neighborhood}, ${item.city} — Gabon",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )
                }

                // Geolocation map placeholder
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D2137)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                        // Map placeholder with grid pattern
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Draw grid lines
                            for (i in 0..10) {
                                val x = size.width * i / 10
                                drawLine(Color.White.copy(alpha = 0.04f), Offset(x, 0f), Offset(x, size.height))
                            }
                            for (i in 0..6) {
                                val y = size.height * i / 6
                                drawLine(Color.White.copy(alpha = 0.04f), Offset(0f, y), Offset(size.width, y))
                            }
                            // Draw center pin
                            drawCircle(PrimaryGreen.copy(alpha = 0.15f), radius = 20f, center = center)
                            drawCircle(PrimaryGreen, radius = 8f, center = center)
                            drawCircle(Color.White, radius = 4f, center = center)
                        }
                        // Location label
                        Column(modifier = Modifier.align(Alignment.BottomStart).padding(10.dp)) {
                            Text("${item.neighborhood}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("${item.city}, Gabon", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                        }
                        // Open in maps button
                        Icon(
                            Icons.Rounded.Map,
                            contentDescription = "Voir sur la carte",
                            tint = PrimaryGreen,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(28.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                .padding(4.dp)
                        )
                    }
                }

                // Daily Price Box
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
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
                            Text("Prix par jour", fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f), fontWeight = FontWeight.Medium)
                            Text(formatPriceCfa(item.pricePerDay), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryGreen)
                        }

                        Surface(
                            color = PrimaryGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                "Dispo Immédiatement",
                                color = PrimaryGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Description Title and contents
                SectionHeader(title = "Description du bien")
                Text(
                    text = item.description,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.75f),
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Justify
                )

                // Landlord contact row
                SectionHeader(title = "Annonceur")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandNavy.copy(alpha = 0.6f)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f))
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
                                    .clip(CircleShape)
                                    .border(2.dp, PrimaryGreen.copy(alpha = 0.3f), CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.ownerName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Rounded.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(13.dp))
                                Text("4.9/5", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f))
                                Box(modifier = Modifier.size(3.dp).background(Color.White.copy(alpha = 0.3f), CircleShape))
                                Text("Gabonais", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f), fontWeight = FontWeight.Medium)
                            }
                        }

                        // Message Owner direct trigger click
                        IconButton(
                            onClick = {
                                viewModel.selectItem(item)
                                viewModel.openChatFor(item)
                                viewModel.navigateTo("chat")
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(PrimaryGreen.copy(alpha = 0.15f))
                        ) {
                            Icon(Icons.Default.Email, contentDescription = "Contacter par message", tint = PrimaryGreen)
                        }
                    }
                }

                // Fake map section represent localization
                SectionHeader(title = "Géolocalisation du bien")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawRect(color = Color(0xFF0C2417))
                            val gridW = size.width / 5
                            val gridH = size.height / 3
                            for (i in 1..4) {
                                drawLine(Color.White.copy(alpha = 0.06f), start = androidx.compose.ui.geometry.Offset(i * gridW, 0f), end = androidx.compose.ui.geometry.Offset(i * gridW, size.height), strokeWidth = 2f)
                            }
                            for (i in 1..2) {
                                drawLine(Color.White.copy(alpha = 0.06f), start = androidx.compose.ui.geometry.Offset(0f, i * gridH), end = androidx.compose.ui.geometry.Offset(size.width, i * gridH), strokeWidth = 2f)
                            }
                            val river = Path().apply {
                                moveTo(0f, size.height * 0.8f)
                                quadraticTo(size.width / 2, size.height * 0.7f, size.width, size.height * 0.9f)
                                lineTo(size.width, size.height)
                                lineTo(0f, size.height)
                                close()
                            }
                            drawPath(river, color = Color(0xFF1A3354))
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
                                SmoothIcon(icon = Icons.Rounded.LocationOn, tint = Color.White, backgroundColor = PrimaryGreen, size = 24.dp, iconSize = 14.dp, cornerRadius = 6.dp)
                                Text("${item.neighborhood}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Reviews section
                val reviews by viewModel.reviewsFor(item.id).collectAsState(initial = emptyList())
                SectionHeader(title = "Avis")
                Spacer(modifier = Modifier.height(8.dp))
                reviews.forEach { review ->
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
                                Text(review.author, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    repeat(review.rating) {
                                        Icon(
                                            Icons.Rounded.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFFFB300),
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                            Text(review.comment, fontSize = 12.sp, color = Color.White.copy(alpha = 0.65f), lineHeight = 18.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Similar listings
                val similarItems by viewModel.similarItems.collectAsState()
                if (similarItems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionHeader(title = "Annonces similaires")
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(similarItems) { simItem ->
                            Card(
                                modifier = Modifier.width(160.dp).clickable { viewModel.selectItem(simItem); viewModel.navigateTo("details") },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))
                            ) {
                                Column {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current).data(simItem.imageUrl).crossfade(true).build(),
                                        contentDescription = simItem.title,
                                        modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(simItem.title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(formatPriceCfa(simItem.pricePerDay) + " / jour", color = PrimaryGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Booking recap
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Récapitulatif", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Prix / jour", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                            Text(formatPriceCfa(item.pricePerDay), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Commission LocAll (5%)", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                            Text(formatPriceCfa((item.pricePerDay * 0.05).toInt()), color = Color.White, fontSize = 13.sp)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.White.copy(alpha = 0.1f))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total / jour", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(formatPriceCfa((item.pricePerDay * 1.05).toInt()), color = PrimaryGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Bottom spacing for floating bar
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    // Floating price bar
    Surface(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF162133).copy(alpha = 0.95f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Total / jour", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f), fontWeight = FontWeight.Medium)
                Text(formatPriceCfa((item.pricePerDay * 1.05).toInt()), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryGreen)
            }
            Button(
                onClick = { showBookingDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.height(48.dp).testTag("rent_now_button")
            ) {
                Text("Louer maintenant", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
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
                val currentStep = when (paymentState) {
                    is PaymentState.Idle -> 1
                    is PaymentState.AwaitingPin -> 3
                    is PaymentState.Processing -> 4
                    is PaymentState.Success -> 4
                    else -> 1
                }
                StepIndicator(
                    currentStep = currentStep,
                    totalSteps = 4,
                    stepLabels = listOf("Jours", "Paiement", "Numéro", "Confirm")
                )

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

                        HorizontalDivider()

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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    PointsChip(points = 50)
                                    Text(
                                        formatPriceCfa(item.pricePerDay * daysCount),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = BrandNavy
                                    )
                                }
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
    val isLoading by viewModel.isBookingsLoading.collectAsState()
    var showCancelDialog by remember { mutableStateOf<Booking?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SmoothIconButton(
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                onClick = { viewModel.navigateTo("home") },
                tint = Color.White
            )
            Text(
                "Mes Réservations",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

        if (isLoading) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(3) { SkeletonBookingItem() }
            }
        } else if (bookings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                AnimatedEmptyState(
                    icon = Icons.Rounded.EventBusy,
                    title = "Aucune réservation",
                    subtitle = "Explorez les annonces et louez votre premier bien"
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(bookings) { booking ->
                    BookingItemCard(
                        booking = booking,
                        onCancelClick = { showCancelDialog = booking }
                    )
                }
            }
        }
    }

    showCancelDialog?.let { booking ->
        ConfirmDialog(
            title = "Annuler la réservation",
            message = "Êtes-vous sûr de vouloir annuler cette réservation ? Cette action est irréversible.",
            confirmText = "Annuler la réservation",
            onConfirm = {
                viewModel.cancelBooking(booking.id, "Annulé par l'utilisateur")
                showCancelDialog = null
            },
            onDismiss = { showCancelDialog = null },
            isDestructive = true
        )
    }
}

@Composable
fun BookingItemCard(booking: Booking, onCancelClick: () -> Unit = {}) {
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
                    color = when (booking.status) {
                        "Payé" -> Color(0xFF0C2417)
                        "Confirmé" -> Color(0xFF0D2944)
                        "Annulé" -> Color(0xFF3C1111)
                        else -> Color(0xFF162133)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Statut: ${booking.status}",
                        color = when (booking.status) {
                            "Payé" -> PrimaryGreen
                            "Confirmé" -> Color(0xFF4FC3F7)
                            "Annulé" -> Color.Red
                            else -> Color.White
                        },
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
                    Text(maskPhoneNumber(booking.paymentPhone), fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.12f), thickness = 1.dp)

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

            if (booking.status != "Annulé" && booking.status != "Terminé") {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    onClick = { onCancelClick() },
                    color = Color.Red.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Rounded.Cancel, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                        Text("Annuler", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ----------------- FAVORITES BOOKMARKS SCREEN -----------------

@Composable
fun BookmarksScreen(viewModel: RentalViewModel) {
    val items by viewModel.bookmarkedItems.collectAsState()
    val isLoading by viewModel.isBookmarksLoading.collectAsState()

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

        HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

        if (isLoading) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(4) { SkeletonCard() }
            }
        } else if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                AnimatedEmptyState(
                    icon = Icons.Rounded.FavoriteBorder,
                    title = "Aucun favori",
                    subtitle = "Ajoutez des annonces en favori pour les retrouver facilement"
                )
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
    val isLoading by viewModel.isInboxLoading.collectAsState()

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

        HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

        if (isLoading) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(5) { SkeletonChatItem() }
            }
        } else if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                AnimatedEmptyState(
                    icon = Icons.Rounded.ChatBubbleOutline,
                    title = "Aucun message",
                    subtitle = "Contactez un propriétaire pour démarrer une conversation"
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val showItems = items
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
    var showTypingIndicator by remember { mutableStateOf(false) }

    LaunchedEffect(showTypingIndicator) {
        if (showTypingIndicator) {
            delay(2000)
            showTypingIndicator = false
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandNavy)
            .imePadding()
    ) {
        // App header containing landlord profiles
        Surface(
            color = Color(0xFF162133),
            tonalElevation = 8.dp,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SmoothIconButton(
                    icon = Icons.AutoMirrored.Rounded.ArrowBack,
                    onClick = onBack,
                    tint = Color.White
                )

                Box(modifier = Modifier.size(40.dp)) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80")
                            .crossfade(true)
                            .build(),
                        contentDescription = "Contact photo avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(2.dp, PrimaryGreen.copy(alpha = 0.3f), CircleShape)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(item.ownerName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val infiniteTransition = rememberInfiniteTransition(label = "online")
                        val pulseAlpha by infiniteTransition.animateFloat(
                            initialValue = 0.4f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(800),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "pulseAlpha"
                        )
                        val pulseScale by infiniteTransition.animateFloat(
                            initialValue = 0.8f,
                            targetValue = 1.2f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(800),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "pulseScale"
                        )
                        Box(
                            modifier = Modifier
                                .size((6 * pulseScale).dp)
                                .clip(CircleShape)
                                .background(PrimaryGreen.copy(alpha = pulseAlpha))
                        )
                        Text("En ligne", fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f), fontWeight = FontWeight.Medium)
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
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Aujourd'hui",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            items(messages) { message ->
                val isMe = message.sender == "User"
                val isImage = message.messageText.startsWith("[image]")
                val isLocation = message.messageText.startsWith("[location]")
                val displayText = when {
                    isImage -> message.messageText.removePrefix("[image] ").trim()
                    isLocation -> message.messageText.removePrefix("[location] ").trim()
                    else -> message.messageText
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                ) {
                    if (!isMe) {
                        Text(
                            text = item.ownerName,
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .widthIn(max = 280.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 18.dp,
                                    topEnd = 18.dp,
                                    bottomStart = if (isMe) 18.dp else 4.dp,
                                    bottomEnd = if (isMe) 4.dp else 18.dp
                                )
                            )
                            .background(if (isMe) PrimaryGreen.copy(alpha = 0.15f) else Color(0xFF1E2D45))
                            .border(
                                1.dp,
                                if (isMe) PrimaryGreen.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.06f),
                                RoundedCornerShape(18.dp)
                            )
                            .padding(14.dp)
                    ) {
                        when {
                            isImage -> Column {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(displayText)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Image partagée",
                                    modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(10.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("📷 Photo partagée", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                            }
                            isLocation -> Column {
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D2137))
                                ) {
                                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(PrimaryGreen.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                                            Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                                        }
                                        Column {
                                            Text("📍 Position partagée", color = PrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text(displayText, color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                            else -> Text(
                                text = displayText,
                                fontSize = 14.sp,
                                color = if (isMe) Color.White else Color.White.copy(alpha = 0.9f),
                                lineHeight = 20.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isMe) "Vous • maintenant" else "${item.ownerName} • maintenant",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        if (showTypingIndicator) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(3) { i ->
                        val dotOffset by rememberInfiniteTransition(label = "dot$i").animateFloat(
                            initialValue = 0f, targetValue = -8f,
                            animationSpec = infiniteRepeatable(tween(300, delayMillis = i * 100), RepeatMode.Reverse),
                            label = "dotAnim$i"
                        )
                        Box(modifier = Modifier.size(6.dp).offset(y = dotOffset.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.5f)))
                    }
                }
            }
        }

        // Write messaging bar bottom
        Surface(
            color = Color(0xFF162133),
            tonalElevation = 8.dp,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Column {
                QuickReplyChips(
                    replies = listOf("Disponible ?", "Quel prix ?", "Visite possible ?", "Négociation"),
                    onReply = { reply ->
                        viewModel.sendChatMessage(item.id, reply, item.ownerName)
                        showTypingIndicator = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
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
                    placeholder = { Text("Écrire un message...", color = Color.White.copy(alpha = 0.3f), fontSize = 14.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_message_input"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        focusedContainerColor = Color(0xFF162133),
                        unfocusedContainerColor = Color(0xFF162133)
                    ),
                    maxLines = 3,
                    singleLine = false
                )

                IconButton(
                    onClick = {
                        if (userMessageText.isNotBlank()) {
                            viewModel.sendChatMessage(item.id, userMessageText, item.ownerName)
                            userMessageText = ""
                            showTypingIndicator = true
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (userMessageText.isNotBlank()) PrimaryGreen else Color.White.copy(alpha = 0.1f))
                        .testTag("send_chat_message_button")
                ) {
                    Icon(Icons.Rounded.Send, contentDescription = "Envoyer", tint = if (userMessageText.isNotBlank()) BrandNavy else Color.White.copy(alpha = 0.3f))
                }
            }
            }
        }
    }
}

// ----------------- ADD POST LISTING FORM SCREEN -----------------

@Composable
fun PostListingScreen(viewModel: RentalViewModel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Immobilier") }
    var priceStr by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Libreville") }
    var neighborhood by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var ownerPhone by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var photoTaken by remember { mutableStateOf(false) }
    var isSuccessPost by remember { mutableStateOf(false) }
    var showErrorField by remember { mutableStateOf(false) }

    val categoryIcons = mapOf(
        "Immobilier" to Icons.Rounded.Home,
        "Véhicules" to Icons.Rounded.DirectionsCar,
        "Équipements" to Icons.Rounded.Build
    )

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
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(36.dp))
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
                            model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true).build(),
                            contentDescription = "Aperçu",
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
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
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("Immobilier", "Véhicules", "Équipements").forEach { cat ->
                            val isSelected = category == cat
                            Card(
                                modifier = Modifier.weight(1f).clickable { category = cat },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) PrimaryGreen.copy(alpha = 0.15f) else Color(0xFF162133)
                                ),
                                border = BorderStroke(1.dp, if (isSelected) PrimaryGreen else Color.White.copy(alpha = 0.08f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    SmoothIcon(
                                        icon = categoryIcons[cat] ?: Icons.Rounded.Category,
                                        tint = if (isSelected) BrandNavy else PrimaryGreen,
                                        backgroundColor = if (isSelected) PrimaryGreen else PrimaryGreen.copy(alpha = 0.12f),
                                        size = 36.dp,
                                        iconSize = 18.dp
                                    )
                                    Text(cat, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) PrimaryGreen else Color.White.copy(alpha = 0.6f))
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
                                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Photo capturée !", color = PrimaryGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Rounded.AddAPhoto, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(32.dp))
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
                            Icon(Icons.Rounded.Warning, contentDescription = null, tint = Color.Red, modifier = Modifier.size(18.dp))
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
