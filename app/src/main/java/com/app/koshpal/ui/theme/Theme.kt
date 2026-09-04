package com.app.koshpal.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = Color.White,
    primaryContainer = SubPrimaryColor,
    onPrimaryContainer = OffBlack,
    secondary = SecondaryColor,
    onSecondary = Color.White,
    secondaryContainer = SubSecondaryColor,
    secondaryFixed = SubSecondaryColorVariant,
    onSecondaryContainer = Color.White,
    error = TextRed,
    onError = Color.Red,
    background = Color.White,
    onBackground = OffBlack,
    surface = Color.White,
    onSurface = OffBlack,
    surfaceVariant = SurfaceLightBlue,
    onSurfaceVariant = NeutralDark,
    outline = NeutralLight,
    outlineVariant = DividerLightGrey
)

private val DarkColorScheme = LightColorScheme

data class ExtendedColors(
    val success: Color,
    val successContainer: Color,
    val warning: Color,
    val errorLight: Color,
    val errorDark: Color,
    val info: Color,
    val infoContainer: Color,
    val infoVariant: Color,
    val divider: Color,
    val deleteOuter: Color,
    val deleteInner: Color
)

private val LightExtendedColors = ExtendedColors(
    success = SuccessGreen,
    successContainer = SuccessGreenLight,
    warning = WarningOrange,
    errorLight = ErrorRedLight,
    errorDark = ErrorRedDark,
    info = BrandingBlue,
    infoContainer = IndigoLight,
    infoVariant = IndigoMedium,
    divider = DividerLightGrey,
    deleteOuter = LightRedTint,
    deleteInner = DeepRed
)
private val DarkExtendedColors = LightExtendedColors

val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }


@Composable
fun SetStatusBarAppearance(
    isDarkIcons: Boolean
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isDarkIcons
        }
    }
}

@Composable
fun SetStatusBarVisibility(
    isVisible: Boolean
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            if (isVisible) {
                controller.show(WindowInsetsCompat.Type.statusBars())
            } else {
                controller.hide(WindowInsetsCompat.Type.statusBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }
}

@Composable
fun KoshpalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> LightColorScheme
    }
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
