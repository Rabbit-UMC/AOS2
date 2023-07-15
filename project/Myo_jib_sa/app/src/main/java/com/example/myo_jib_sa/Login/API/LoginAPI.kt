package com.example.myo_jib_sa.Login.API

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginAPI {
    @POST("user/login")
    fun Login(
        @Query("client_id") clientId: String,
        @Query("redirect_uri") redirectUri: String,
        @Query("response_type") responseType: String
    ): Call<LoginResponse>
}