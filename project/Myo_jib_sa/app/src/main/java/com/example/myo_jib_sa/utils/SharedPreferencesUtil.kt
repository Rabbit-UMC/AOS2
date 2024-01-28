package com.example.myo_jib_sa.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SharedPreferencesUtil(context: Context) {
    private val spfManager: SharedPreferences =
        context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)

    fun spfClear() {
        spfManager.edit().clear().apply()
    }

    // accessToken 메서드
    fun checkUserToken(): Boolean = spfManager.contains("accessToken")
    fun getAccessToken(): String = spfManager.getString("accessToken", "").toString()
    fun setAccessToken(token: String) {
        spfManager.edit().putString("accessToken", "$token").apply()
        Log.d("setAccessToken", "${getAccessToken()}")
    }

    fun getRefreshToken(): String = spfManager.getString("refreshToken", "").toString()
    fun setRefreshToken(token: String) {
        spfManager.edit().putString("refreshToken", "$token").apply()
        Log.d("setRefreshToken", "${getRefreshToken()}")
    }

}
