package com.example.agenthq.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = CopilotBlue,
    onPrimary = GitHubCanvas,
    primaryContainer = Color(0xFFDDF4FF),
    onPrimaryContainer = Color(0xFF0550AE),
    secondary = CopilotPurple,
    onSecondary = GitHubCanvas,
    secondaryContainer = Color(0xFFEADDFF),
    onSecondaryContainer = Color(0xFF5A2CA0),
    tertiary = CopilotGreen,
    onTertiary = GitHubCanvas,
    tertiaryContainer = Color(0xFFDCFCE7),
    onTertiaryContainer = Color(0xFF116329),
    error = CopilotRed,
    onError = GitHubCanvas,
    background = GitHubCanvas,
    onBackground = Color(0xFF1F2328),
    surface = GitHubSurface,
    onSurface = Color(0xFF1F2328),
    surfaceVariant = GitHubSurface,
    onSurfaceVariant = GitHubMuted,
    outline = GitHubBorder,
    outlineVariant = Color(0xFFEAECEF)
)

private val DarkColorScheme = darkColorScheme(
    primary = CopilotBlueDark,
    onPrimary = Color(0xFF002155),
    primaryContainer = Color(0xFF003272),
    onPrimaryContainer = Color(0xFFADD6FF),
    secondary = Color(0xFFD2BCFF),
    onSecondary = Color(0xFF3B1F6E),
    secondaryContainer = Color(0xFF523685),
    onSecondaryContainer = Color(0xFFEADDFF),
    tertiary = CopilotGreenDark,
    onTertiary = Color(0xFF00391C),
    tertiaryContainer = Color(0xFF00522A),
    onTertiaryContainer = Color(0xFF96D5A8),
    error = CopilotRedDark,
    onError = Color(0xFF690005),
    background = GitHubCanvasDark,
    onBackground = Color(0xFFE6EDF3),
    surface = GitHubSurfaceDark,
    onSurface = Color(0xFFE6EDF3),
    surfaceVariant = GitHubSurfaceDark,
    onSurfaceVariant = GitHubMutedDark,
    outline = GitHubBorderDark,
    outlineVariant = Color(0xFF21262D)
)

@Composable
fun AgentHQTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AgentHQTypography,
        content = content
    )
}
