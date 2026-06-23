package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
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

private val LightColorScheme =
  darkColorScheme( // Use DarkColorScheme as base to enforce the Sophisticated Dark look
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
  darkTheme: Boolean = true, // Force dark theme for Sophisticated Dark design
  dynamicColor: Boolean = false, // Disable dynamic system-wide color overlays to preserve Gabon LocAll brand palette
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
