package com.example.camera.util

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Very small helper to persist JWT in SharedPreferences.
 */
class TokenStore(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    private val _tokenFlow = MutableStateFlow(prefs.getString(KEY, null))
    val tokenFlow = _tokenFlow.asStateFlow()

    fun get(): String? = _tokenFlow.value

    suspend fun set(token: String) {
        prefs.edit().putString(KEY, token).apply()
        _tokenFlow.value = token
    }

    suspend fun clear() {
        prefs.edit().remove(KEY).apply()
        _tokenFlow.value = null
    }

    private companion object { const val KEY = "jwt" }
}