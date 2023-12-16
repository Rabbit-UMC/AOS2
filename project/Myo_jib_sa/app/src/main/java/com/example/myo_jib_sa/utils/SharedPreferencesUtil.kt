package com.example.myo_jib_sa.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesUtil(context: Context) {
    private val spfManager: SharedPreferences =
        context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)

    fun spfClear() {
        spfManager.edit().clear().apply()
    }

    // userToken 메서드
    fun checkUserToken(): Boolean = spfManager.contains("X_ACCESS_TOKEN")
    fun getUserToken(): String = spfManager.getString("X_ACCESS_TOKEN", "").toString()
    fun setUserToken(token: String) {
        spfManager.edit().putString("user_token", "Bearer $token").apply()
    }

}
