package com.example.myo_jib_sa.signup.api

import com.example.myo_jib_sa.mypage.api.LogoutResponse
import com.example.myo_jib_sa.mypage.api.UnregisterResponse
import retrofit2.Call
import retrofit2.http.*

interface MemeberApi {
    // 로그인
    @GET("app/users/kakao-login")
    fun getLogin(
        @Header("Authorization") auth: String,
    ): Call<LoginResponse>


    // 회원가입
    @POST("app/users/sign-up")
    fun postSignUp(
        @Header("Authorization") auth: String,
        @Body requestBody: SignUpRequest
    ): Call<SignUpResponse>

}