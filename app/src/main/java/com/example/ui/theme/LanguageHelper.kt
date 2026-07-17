package com.example.ui.theme

import android.content.Context
import android.content.SharedPreferences
import java.util.Locale

object LanguageHelper {
    private const val PREFS_NAME = "locall_language"
    private const val KEY_LANGUAGE = "language"

    enum class AppLanguage(val code: String, val displayName: String, val nativeName: String, val flag: String) {
        FRENCH("fr", "Français", "Français", "\uD83C\uDDEB\uD83C\uDDF7"),
        ENGLISH("en", "English", "English", "\uD83C\uDDEC\uD83C\uDDE7"),
        GABONESE("gbe", "Gbë̱", "Gbë̱", "\uD83C\uDDEC\uD83C\uDDEC")
    }

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadLanguage(context: Context): AppLanguage {
        val code = prefs(context).getString(KEY_LANGUAGE, AppLanguage.FRENCH.code) ?: AppLanguage.FRENCH.code
        return AppLanguage.entries.find { it.code == code } ?: AppLanguage.FRENCH
    }

    fun saveLanguage(context: Context, language: AppLanguage) {
        prefs(context).edit().putString(KEY_LANGUAGE, language.code).apply()
    }

    fun applyLanguage(context: Context, language: AppLanguage): Context {
        val locale = Locale(language.code)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        return context.createConfigurationContext(config)
    }
}
