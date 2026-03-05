package com.example.agenthq.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Brand colors
val CopilotBlue = Color(0xFF0969DA)
val CopilotBlueDark = Color(0xFF54AEFF)
val CopilotPurple = Color(0xFF8250DF)
val CopilotGreen = Color(0xFF1A7F37)
val CopilotGreenDark = Color(0xFF3FB950)
val CopilotRed = Color(0xFFCF222E)
val CopilotRedDark = Color(0xFFFF7B72)
val CopilotYellow = Color(0xFF9A6700)
val CopilotYellowDark = Color(0xFFD29922)

// GitHub-inspired primary palette
val GitHubGreen = Color(0xFF2DA44E)       // primary light
val GitHubGreenDark = Color(0xFF3FB950)   // primary dark
val GitHubBlue = Color(0xFF0969DA)        // secondary light
val GitHubBlueDark = Color(0xFF54AEFF)    // secondary dark
val GitHubRedPrimary = Color(0xFFCF222E)  // tertiary / error

// Neutral
val GitHubCanvas = Color(0xFFFFFFFF)
val GitHubCanvasDark = Color(0xFF0D1117)
val GitHubSurface = Color(0xFFF6F8FA)
val GitHubSurfaceDark = Color(0xFF161B22)
val GitHubBorder = Color(0xFFD0D7DE)
val GitHubBorderDark = Color(0xFF30363D)
val GitHubMuted = Color(0xFF656D76)
val GitHubMutedDark = Color(0xFF8B949E)

internal val LightColorScheme = lightColorScheme(
    primary = GitHubGreen,
    onPrimary = GitHubCanvas,
    primaryContainer = Color(0xFFDCFCE7),
    onPrimaryContainer = Color(0xFF116329),
    secondary = GitHubBlue,
    onSecondary = GitHubCanvas,
    secondaryContainer = Color(0xFFDDF4FF),
    onSecondaryContainer = Color(0xFF0550AE),
    tertiary = GitHubRedPrimary,
    onTertiary = GitHubCanvas,
    tertiaryContainer = Color(0xFFFFEBEB),
    onTertiaryContainer = Color(0xFF8B0000),
    error = GitHubRedPrimary,
    onError = GitHubCanvas,
    background = GitHubCanvas,
    onBackground = Color(0xFF1F2328),
    surface = GitHubCanvas,
    onSurface = Color(0xFF1F2328),
    surfaceVariant = GitHubSurface,
    onSurfaceVariant = GitHubMuted,
    outline = GitHubBorder,
    outlineVariant = Color(0xFFEAECEF)
)

internal val DarkColorScheme = darkColorScheme(
    primary = GitHubGreenDark,
    onPrimary = Color(0xFF00391C),
    primaryContainer = Color(0xFF00522A),
    onPrimaryContainer = Color(0xFF96D5A8),
    secondary = GitHubBlueDark,
    onSecondary = Color(0xFF002155),
    secondaryContainer = Color(0xFF003272),
    onSecondaryContainer = Color(0xFFADD6FF),
    tertiary = CopilotRedDark,
    onTertiary = Color(0xFF690005),
    tertiaryContainer = Color(0xFF93000A),
    onTertiaryContainer = Color(0xFFFFDAD6),
    error = CopilotRedDark,
    onError = Color(0xFF690005),
    background = GitHubCanvasDark,
    onBackground = Color(0xFFE6EDF3),
    surface = GitHubCanvasDark,
    onSurface = Color(0xFFE6EDF3),
    surfaceVariant = GitHubSurfaceDark,
    onSurfaceVariant = GitHubMutedDark,
    outline = GitHubBorderDark,
    outlineVariant = Color(0xFF21262D)
)
