package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// --- Premium Black and Gold Dark Theme Color Scheme ---
private val StockScannerDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFD700), // Gold
    onPrimary = Color(0xFF040406), // Black
    primaryContainer = Color(0xFFC5A02B), // Darker Gold
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF00E5FF), // Cyan
    onSecondary = Color(0xFF040406),
    background = Color(0xFF040406), // True Black background
    onBackground = Color.White,
    surface = Color(0xFF101016), // Deep Charcoal Surface
    onSurface = Color.White,
    surfaceVariant = Color(0x1AFFFFFF), // Semi-transparent glass
    onSurfaceVariant = Color(0xFFA0A0A8) // TextGrayMuted
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force premium dark theme by default
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = StockScannerDarkColorScheme,
        typography = Typography,
        content = content
    )
}
