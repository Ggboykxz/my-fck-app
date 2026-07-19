package com.example.preferences

import android.content.Context
import android.content.SharedPreferences

object UserPreferences {
    private const val PREFS_NAME = "locall_user_prefs"
    
    private fun getPrefs(context: Context): SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    fun isDarkMode(context: Context): Boolean = getPrefs(context).getBoolean("dark_mode", true)
    fun setDarkMode(context: Context, dark: Boolean) = getPrefs(context).edit().putBoolean("dark_mode", dark).apply()
    
    fun notificationsEnabled(context: Context): Boolean = getPrefs(context).getBoolean("notifications_enabled", true)
    fun setNotificationsEnabled(context: Context, enabled: Boolean) = getPrefs(context).edit().putBoolean("notifications_enabled", enabled).apply()
    
    fun getCurrency(context: Context): String = getPrefs(context).getString("currency", "FCFA") ?: "FCFA"
    fun setCurrency(context: Context, currency: String) = getPrefs(context).edit().putString("currency", currency).apply()
    
    fun getFavoriteCategories(context: Context): Set<String> = getPrefs(context).getStringSet("fav_categories", emptySet()) ?: emptySet()
    fun setFavoriteCategories(context: Context, categories: Set<String>) = getPrefs(context).edit().putStringSet("fav_categories", categories).apply()
    
    fun priceAlertsEnabled(context: Context): Boolean = getPrefs(context).getBoolean("price_alerts", false)
    fun setPriceAlertsEnabled(context: Context, enabled: Boolean) = getPrefs(context).edit().putBoolean("price_alerts", enabled).apply()
    
    fun onboardingDone(context: Context): Boolean = getPrefs(context).getBoolean("onboarding_done", false)
    fun setOnboardingDone(context: Context, done: Boolean) = getPrefs(context).edit().putBoolean("onboarding_done", done).apply()
    
    fun getProfileCompletion(context: Context): Int = getPrefs(context).getInt("profile_completion", 0)
    fun setProfileCompletion(context: Context, percent: Int) = getPrefs(context).edit().putInt("profile_completion", percent).apply()
    
    fun dataSavingMode(context: Context): Boolean = getPrefs(context).getBoolean("data_saving", false)
    fun setDataSavingMode(context: Context, enabled: Boolean) = getPrefs(context).edit().putBoolean("data_saving", enabled).apply()
    
    fun getPriceAlertThreshold(context: Context): Int = getPrefs(context).getInt("price_alert_threshold", 100000)
    fun setPriceAlertThreshold(context: Context, threshold: Int) = getPrefs(context).edit().putInt("price_alert_threshold", threshold).apply()
}
