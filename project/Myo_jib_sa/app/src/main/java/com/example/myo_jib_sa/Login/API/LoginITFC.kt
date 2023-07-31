package com.example.myo_jib_sa.Login.API

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginITFC {

    @POST("/app/users/kakao-login")
    fun Login(
        @Query("client_id") clientId: String,
        @Query("redirect_uri") redirectUri: String,
        @Query("response_type") responseType: String
    ): Call<LoginResponse>

    @POST("/app/users/sign-up")
    fun SignUp(
        @Header("X-ACCESS-TOKEN")
        accessToken: String,
        @Body requestBody: SignUpRequest
    ): Call<SignUpResponse>

}