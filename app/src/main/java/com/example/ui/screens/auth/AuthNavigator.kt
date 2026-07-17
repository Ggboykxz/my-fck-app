package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.ui.navigation.AuthNavHost
import com.example.ui.viewmodel.RentalViewModel

// NavHost-based AuthNavigator (production)
@Composable
fun AuthNavigator(viewModel: RentalViewModel) {
    val navController = rememberNavController()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1526))
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 100.dp, y = (-80).dp)
                .size(350.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFF13EC5B).copy(alpha = 0.12f), Color.Transparent)))
                .blur(80.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-100).dp, y = 100.dp)
                .size(350.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFF2563EB).copy(alpha = 0.05f), Color.Transparent)))
                .blur(100.dp)
        )
        AuthNavHost(navController = navController, viewModel = viewModel)
    }
}
