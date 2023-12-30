package com.example.myo_jib_sa.login.api

import retrofit2.Call
import retrofit2.http.*

interface LoginITFC {

    @GET("app/users/kakao-login")
    fun getLogin(
        @Header("Authorization") accessToken: String,
    ): Call<LoginResponse>

    @POST("app/users/sign-up")
    fun postSignUp(
        @Body requestBody: SignUpRequest
    ): Call<SignUpResponse>


}