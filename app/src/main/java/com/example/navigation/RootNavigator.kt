package com.example.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.auth.AuthNavigator
import com.example.ui.viewmodel.RentalViewModel

@Composable
fun RootNavigator(viewModel: RentalViewModel, context: Context) {
    val prefs = context.getSharedPreferences("locall_prefs", Context.MODE_PRIVATE)
    var showOnboarding by remember {
        mutableStateOf(!prefs.getBoolean("onboarding_done", false))
    }

    if (showOnboarding) {
        OnboardingScreen(
            onComplete = {
                prefs.edit().putBoolean("onboarding_done", true).apply()
                showOnboarding = false
            }
        )
    } else {
        AuthNavigator(viewModel = viewModel)
    }
}
