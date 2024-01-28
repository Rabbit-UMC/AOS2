package com.example.myo_jib_sa.signup.api

import com.example.myo_jib_sa.signup.api.LoginResponse
import com.example.myo_jib_sa.signup.api.SignUpRequest
import com.example.myo_jib_sa.signup.api.SignUpResponse
import retrofit2.Call
import retrofit2.http.*

interface SignUpTFC {
    // 로그인
    @GET("app/users/kakao-login")
    fun getLogin(
        @Header("Authorization") Authorization: String,
    ): Call<LoginResponse>

    // 회원가입
    @POST("app/users/sign-up")
    fun postSignUp(
        @Header("Authorization") Authorization: String,
        @Body requestBody: SignUpRequest
    ): Call<SignUpResponse>

}