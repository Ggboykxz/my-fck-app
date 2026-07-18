package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

var isDarkMode by mutableStateOf(true)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    onPrimary = BrandNavy,
    primaryContainer = PrimaryGreen.copy(alpha = 0.15f),
    onPrimaryContainer = PrimaryGreen,
    secondary = SoftGreen80,
    onSecondary = BrandNavy,
    secondaryContainer = SoftGreen80.copy(alpha = 0.15f),
    onSecondaryContainer = SoftGreen80,
    tertiary = Grey80,
    onTertiary = BrandNavy,
    tertiaryContainer = Grey80.copy(alpha = 0.15f),
    onTertiaryContainer = Grey80,
    background = BrandNavy,
    onBackground = Color.White,
    surface = Color(0xFF162133),
    onSurface = Color.White,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    error = DarkError,
    onError = DarkOnError,
    outline = DarkOutline,
    inverseSurface = DarkInverseSurface,
    inverseOnSurface = DarkInverseOnSurface,
    inversePrimary = DarkInversePrimary
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = BrandNavy,
    primaryContainer = PrimaryGreen.copy(alpha = 0.12f),
    onPrimaryContainer = BrandNavy,
    secondary = Green40,
    onSecondary = Color.White,
    secondaryContainer = SoftGreen40.copy(alpha = 0.15f),
    onSecondaryContainer = BrandNavy,
    tertiary = Grey40,
    onTertiary = Color.White,
    tertiaryContainer = Grey40.copy(alpha = 0.15f),
    onTertiaryContainer = BrandNavy,
    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    error = LightError,
    onError = LightOnError,
    outline = LightOutline,
    inverseSurface = LightInverseSurface,
    inverseOnSurface = LightInverseOnSurface,
    inversePrimary = LightInversePrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
