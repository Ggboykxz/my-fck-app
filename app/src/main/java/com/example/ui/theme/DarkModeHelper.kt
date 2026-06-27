package com.example.ui.theme

import android.content.Context
import android.content.SharedPreferences

object DarkModeHelper {
    private const val PREF_NAME = "locall_prefs"
    private const val KEY_DARK_MODE = "dark_mode"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun loadDarkMode(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_DARK_MODE, true)
    }

    fun saveDarkMode(context: Context, isDark: Boolean) {
        prefs(context).edit().putBoolean(KEY_DARK_MODE, isDark).apply()
    }
}
