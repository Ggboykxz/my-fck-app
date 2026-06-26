package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

var isDarkMode by mutableStateOf(true)

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

private val LightColorScheme = lightColorScheme(
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
    val colorScheme = when {
        dynamicColor && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S -> {
            if (darkTheme) DarkColorScheme else LightColorScheme
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
