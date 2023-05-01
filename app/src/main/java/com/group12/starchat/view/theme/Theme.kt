package com.group12.starchat.view.theme

import android.app.Activity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorPalette = darkColors(
    // primary = Purple200,
    // primaryVariant = Purple700,
    // secondary = Teal200

    primary = ColorOb,
    background = Black,
    onBackground = ColorP,
    onSurface = DarkRed,
    secondary = ColorOs,
)

private val LightColorPalette = lightColors(
    // primary = Purple500,
    // primaryVariant = Purple700,
    // secondary = Teal200

    // Rainbow Colors to see what is coloured
    /*
    primary = Red,
    background = Yellow,
    surface = Orange,
    onSurface = Green,
    onBackground = Blue,
    secondary = Purple,
     */

    primary = ColorP,
    background = ColorBg,
    onBackground = ColorOb,
    onSurface = LightRed,
    secondary = ColorOs,

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun StarChatTheme(darkTheme: Boolean, content: @Composable () -> Unit) {

    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val view = LocalView.current
    val window = (view.context as Activity).window

    if (!view.isInEditMode) {
        SideEffect {
            if (darkTheme) {
                window.statusBarColor = colors.onBackground.toArgb()
                window.navigationBarColor = colors.onBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            } else {
                window.statusBarColor = colors.onBackground.toArgb()
                window.navigationBarColor = colors.onBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
            }
        }
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}