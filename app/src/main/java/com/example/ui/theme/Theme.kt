package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DominoCyan,
    onPrimary = Color(0xFF000000),
    primaryContainer = DominoCyanContainer,
    onPrimaryContainer = DominoCyanLight,
    secondary = DominoGreen,
    onSecondary = Color(0xFF000000),
    tertiary = DominoAmber,
    onTertiary = Color(0xFF000000),
    background = DarkBgClear,
    onBackground = DarkTextPrimary,
    surface = DarkSurfaceClear,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariantClear,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorderClear,
    outlineVariant = Color(0xFF2E2E33),
    error = DominoRed,
    onError = Color(0xFF000000)
)

private val LightColorScheme = lightColorScheme(
    primary = DominoPrimaryLight,
    onPrimary = DominoOnPrimaryLight,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = DominoSecondaryLight,
    onSecondary = Color.White,
    tertiary = DominoTertiaryLight,
    onTertiary = Color.White,
    background = DominoBackgroundLight,
    onBackground = Color(0xFF0F172A),
    surface = DominoSurfaceLight,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = DominoSurfaceVariantLight,
    onSurfaceVariant = Color(0xFF475569),
    outline = DominoBorderLight,
    outlineVariant = Color(0xFFCBD5E1),
    error = DominoRed,
    onError = Color.White
)

enum class AppThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our specialized industrial theme
    content: @Composable () -> Unit,
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> systemInDark
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

