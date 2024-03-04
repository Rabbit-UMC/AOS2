package com.example.myo_jib_sa.base

import android.util.Log
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.sRetrofit
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.spfManager
import com.example.myo_jib_sa.signup.api.LoginResponse
import com.example.myo_jib_sa.signup.api.MemeberApi
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Call
import retrofit2.Callback

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()

        // Retrieve the access token from wherever it is stored (e.g., SharedPreferences)
        val accessToken = spfManager.getAccessToken()
        // Add the "Bearer" token to the header
        val modifiedRequest: Request = originalRequest.newBuilder()
            .header("X-ACCESS-TOKEN", "$accessToken")
            .build()
        var response: Response = chain.proceed(modifiedRequest)

        // JWT4002 응답 코드 확인
        if (response.code() == 401) { // or 다른 특정 에러 코드 확인 방법
            // 리프레시 토큰을 사용하여 새 액세스 토큰을 요청합니다.
            val newTokens = refreshToken() // 이 함수는 새 토큰을 가져오는 로직을 구현해야 합니다.

            if (newTokens != null) {
                spfManager.setAccessToken(newTokens.first)
                spfManager.setRefreshToken(newTokens.second)

                // 새로운 액세스 토큰으로 요청을 다시 구성합니다.
                val newRequest: Request = originalRequest.newBuilder()
                    .header("Authorization", "Bearer ${spfManager.getAccessToken()}")
                    .build()

                // 수정된 요청으로 다시 API 호출을 시도합니다.
                response = chain.proceed(newRequest)
            }
        }

        return response

    }

    private fun refreshToken(): Pair<String, String>? {
        try {
            val call = sRetrofit.create(MemeberApi::class.java)
                .reissueToken(spfManager.getAccessToken(), spfManager.getRefreshToken())
            val response = call.execute() // 동기 호출

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.isSuccess) {
                    return body.result?.let { Pair(it.jwtAccessToken, body.result.jwtRefreshToken) }
                }
            }
        } catch (e: Exception) {
            Log.d("AuthInterceptor", "Refresh token error: ${e.message}")
        }
        return null
    }
}