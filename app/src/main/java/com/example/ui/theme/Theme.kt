package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    secondary = SoftGreen80,
    tertiary = Grey80,
    background = BrandNavy,
    surface = Color(0xFF162133),
    onPrimary = BrandNavy,
    onSecondary = Color.White,
    onTertiary = BrandNavy,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    secondary = SoftGreen80,
    tertiary = Grey80,
    background = BrandNavy,
    surface = Color(0xFF162133),
    onPrimary = BrandNavy,
    onSecondary = Color.White,
    onTertiary = BrandNavy,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
