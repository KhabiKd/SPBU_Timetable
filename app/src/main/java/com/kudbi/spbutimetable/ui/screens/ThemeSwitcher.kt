package com.kudbi.spbutimetable.ui.screens

import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.kudbi.spbutimetable.ui.theme.White

@Composable
fun ThemeSwitcher(
    darkTheme: Boolean,
    onClick: () -> Unit,
    ) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (!darkTheme) Icons.Default.LightMode else Icons.Default.Nightlight,
            contentDescription = "Theme Icon",
            tint = White
        )
    }
}