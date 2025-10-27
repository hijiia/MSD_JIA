package com.example.camera

import android.content.Context
import androidx.core.content.edit

/**
 * read and clear JWT
 * get token
 */
object TokenStore {
    private const val PREF = "auth_prefs"
    private const val KEY = "jwt"

    fun save(context: Context, token: String) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit {
            putString(KEY, token)
        }
    }

    fun get(context: Context): String? =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY, null)

    fun clear(context: Context) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit {
            remove(KEY)
        }
    }

    fun isLoggedIn(context: Context): Boolean = get(context) != null
}