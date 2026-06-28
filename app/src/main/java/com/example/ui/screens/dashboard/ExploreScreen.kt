package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import com.example.data.model.RentalItem
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.model.RentalCategory
import com.example.ui.viewmodel.RentalViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(viewModel: RentalViewModel) {
    val items by viewModel.filteredRentalItems.collectAsState()
    val rawItems by viewModel.rawRentalItems.collectAsState()
    val selectedCat by viewModel.selectedCategory.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedMaxPrice by viewModel.selectedMaxPrice.collectAsState()

    var sortOption by remember { mutableStateOf(SortOption.RECENT) }
    var selectedItemForModal by remember { mutableStateOf<RentalItem?>(null) }
    var showBookingFromModal by remember { mutableStateOf<RentalItem?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var showFab by remember { mutableStateOf(false) }
    var showActionsSheet by remember { mutableStateOf(false) }
    val isOwnerMode by viewModel.isOwnerMode.collectAsState()
    val appearedItems = remember { mutableStateMapOf<Int, Boolean>() }

    LaunchedEffect(Unit) { delay(300); isLoading = false }
    LaunchedEffect(isRefreshing) { if (isRefreshing) { delay(300); isRefreshing = false } }

    val sortedItems = items
    val displayItems = if (isOwnerMode) sortedItems.filter { it.ownerName == "Vous" || it.ownerName == "User" } else sortedItems

    var displayedCount by remember { mutableIntStateOf(10) }
    val pagedItems = displayItems.take(displayedCount)
    val canLoadMore = displayedCount < displayItems.size

    LaunchedEffect(searchQuery, selectedCat, selectedCity, selectedMaxPrice) {
        displayedCount = 10
    }

    Box(modifier = Modifier.fillMaxSize()) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .collect { index -> showFab = index > 3 }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        if (isRefreshing) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.TopCenter).padding(8.dp),
                color = PrimaryGreen,
                strokeWidth = 2.dp
            )
        }
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = if (isRefreshing) 48.dp else 0.dp)
        ) {
        // Welcome Header
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

        // Search Bar + Filter Button
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
                    imageVector = Icons.Rounded.Tune,
                    contentDescription = "Filtres",
                    onClick = { showFilterSheet = true },
                    tint = BrandNavy,
                    backgroundColor = PrimaryGreen,
                    modifier = Modifier.size(54.dp).testTag("filter_button"),
                    iconSize = 22.dp
                )
            }
        }

        // Category Grid (2 columns)
        item {
            val categoriesWithIcons = listOf(
                Triple("Tous", Icons.Rounded.Apps, "Tous")
            ) + RentalCategory.entries.map { Triple(it.displayName, it.icon, it.displayName) }

            val rows = categoriesWithIcons.chunked(2)

            Column(
                modifier = Modifier.fillMaxWidth().testTag("categories_section"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rows.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        row.forEach { (catName, icon, label) ->
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
                                modifier = Modifier.weight(1f).testTag("category_filter_$catName")
                            )
                        }
                        if (row.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
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
                            contentDescription = "Aucun résultat",
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
            items(pagedItems, key = { it.id }, contentType = { "rental" }) { item ->
                LaunchedEffect(item.id) {
                    delay(item.id.toLong() * 60L)
                    appearedItems[item.id] = true
                }
                AnimatedVisibility(
                    visible = appearedItems.containsKey(item.id),
                    enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 3 }
                ) {
                    RentalCard(
                        item = item,
                        onSelect = { selectedItemForModal = item },
                        onBookmarkToggle = { viewModel.toggleBookmark(item) },
                        onChat = {
                            viewModel.selectItem(item)
                            viewModel.openChatFor(item)
                            viewModel.navigateTo("chat")
                        },
                        onBook = { showBookingFromModal = item }
                    )
                }
            }
            if (canLoadMore) {
                item {
                    LaunchedEffect(Unit) {
                        delay(500)
                        displayedCount = (displayedCount + 10).coerceAtMost(displayItems.size)
                    }
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryGreen, strokeWidth = 2.dp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    }

    AnimatedVisibility(
        visible = showFab,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut(),
        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
    ) {
        FloatingActionButton(
            onClick = { showActionsSheet = true },
            containerColor = PrimaryGreen,
            contentColor = BrandNavy
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Actions rapides")
        }
    }
    }

    // Unified Filter Bottom Sheet
    if (showFilterSheet) {
        FilterBottomSheet(
            currentCity = selectedCity,
            currentMaxPrice = selectedMaxPrice,
            currentSort = sortOption,
            onDismiss = { showFilterSheet = false },
            onApply = { city, maxPrice, sort ->
                viewModel.setSelectedCity(city)
                viewModel.setSelectedMaxPrice(maxPrice)
                sortOption = sort
                viewModel.setSortOption(sort)
                showFilterSheet = false
            },
            onReset = {
                viewModel.setSelectedCity("Tous")
                viewModel.setSelectedMaxPrice(0)
                sortOption = SortOption.RECENT
                viewModel.setSortOption(SortOption.RECENT)
                showFilterSheet = false
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

    if (showActionsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showActionsSheet = false },
            containerColor = Color(0xFF162133),
            contentColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Actions rapides", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                listOf(
                    Triple(Icons.Rounded.Add, "Publier une annonce", { viewModel.navigateTo("post_listing") }),
                    Triple(Icons.Rounded.Search, "Recherche avancée", { viewModel.navigateTo("advanced_search") }),
                    Triple(Icons.Rounded.Email, "Messages", { viewModel.navigateTo("messages") }),
                    Triple(Icons.Rounded.Person, "Mon profil", { viewModel.navigateTo("profile") })
                ).forEach { (icon, label, action) ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { action(); showActionsSheet = false },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(icon, contentDescription = label, tint = PrimaryGreen)
                            Text(label, color = Color.White, fontSize = 15.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentCity: String,
    currentMaxPrice: Int,
    currentSort: SortOption,
    onDismiss: () -> Unit,
    onApply: (city: String, maxPrice: Int, sort: SortOption) -> Unit,
    onReset: () -> Unit
) {
    var city by remember { mutableStateOf(currentCity) }
    var maxPrice by remember { mutableIntStateOf(currentMaxPrice) }
    var sort by remember { mutableStateOf(currentSort) }

    val cities = listOf("Tous", "Libreville", "Port-Gentil", "Franceville", "Oyem", "Akanda")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF162133),
        contentColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("Filtres", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)

            // City selection
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Ville", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(cities, key = { it }, contentType = { "string" }) { c ->
                        val isSelected = city == c
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) PrimaryGreen else Color.White.copy(alpha = 0.06f))
                                .border(
                                    1.dp,
                                    if (isSelected) Color.Transparent else Color.White.copy(alpha = 0.12f),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { city = c }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = c,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) BrandNavy else Color.White.copy(alpha = 0.70f)
                            )
                        }
                    }
                }
            }

            // Price range
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Prix max",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (maxPrice == 0) "Aucune limite" else "Maximum: " + formatPriceCfa(maxPrice) + " / Jour",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (maxPrice == 0) Color.White.copy(alpha = 0.5f) else PrimaryGreen
                )
                Slider(
                    value = if (maxPrice == 0) 160000f else maxPrice.toFloat(),
                    onValueChange = {
                        val v = it.toInt()
                        maxPrice = if (v >= 15500) v else 0
                    },
                    valueRange = 10000f..150000f,
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryGreen,
                        activeTrackColor = PrimaryGreen,
                        inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(30000, 60000, 100000).forEach { preset ->
                        OutlinedButton(
                            onClick = { maxPrice = preset },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, if (maxPrice == preset) PrimaryGreen else Color.White.copy(alpha = 0.15f))
                        ) {
                            Text(
                                "${preset / 1000}K",
                                fontSize = 11.sp,
                                color = if (maxPrice == preset) PrimaryGreen else Color.White.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Sort selection
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Tri par", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(SortOption.entries, key = { it.name }, contentType = { "sort" }) { option ->
                        val isSelected = sort == option
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) PrimaryGreen else Color.White.copy(alpha = 0.06f))
                                .border(
                                    1.dp,
                                    if (isSelected) Color.Transparent else Color.White.copy(alpha = 0.12f),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { sort = option }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = option.label,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) BrandNavy else Color.White.copy(alpha = 0.70f)
                            )
                        }
                    }
                }
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                ) {
                    Text("Réinitialiser", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                }

                Button(
                    onClick = { onApply(city, maxPrice, sort) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Appliquer", color = BrandNavy, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RentalCard(
    item: RentalItem,
    onSelect: () -> Unit,
    onBookmarkToggle: () -> Unit,
    onChat: () -> Unit,
    onBook: () -> Unit
) {
    var showContextMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onSelect() },
                onLongClick = { showContextMenu = true }
            )
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
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.imageUrl)
                        .crossfade(true)
                        .size(Size.ORIGINAL)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                    error = painterResource(android.R.drawable.ic_menu_close_clear_cancel)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

                    AnimatedHeartButton(
                        isFavorite = item.isBookmarked,
                        onClick = onBookmarkToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .testTag("bookmark_toggle_button_${item.id}")
                    )
                }

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
                                contentDescription = "Profil vérifié",
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
                            contentDescription = "Note",
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

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = "Localisation",
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

                    Surface(
                        onClick = onBook,
                        color = PrimaryGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f)),
                        modifier = Modifier.testTag("rental_book_button_${item.id}")
                    ) {
                        Text(
                            text = "Louer",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        DropdownMenu(
            expanded = showContextMenu,
            onDismissRequest = { showContextMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Voir détails") },
                onClick = { showContextMenu = false; onSelect() },
                leadingIcon = { Icon(Icons.Rounded.Info, contentDescription = null) }
            )
            DropdownMenuItem(
                text = { Text("Contacter") },
                onClick = { showContextMenu = false; onChat() },
                leadingIcon = { Icon(Icons.Rounded.Chat, contentDescription = null) }
            )
            DropdownMenuItem(
                text = { Text("Signaler", color = Color.Red) },
                onClick = { showContextMenu = false },
                leadingIcon = { Icon(Icons.Rounded.Flag, contentDescription = null, tint = Color.Red) }
            )
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
                                .size(Size.ORIGINAL)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .build(),
                            contentDescription = item.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                            error = painterResource(android.R.drawable.ic_menu_close_clear_cancel)
                        )

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

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
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

                        Text(
                            text = item.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 26.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

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

                        SectionHeader(title = "Description")
                        Text(
                            text = item.description,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            lineHeight = 22.sp
                        )

                        HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

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
                                            .size(Size.ORIGINAL)
                                            .diskCachePolicy(CachePolicy.ENABLED)
                                            .memoryCachePolicy(CachePolicy.ENABLED)
                                            .build(),
                                        contentDescription = "Avatar propriétaire",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop,
                                        placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                                        error = painterResource(android.R.drawable.ic_menu_close_clear_cancel)
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
