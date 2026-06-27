package com.example.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ui.screens.*
import com.example.ui.viewmodel.RentalViewModel

@Composable
fun OnboardingNavHost(
    navController: NavHostController,
    viewModel: RentalViewModel,
    onFinished: () -> Unit
) {
    val step by viewModel.onboardingStep.collectAsState()

    // Sync ViewModel step → NavController
    LaunchedEffect(step) {
        val route = when (step) {
            0 -> RouteOnboardingSplash::class.qualifiedName!!
            1 -> RouteOnboardingWelcome::class.qualifiedName!!
            2 -> RouteOnboardingPayments::class.qualifiedName!!
            3 -> RouteOnboardingTrust::class.qualifiedName!!
            else -> return@LaunchedEffect
        }
        navController.navigate(route) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    Box(modifier = Modifier.statusBarsPadding()) {
    NavHost(
        navController = navController,
        startDestination = RouteOnboardingSplash,
        enterTransition = { slideInHorizontally(tween(300)) { it } + fadeIn(tween(300)) },
        exitTransition = { slideOutHorizontally(tween(300)) { -it } + fadeOut(tween(300)) },
        popEnterTransition = { slideInHorizontally(tween(300)) { -it } + fadeIn(tween(300)) },
        popExitTransition = { slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300)) }
    ) {
        composable<RouteOnboardingSplash> {
            SplashScreenView(onNext = { viewModel.nextOnboarding() })
        }

        composable<RouteOnboardingWelcome> {
            WelcomeOnboardingScreen(
                onNext = { viewModel.nextOnboarding() },
                onSkip = { viewModel.skipOnboarding() }
            )
        }

        composable<RouteOnboardingPayments> {
            PaymentsOnboardingScreen(
                onNext = { viewModel.nextOnboarding() },
                onSkip = { viewModel.skipOnboarding() }
            )
        }

        composable<RouteOnboardingTrust> {
            TrustOnboardingScreen(
                onStart = {
                    viewModel.nextOnboarding()
                    onFinished()
                }
            )
        }
    }
    }
}
