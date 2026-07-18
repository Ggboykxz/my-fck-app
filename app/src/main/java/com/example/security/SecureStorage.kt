package com.example.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object SecureStorage {
    private const val PREFS_NAME = "locall_secure_prefs"

    private fun getPrefs(context: Context) = EncryptedSharedPreferences.create(
        PREFS_NAME,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(context: Context, token: String) {
        getPrefs(context).edit().putString("auth_token", token).apply()
    }

    fun getToken(context: Context): String? = getPrefs(context).getString("auth_token", null)

    fun saveUserId(context: Context, id: Int) {
        getPrefs(context).edit().putInt("user_id", id).apply()
    }

    fun getUserId(context: Context): Int = getPrefs(context).getInt("user_id", -1)

    fun clearAll(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}
