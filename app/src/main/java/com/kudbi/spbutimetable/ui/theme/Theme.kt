package com.kudbi.spbutimetable.ui.theme

import android.annotation.SuppressLint
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.kudbi.spbutimetable.domain.entities.ThemeManager
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@SuppressLint("ConflictingOnColor")
private val DarkColorPalette = darkColors(
    primary = Orange,
    primaryVariant = Orange,
    secondary = Dark,
    onSecondary = Dark,
    surface = Dark,
    background = DarkMD,
    onBackground = DarkMd8,
    onSurface = White
)

private val LightColorPalette = lightColors(
    primary = Orange,
    primaryVariant = Orange,
    secondary = Cream,
    onSecondary = Orange,
    surface = White,
    background = DarkWhite,
    onBackground = DarkWhite,
    onSurface = Color.Black

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
fun SPBUTimetableTheme(
    content: @Composable () -> Unit,
) {
    val darkTheme by ThemeManager.darkTheme.collectAsState()

    val systemUiController = rememberSystemUiController()
    if (darkTheme) {
        systemUiController.setStatusBarColor(
            color = Dark
        )
        systemUiController.setNavigationBarColor(
            color = DarkMD
        )
    } else {
        systemUiController.setStatusBarColor(
            color = Orange
        )
        systemUiController.setNavigationBarColor(
            color = DarkWhite
        )
    }

    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}