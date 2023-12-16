package com.example.myo_jib_sa.base

import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.spfManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()

        // Retrieve the access token from wherever it is stored (e.g., SharedPreferences)
        val accessToken = spfManager.getUserToken()

        // Add the "Bearer" token to the header
        val modifiedRequest: Request = originalRequest.newBuilder()
            .header("X-ACCESS-TOKEN", "Bearer $accessToken")
            .build()

        return chain.proceed(modifiedRequest)
    }
}