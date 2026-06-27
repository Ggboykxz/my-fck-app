package com.example.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ui.screens.*
import com.example.ui.viewmodel.RentalViewModel

@Composable
fun AuthNavHost(
    navController: NavHostController,
    viewModel: RentalViewModel
) {
    val authState by viewModel.authState.collectAsState()

    // Sync ViewModel authState → NavController
    LaunchedEffect(authState) {
        val route = authStateToRoute(authState) ?: return@LaunchedEffect
        navController.navigate(route) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = RouteAuthLogin,
        enterTransition = { slideInHorizontally(tween(300)) { it } + fadeIn(tween(300)) },
        exitTransition = { slideOutHorizontally(tween(300)) { -it } + fadeOut(tween(300)) }
    ) {
        composable<RouteAuthLogin> {
            LoginScreenView(
                onLoginSuccess = { viewModel.setAuthState("loading_login") },
                onNavigateToRegister = { viewModel.setAuthState("register") },
                onNavigateToForgotPassword = { viewModel.setAuthState("forgot_password") }
            )
        }

        composable<RouteAuthLoadingLogin> {
            LoadingLoginView(
                onLoadingFinished = { viewModel.setAuthState("login_success") }
            )
        }

        composable<RouteAuthLoginSuccess> {
            LoginSuccessView(
                onProceed = { viewModel.setLoggedIn(true) }
            )
        }

        composable<RouteAuthRegister> {
            RegisterScreenView(
                onRegisterSuccess = { viewModel.setAuthState("loading_register") },
                onNavigateToLogin = { viewModel.setAuthState("login") }
            )
        }

        composable<RouteAuthLoadingRegister> {
            LoadingRegisterView(
                onLoadingFinished = { viewModel.setAuthState("register_success") }
            )
        }

        composable<RouteAuthRegisterSuccess> {
            RegisterSuccessView(
                onExplore = { viewModel.setLoggedIn(true) },
                onCompleteProfile = { viewModel.setAuthState("complete_profile") }
            )
        }

        composable<RouteAuthCompleteProfile> {
            CompleteProfileView(
                viewModel = viewModel,
                onSaveProfileSuccess = { viewModel.setAuthState("profile_success") },
                onBack = { viewModel.setAuthState("register_success") }
            )
        }

        composable<RouteAuthProfileSuccess> {
            ProfileSuccessView(
                onProceed = { viewModel.setLoggedIn(true) }
            )
        }

        composable<RouteAuthForgotPassword> {
            ForgotPasswordScreenView(
                onCodeSent = { viewModel.setAuthState("otp") },
                onBack = { viewModel.setAuthState("login") }
            )
        }

        composable<RouteAuthOtp> {
            OtpScreenView(
                onVerifySuccess = { viewModel.setAuthState("new_password") },
                onBack = { viewModel.setAuthState("forgot_password") }
            )
        }

        composable<RouteAuthNewPassword> {
            NewPasswordScreenView(
                onResetSuccess = { viewModel.setAuthState("password_reset_success") },
                onBack = { viewModel.setAuthState("otp") }
            )
        }

        composable<RouteAuthPasswordResetSuccess> {
            PasswordResetSuccessView(
                onBackToLogin = { viewModel.setAuthState("login") }
            )
        }
    }
}

private fun authStateToRoute(state: String): String? {
    return when (state) {
        "login" -> RouteAuthLogin::class.qualifiedName
        "loading_login" -> RouteAuthLoadingLogin::class.qualifiedName
        "login_success" -> RouteAuthLoginSuccess::class.qualifiedName
        "register" -> RouteAuthRegister::class.qualifiedName
        "loading_register" -> RouteAuthLoadingRegister::class.qualifiedName
        "register_success" -> RouteAuthRegisterSuccess::class.qualifiedName
        "complete_profile" -> RouteAuthCompleteProfile::class.qualifiedName
        "profile_success" -> RouteAuthProfileSuccess::class.qualifiedName
        "forgot_password" -> RouteAuthForgotPassword::class.qualifiedName
        "otp" -> RouteAuthOtp::class.qualifiedName
        "new_password" -> RouteAuthNewPassword::class.qualifiedName
        "password_reset_success" -> RouteAuthPasswordResetSuccess::class.qualifiedName
        else -> null
    }
}
