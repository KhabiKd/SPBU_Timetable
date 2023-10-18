package com.kudbi.spbutimetable.domain.entities

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ThemeManager {
    private const val THEME_PREFS_NAME = "app_prefs"
    private const val THEME_KEY = "selected_theme"

    private val _darkTheme = MutableStateFlow(false)
    val darkTheme: StateFlow<Boolean> get() = _darkTheme.asStateFlow()

    fun initialize(context: Context) {
        val savedTheme = loadSavedTheme(context)
        _darkTheme.value = savedTheme
    }

    fun setDarkTheme(isDarkTheme: Boolean, context: Context) {
        _darkTheme.value = isDarkTheme
        saveTheme(isDarkTheme, context)
    }

    private fun loadSavedTheme(context: Context): Boolean {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getBoolean(THEME_KEY, true)
    }

    private fun saveTheme(isDarkTheme: Boolean, context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        sharedPreferences.edit().putBoolean(THEME_KEY, isDarkTheme).apply()
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(THEME_PREFS_NAME, Context.MODE_PRIVATE)
    }
}