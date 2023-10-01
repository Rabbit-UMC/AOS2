package com.example.myo_jib_sa.Login.API

import retrofit2.Call
import retrofit2.http.*

interface LoginITFC {

    @GET("app/users/kakao-login")
    fun Login(
        @Header("Authorization") accessToken: String,
        @Query("client_id") clientId: String,
        @Query("redirect_uri") redirectUri: String,
        @Query("response_type") responseType: String
    ): Call<LoginResponse>

    @POST("app/users/sign-up")
    fun SignUp(
        @Header("X-ACCESS-TOKEN")
        accessToken: String,
        @Body requestBody: SignUpRequest
    ): Call<SignUpResponse>


}