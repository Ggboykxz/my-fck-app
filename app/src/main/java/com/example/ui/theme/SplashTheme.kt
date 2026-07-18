package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun SplashTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = BrandNavy,
            background = BrandNavy,
            surface = BrandNavy,
            onPrimary = Color.White,
            onBackground = Color.White,
            onSurface = Color.White
        ),
        typography = Typography,
        content = content
    )
}
