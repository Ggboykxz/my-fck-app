package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.navigation.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel
import com.example.ui.viewmodel.Screen
import androidx.compose.animation.ExperimentalSharedTransitionApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainDashboardView(viewModel: RentalViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedItem by viewModel.selectedItem.collectAsState()
    val unreadCount by viewModel.unreadMessageCount.collectAsState()
    val bookings by viewModel.bookings.collectAsState()

    // Main Layout Scaffold with M3 Bottom Navigation
    Scaffold(
        bottomBar = {
            if (currentScreen !is Screen.Details && currentScreen !is Screen.Chat) {
                DashboardBottomBarLegacy(
                    currentScreen = currentScreen,
                    onNavigate = { screen -> viewModel.navigateTo(screen) },
                    unreadCount = unreadCount,
                    bookingCount = bookings.size
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .background(BrandNavy)
        ) {
            Column {
                OfflineBanner()
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        if (targetState is Screen.Details || targetState is Screen.Chat) {
                            slideInVertically(initialOffsetY = { it / 3 }) + fadeIn(tween(300)) togetherWith slideOutVertically(targetOffsetY = { -it / 3 }) + fadeOut(tween(200))
                        } else if (targetState is Screen.Home) {
                            slideInVertically(initialOffsetY = { it / 3 }) + fadeIn(tween(300)) togetherWith slideOutVertically(targetOffsetY = { -it / 3 }) + fadeOut(tween(200))
                        } else {
                            fadeIn(tween(300)) + slideInHorizontally(initialOffsetX = { it / 5 }) togetherWith fadeOut(tween(200)) + slideOutHorizontally(targetOffsetX = { -it / 5 })
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
                        is Screen.MapExplorer -> MapExplorerScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateTo("home") },
                            onSelectItem = { item ->
                                viewModel.selectItem(item)
                                viewModel.navigateTo("details")
                            }
                        )
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
                                    onBack = { viewModel.navigateTo("messages") }
                                )
                            } else {
                                ExploreScreen(viewModel)
                            }
                        }
                        is Screen.SearchIntelligence -> SearchIntelligenceScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateTo("home") }
                        )
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun MainDashboardViewNavHost(viewModel: RentalViewModel) {
    val navController = rememberNavController()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val unreadCount by viewModel.unreadMessageCount.collectAsState()
    val bookings by viewModel.bookings.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isOverlay = currentRoute == RouteDetails::class.qualifiedName ||
            currentRoute == RouteChat::class.qualifiedName

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(snackbarHostState, coroutineScope) {
        SnackbarHelper.init(snackbarHostState, coroutineScope)
    }

    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissSnackbar()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = PrimaryGreen,
                    contentColor = BrandNavy,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(16.dp)
                )
            }
        },
        bottomBar = {
            if (!isOverlay) {
                DashboardBottomBar(
                    navController = navController,
                    unreadCount = unreadCount,
                    bookingCount = bookings.size
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .background(BrandNavy)
        ) {
            Column {
                OfflineBanner()
                DashboardNavHost(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }

    LaunchedEffect(currentScreen) {
        val targetRoute = when (currentScreen) {
            is Screen.Home -> RouteHome::class.qualifiedName
            is Screen.Bookings -> RouteBookings::class.qualifiedName
            is Screen.PostListing -> RoutePostListing::class.qualifiedName
            is Screen.Bookmarks -> RouteBookmarks::class.qualifiedName
            is Screen.Messages -> RouteMessages::class.qualifiedName
            is Screen.Profile -> RouteProfile::class.qualifiedName
            is Screen.Details -> RouteDetails::class.qualifiedName
            is Screen.Chat -> RouteChat::class.qualifiedName
            is Screen.MapExplorer -> RouteMapExplorer::class.qualifiedName
            is Screen.SearchIntelligence -> RouteSearchIntelligence::class.qualifiedName
            is Screen.OwnerAnalytics -> null
            is Screen.MarketInsights -> null
            is Screen.NotificationSettings -> null
            is Screen.ReferralTracking -> null
        }
        if (targetRoute != null && currentRoute != targetRoute) {
            navController.navigate(targetRoute) {
                popUpTo(RouteHome::class.qualifiedName!!) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
}

@Composable
fun DashboardBottomBarLegacy(
    currentScreen: Screen,
    onNavigate: (String) -> Unit,
    unreadCount: Int = 0,
    bookingCount: Int = 0
) {
    NavigationBar(
        containerColor = BrandNavy,
        tonalElevation = 8.dp,
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        NavigationBarItem(
            selected = currentScreen is Screen.Home || currentScreen is Screen.Details || currentScreen is Screen.MapExplorer,
            onClick = { onNavigate("home") },
            icon = {
                val isSel = currentScreen is Screen.Home || currentScreen is Screen.Details || currentScreen is Screen.MapExplorer
                SmoothIcon(Icons.Rounded.Search, contentDescription = "Explorer", tint = if (isSel) BrandNavy else Color.White.copy(alpha = 0.45f), backgroundColor = if (isSel) PrimaryGreen else Color.White.copy(alpha = 0.08f), modifier = Modifier.size(32.dp), iconSize = 18.dp)
            },
            label = { Text("Explorer", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryGreen, selectedTextColor = PrimaryGreen,
                unselectedIconColor = Color.White.copy(alpha = 0.45f), unselectedTextColor = Color.White.copy(alpha = 0.45f),
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
                selectedIconColor = PrimaryGreen, selectedTextColor = PrimaryGreen,
                unselectedIconColor = Color.White.copy(alpha = 0.45f), unselectedTextColor = Color.White.copy(alpha = 0.45f),
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
                selectedIconColor = PrimaryGreen, selectedTextColor = PrimaryGreen,
                unselectedIconColor = Color.White.copy(alpha = 0.45f), unselectedTextColor = Color.White.copy(alpha = 0.45f),
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
                        androidx.compose.animation.AnimatedVisibility(visible = unreadCount > 0, enter = scaleIn(tween(200)) + fadeIn(tween(200)), exit = scaleOut(tween(150)) + fadeOut(tween(150))) {
                            Badge(containerColor = Color.Red, contentColor = Color.White) {
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
                selectedIconColor = PrimaryGreen, selectedTextColor = PrimaryGreen,
                unselectedIconColor = Color.White.copy(alpha = 0.45f), unselectedTextColor = Color.White.copy(alpha = 0.45f),
                indicatorColor = Color.White.copy(alpha = 0.12f)
            )
        )

        NavigationBarItem(
            selected = currentScreen is Screen.Profile,
            onClick = { onNavigate("profile") },
            icon = {
                val isSel = currentScreen is Screen.Profile
                BadgedBox(
                    badge = {
                        androidx.compose.animation.AnimatedVisibility(visible = bookingCount > 0, enter = scaleIn(tween(200)) + fadeIn(tween(200)), exit = scaleOut(tween(150)) + fadeOut(tween(150))) {
                            Badge(containerColor = PrimaryGreen) {
                                Text("$bookingCount", fontSize = 9.sp, color = BrandNavy)
                            }
                        }
                    }
                ) {
                    SmoothIcon(Icons.Rounded.Person, contentDescription = "Profil", tint = if (isSel) BrandNavy else Color.White.copy(alpha = 0.45f), backgroundColor = if (isSel) PrimaryGreen else Color.White.copy(alpha = 0.08f), modifier = Modifier.size(32.dp), iconSize = 18.dp)
                }
            },
            label = { Text("Profil", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryGreen, selectedTextColor = PrimaryGreen,
                unselectedIconColor = Color.White.copy(alpha = 0.45f), unselectedTextColor = Color.White.copy(alpha = 0.45f),
                indicatorColor = Color.White.copy(alpha = 0.12f)
            )
        )
    }
}

@Composable
fun DashboardBottomBar(
    navController: androidx.navigation.NavController,
    unreadCount: Int = 0,
    bookingCount: Int = 0
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    fun isSelected(route: String): Boolean = currentRoute == route

    NavigationBar(
        containerColor = BrandNavy,
        tonalElevation = 8.dp,
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        NavigationBarItem(
            selected = isSelected(RouteHome::class.qualifiedName!!) || isSelected(RouteDetails::class.qualifiedName!!),
            onClick = { navController.navigate(RouteHome::class.qualifiedName!!) {
                popUpTo(RouteHome::class.qualifiedName!!) { saveState = true }
                launchSingleTop = true; restoreState = true
            }},
            icon = {
                val isSel = isSelected(RouteHome::class.qualifiedName!!) || isSelected(RouteDetails::class.qualifiedName!!)
                SmoothIcon(Icons.Rounded.Search, contentDescription = "Explorer", tint = if (isSel) BrandNavy else Color.White.copy(alpha = 0.45f), backgroundColor = if (isSel) PrimaryGreen else Color.White.copy(alpha = 0.08f), modifier = Modifier.size(32.dp), iconSize = 18.dp)
            },
            label = { Text("Explorer", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryGreen, selectedTextColor = PrimaryGreen,
                unselectedIconColor = Color.White.copy(alpha = 0.45f), unselectedTextColor = Color.White.copy(alpha = 0.45f),
                indicatorColor = Color.White.copy(alpha = 0.12f)
            )
        )

        NavigationBarItem(
            selected = isSelected(RoutePostListing::class.qualifiedName!!),
            onClick = { navController.navigate(RoutePostListing::class.qualifiedName!!) {
                popUpTo(RouteHome::class.qualifiedName!!) { saveState = true }
                launchSingleTop = true; restoreState = true
            }},
            icon = {
                val isSel = isSelected(RoutePostListing::class.qualifiedName!!)
                SmoothIcon(Icons.Rounded.AddCircle, contentDescription = "Ajouter", tint = if (isSel) BrandNavy else Color.White.copy(alpha = 0.45f), backgroundColor = if (isSel) PrimaryGreen else Color.White.copy(alpha = 0.08f), modifier = Modifier.size(32.dp), iconSize = 18.dp)
            },
            label = { Text("Publier", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryGreen, selectedTextColor = PrimaryGreen,
                unselectedIconColor = Color.White.copy(alpha = 0.45f), unselectedTextColor = Color.White.copy(alpha = 0.45f),
                indicatorColor = Color.White.copy(alpha = 0.12f)
            )
        )

        NavigationBarItem(
            selected = isSelected(RouteBookmarks::class.qualifiedName!!),
            onClick = { navController.navigate(RouteBookmarks::class.qualifiedName!!) {
                popUpTo(RouteHome::class.qualifiedName!!) { saveState = true }
                launchSingleTop = true; restoreState = true
            }},
            icon = {
                val isSel = isSelected(RouteBookmarks::class.qualifiedName!!)
                SmoothIcon(Icons.Rounded.FavoriteBorder, contentDescription = "Favoris", tint = if (isSel) BrandNavy else Color.White.copy(alpha = 0.45f), backgroundColor = if (isSel) PrimaryGreen else Color.White.copy(alpha = 0.08f), modifier = Modifier.size(32.dp), iconSize = 18.dp)
            },
            label = { Text("Favoris", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryGreen, selectedTextColor = PrimaryGreen,
                unselectedIconColor = Color.White.copy(alpha = 0.45f), unselectedTextColor = Color.White.copy(alpha = 0.45f),
                indicatorColor = Color.White.copy(alpha = 0.12f)
            )
        )

        NavigationBarItem(
            selected = isSelected(RouteMessages::class.qualifiedName!!),
            onClick = { navController.navigate(RouteMessages::class.qualifiedName!!) {
                popUpTo(RouteHome::class.qualifiedName!!) { saveState = true }
                launchSingleTop = true; restoreState = true
            }},
            icon = {
                val isSel = isSelected(RouteMessages::class.qualifiedName!!)
                BadgedBox(
                    badge = {
                        androidx.compose.animation.AnimatedVisibility(visible = unreadCount > 0, enter = scaleIn(tween(200)) + fadeIn(tween(200)), exit = scaleOut(tween(150)) + fadeOut(tween(150))) {
                            Badge(containerColor = Color.Red, contentColor = Color.White) {
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
                selectedIconColor = PrimaryGreen, selectedTextColor = PrimaryGreen,
                unselectedIconColor = Color.White.copy(alpha = 0.45f), unselectedTextColor = Color.White.copy(alpha = 0.45f),
                indicatorColor = Color.White.copy(alpha = 0.12f)
            )
        )

        NavigationBarItem(
            selected = isSelected(RouteProfile::class.qualifiedName!!),
            onClick = { navController.navigate(RouteProfile::class.qualifiedName!!) {
                popUpTo(RouteHome::class.qualifiedName!!) { saveState = true }
                launchSingleTop = true; restoreState = true
            }},
            icon = {
                val isSel = isSelected(RouteProfile::class.qualifiedName!!)
                BadgedBox(
                    badge = {
                        androidx.compose.animation.AnimatedVisibility(visible = bookingCount > 0, enter = scaleIn(tween(200)) + fadeIn(tween(200)), exit = scaleOut(tween(150)) + fadeOut(tween(150))) {
                            Badge(containerColor = PrimaryGreen) {
                                Text("$bookingCount", fontSize = 9.sp, color = BrandNavy)
                            }
                        }
                    }
                ) {
                    SmoothIcon(Icons.Rounded.Person, contentDescription = "Profil", tint = if (isSel) BrandNavy else Color.White.copy(alpha = 0.45f), backgroundColor = if (isSel) PrimaryGreen else Color.White.copy(alpha = 0.08f), modifier = Modifier.size(32.dp), iconSize = 18.dp)
                }
            },
            label = { Text("Profil", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryGreen, selectedTextColor = PrimaryGreen,
                unselectedIconColor = Color.White.copy(alpha = 0.45f), unselectedTextColor = Color.White.copy(alpha = 0.45f),
                indicatorColor = Color.White.copy(alpha = 0.12f)
            )
        )
    }
}
