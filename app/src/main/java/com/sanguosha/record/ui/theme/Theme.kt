package com.sanguosha.record.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Gold80,
    onPrimary = ParchmentDark,
    primaryContainer = InkBrownLight,
    onPrimaryContainer = Gold80,
    secondary = Red80,
    onSecondary = ParchmentDark,
    secondaryContainer = Color(0xFF3A2020),
    onSecondaryContainer = Red80,
    tertiary = Teal80,
    onTertiary = ParchmentDark,
    tertiaryContainer = Color(0xFF1E3530),
    onTertiaryContainer = Teal80,
    background = ParchmentDark,
    onBackground = Color(0xFFE8E0D4),
    surface = InkBrown,
    onSurface = Color(0xFFE8E0D4),
    surfaceVariant = InkBrownLight,
    onSurfaceVariant = Color(0xFFC8BEB0),
    outline = Color(0xFF6B6058),
    outlineVariant = Color(0xFF4A4038)
)

private val LightColorScheme = lightColorScheme(
    primary = Red40,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD4),
    onPrimaryContainer = Color(0xFF3B0800),
    secondary = Gold40,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF0D0),
    onSecondaryContainer = Color(0xFF3A2E00),
    tertiary = Teal40,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFCCE8DF),
    onTertiaryContainer = Color(0xFF002019),
    background = ParchmentLight,
    onBackground = Color(0xFF201A17),
    surface = Color(0xFFFFFBF5),
    onSurface = Color(0xFF201A17),
    surfaceVariant = Color(0xFFF0E6D8),
    onSurfaceVariant = Color(0xFF504540),
    outline = Color(0xFF827570),
    outlineVariant = Color(0xFFD5C4BC)
)

@Composable
fun SanguoshaRecordTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,  // 默认关闭动态取色，保持三国杀风格
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
