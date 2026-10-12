package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ModernLightColorScheme = lightColorScheme(
    primary = CurioPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEEF2FF),
    onPrimaryContainer = Color(0xFF312E81),
    secondary = CurioGold,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = CurioTeal,
    onTertiary = Color.White,
    background = CurioBackgroundLight,
    onBackground = CurioTextPrimaryLight,
    surface = CurioSurfaceLight,
    onSurface = CurioTextPrimaryLight,
    surfaceVariant = CurioSurfaceElevatedLight,
    onSurfaceVariant = CurioTextSecondaryLight,
    outline = CurioSurfaceBorderLight
)

private val ModernDarkColorScheme = darkColorScheme(
    primary = CurioPrimaryDark,
    onPrimary = Color(0xFF1E1B4B),
    primaryContainer = Color(0xFF312E81),
    onPrimaryContainer = Color(0xFFEEF2FF),
    secondary = CurioGold,
    onSecondary = Color(0xFF451A03),
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = Color(0xFFFEF3C7),
    tertiary = CurioTeal,
    onTertiary = Color(0xFF042F2E),
    background = CurioObsidian,
    onBackground = CurioTextPrimaryDark,
    surface = CurioSurfaceDark,
    onSurface = CurioTextPrimaryDark,
    surfaceVariant = CurioSurfaceElevatedDark,
    onSurfaceVariant = CurioTextSecondaryDark,
    outline = CurioSurfaceBorderDark
)

@Composable
fun CurioTheme(
    darkTheme: Boolean = false, // White theme by default as requested!
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ModernDarkColorScheme else ModernLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CurioTypography,
        content = content
    )
}
