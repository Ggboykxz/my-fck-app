package com.example.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.example.ui.screens.*
import com.example.ui.viewmodel.RentalViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun DashboardNavHost(
    navController: NavHostController,
    viewModel: RentalViewModel
) {
    val selectedItem by viewModel.selectedItem.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()

    // Determine start destination from ViewModel state (for test compatibility)
    val startRoute = remember {
        when (viewModel.currentScreen.value) {
            is Screen.Home -> RouteHome
            is Screen.Bookings -> RouteBookings
            is Screen.PostListing -> RoutePostListing
            is Screen.Bookmarks -> RouteBookmarks
            is Screen.Messages -> RouteMessages
            is Screen.Profile -> RouteProfile
            is Screen.Details -> RouteDetails
            is Screen.Chat -> RouteChat
        }
    }

    NavHost(
        navController = navController,
        startDestination = startRoute,
        enterTransition = { fadeIn(tween(300)) },
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = { fadeOut(tween(300)) }
    ) {
        // ─── Bottom nav screens ───

        composable<RouteHome>(
            enterTransition = { slideInHorizontally(tween(300)) { -it } + fadeIn(tween(300)) },
            exitTransition = { slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300)) }
        ) {
            ExploreScreen(viewModel)
        }

        composable<RouteBookings> {
            BookingsScreen(viewModel)
        }

        composable<RoutePostListing> {
            PostListingScreen(viewModel)
        }

        composable<RouteBookmarks> {
            BookmarksScreen(viewModel)
        }

        composable<RouteMessages> {
            InboxScreen(viewModel)
        }

        composable<RouteProfile> {
            ProfileNavigator(viewModel = viewModel)
        }

        // ─── Overlay screens (slide from right) ───

        composable<RouteDetails>(
            enterTransition = { slideInHorizontally(tween(300)) { it } + fadeIn(tween(300)) },
            exitTransition = { slideOutHorizontally(tween(300)) { -it } + fadeOut(tween(300)) },
            deepLinks = listOf(
                navDeepLink { uriPattern = "locall://item/{itemId}" }
            )
        ) { backStackEntry ->
            val deepLinkItemId = backStackEntry.arguments?.getString("itemId")?.toIntOrNull()
            val item = if (deepLinkItemId != null) {
                viewModel.rawRentalItems.collectAsState().value.find { it.id == deepLinkItemId }
            } else {
                selectedItem
            }
            if (item != null) {
                ItemDetailsScreen(
                    item = item,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            } else {
                LaunchedEffect(Unit) { navController.popBackStack() }
            }
        }

        composable<RouteChat>(
            enterTransition = { slideInHorizontally(tween(300)) { it } + fadeIn(tween(300)) },
            exitTransition = { slideOutHorizontally(tween(300)) { -it } + fadeOut(tween(300)) }
        ) {
            val item = selectedItem
            if (item != null) {
                ChatRoomScreen(
                    item = item,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            } else {
                LaunchedEffect(Unit) { navController.popBackStack() }
            }
        }
    }
}
