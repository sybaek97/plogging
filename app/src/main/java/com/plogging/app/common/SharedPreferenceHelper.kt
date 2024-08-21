package com.plogging.app.common

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.plogging.app.R

class SharedPreferenceHelper(context: Context, private val key: String, private val value: Any?) {

    private val masterKeyAlias = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    private val preferences = EncryptedSharedPreferences.create(
        context,
        context.getString(R.string.SHARED_PREFERENCE_SETTING),
        masterKeyAlias,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    private val prefEditor = preferences.edit()

    fun prefSetter () {
        when (value) {
            is String -> {
                prefEditor.putString(key, value).apply()
            }
            is Int -> {
                prefEditor.putInt(key, value).apply()
            }
            is Boolean -> {
                prefEditor.putBoolean(key, value).apply()
            }
            is Long -> {
                prefEditor.putLong(key, value).apply()
            }
            is Float -> {
                prefEditor.putFloat(key, value).apply()
            }
            else -> {}
        }
    }
    fun prefGetter(): Any? {
        when(value) {
            is String -> return preferences.getString(key, "")
            is Int -> return preferences.getInt(key, 0)
            is Boolean -> return preferences.getBoolean(key, false)
            is Long -> return preferences.getLong(key, 0L)
            is Float -> return preferences.getFloat(key, 0F)
            else -> {}
        }
        return null
    }





}