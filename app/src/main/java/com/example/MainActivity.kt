package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.ui.screens.MainDashboardViewNavHost
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.isDarkMode
import com.example.ui.theme.DarkModeHelper
import com.example.ui.theme.LanguageHelper
import com.example.ui.viewmodel.RentalViewModel
import com.example.ui.viewmodel.RentalViewModelFactory
import com.example.navigation.RootNavigator

class MainActivity : ComponentActivity() {

    private val viewModel: RentalViewModel by viewModels {
        RentalViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        isDarkMode = DarkModeHelper.loadDarkMode(this)

        val language = LanguageHelper.loadLanguage(this)
        val localizedContext = LanguageHelper.applyLanguage(this, language)

        setContent {
            MyApplicationTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

                    if (!isLoggedIn) {
                        RootNavigator(viewModel = viewModel, context = this@MainActivity)
                    } else {
                        MainDashboardViewNavHost(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
