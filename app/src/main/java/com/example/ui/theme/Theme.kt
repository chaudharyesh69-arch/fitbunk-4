package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FitbunkDarkColorScheme = darkColorScheme(
    primary = FitbunkLavender,
    onPrimary = FitbunkDeepPurple,
    primaryContainer = FitbunkViolet,
    onPrimaryContainer = FitbunkWhite,
    secondary = FitbunkLavenderLight,
    onSecondary = FitbunkDeepPurple,
    background = FitbunkDeepPurple,
    onBackground = FitbunkWhite,
    surface = FitbunkSurfaceDark,
    onSurface = FitbunkWhite,
    surfaceVariant = FitbunkSurfaceElevated,
    onSurfaceVariant = FitbunkLavenderLight,
    outline = FitbunkCardDarkBorder
)

private val FitbunkLightColorScheme = lightColorScheme(
    primary = FitbunkViolet,
    onPrimary = FitbunkWhite,
    primaryContainer = FitbunkLavenderLight,
    onPrimaryContainer = FitbunkDeepPurple,
    secondary = FitbunkLavender,
    onSecondary = FitbunkDeepPurple,
    background = FitbunkLightBg,
    onBackground = FitbunkTextPrimary,
    surface = FitbunkWhite,
    onSurface = FitbunkTextPrimary,
    surfaceVariant = Color(0xFFF3ECFC),
    onSurfaceVariant = FitbunkTextSecondary,
    outline = FitbunkCardBorder
)

@Composable
fun FitbunkTheme(
    darkTheme: Boolean = false, // Fashion tech app defaults to signature vibrant light/purple styling
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) FitbunkDarkColorScheme else FitbunkLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = FitbunkTheme(darkTheme = darkTheme, content = content)
