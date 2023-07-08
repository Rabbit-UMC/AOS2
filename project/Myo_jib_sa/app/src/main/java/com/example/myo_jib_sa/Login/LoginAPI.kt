package com.example.myo_jib_sa.Login

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginAPI {
    @POST("user/login")
    fun Login(
        @Body requestBody: LoginRequest
    ): Call<LoginResponse>
}