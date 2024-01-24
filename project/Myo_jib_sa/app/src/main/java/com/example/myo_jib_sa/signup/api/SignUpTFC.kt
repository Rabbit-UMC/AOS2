package com.example.myo_jib_sa.login.api

import com.example.myo_jib_sa.signup.api.LoginResponse
import com.example.myo_jib_sa.signup.api.SignUpRequest
import com.example.myo_jib_sa.signup.api.SignUpResponse
import retrofit2.Call
import retrofit2.http.*

interface SignUpTFC {

    @GET("app/users/kakao-login")
    fun getLogin(
        @Header("Authorization") accessToken: String,
    ): Call<LoginResponse>

    @POST("app/users/sign-up")
    fun postSignUp(
        @Body requestBody: SignUpRequest
    ): Call<SignUpResponse>


}