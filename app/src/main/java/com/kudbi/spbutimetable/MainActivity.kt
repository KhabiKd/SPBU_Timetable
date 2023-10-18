package com.kudbi.spbutimetable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.kudbi.spbutimetable.domain.entities.ThemeManager
import com.kudbi.spbutimetable.ui.theme.SPBUTimetableTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeManager.initialize(this)
        setContent {
            SPBUTimetableTheme {
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    TimetableApp()
                }
            }
        }
    }
}