package com.example.ui.utils

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class WindowSizeClass(
    val width: WindowWidthSize,
    val height: WindowHeightSize
)

enum class WindowWidthSize { Compact, Medium, Expanded }
enum class WindowHeightSize { Compact, Medium, Expanded }

val LocalWindowSizeClass = compositionLocalOf {
    WindowSizeClass(
        width = WindowWidthSize.Compact,
        height = WindowHeightSize.Compact
    )
}

fun windowSizeClassOf(widthDp: Int, heightDp: Int): WindowSizeClass {
    val width = widthDp.dp
    val height = heightDp.dp
    return WindowSizeClass(
        width = when {
            width < 600.dp -> WindowWidthSize.Compact
            width < 840.dp -> WindowWidthSize.Medium
            else -> WindowWidthSize.Expanded
        },
        height = when {
            height < 400.dp -> WindowHeightSize.Compact
            height < 700.dp -> WindowHeightSize.Medium
            else -> WindowHeightSize.Expanded
        }
    )
}

@androidx.compose.runtime.Composable
fun currentWindowSizeClass(): WindowSizeClass {
    val config = LocalConfiguration.current
    return windowSizeClassOf(config.screenWidthDp, config.screenHeightDp)
}
