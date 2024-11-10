package com.mateusz.itemswap.helpers

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.mateusz.itemswap.data.user.User
import com.mateusz.itemswap.data.user.UserResponse

class PreferencesHelper(context: Context) {

    private val prefsFileName = "encrypted_prefs"

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        prefsFileName,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val gson = Gson()

    fun setJwtToken(token: String) {
        sharedPreferences.edit().putString("JWT_TOKEN", token).apply()
    }

    fun getJwtToken(): String? {
        return sharedPreferences.getString("JWT_TOKEN", null)
    }

    fun setUserContext(userContext: UserResponse) {
        val jsonString = gson.toJson(userContext)
        sharedPreferences.edit().putString("USER_CONTEXT", jsonString).apply()
    }

    fun getUserContext(): UserResponse? {
        val jsonString = sharedPreferences.getString("USER_CONTEXT", null)
        return jsonString?.let { gson.fromJson(it, UserResponse::class.java) }
    }

    fun removeKey(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}