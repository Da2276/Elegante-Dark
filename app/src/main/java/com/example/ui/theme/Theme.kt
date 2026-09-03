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

private val DarkColorScheme =
  darkColorScheme(
    primary = ElegantLilacPrimary,
    onPrimary = ElegantPurpleOnPrimary,
    primaryContainer = ElegantPurpleContainer,
    onPrimaryContainer = ElegantLilacOnContainer,
    secondary = ElegantSecondary,
    onSecondary = ElegantOnSecondary,
    secondaryContainer = ElegantSecondaryContainer,
    onSecondaryContainer = ElegantOnSecondaryContainer,
    tertiary = ElegantTertiary,
    onTertiary = ElegantOnTertiary,
    tertiaryContainer = ElegantTertiaryContainer,
    onTertiaryContainer = ElegantOnTertiaryContainer,
    background = ElegantDarkBackground,
    onBackground = ElegantDarkOnSurface,
    surface = ElegantDarkSurface,
    onSurface = ElegantDarkOnSurface,
    surfaceVariant = ElegantDarkSurfaceVariant,
    onSurfaceVariant = ElegantDarkOnSurfaceVariant,
    outline = ElegantDarkOutline,
    outlineVariant = Color(0xFF49454F),
  )

private val LightColorScheme = DarkColorScheme

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

