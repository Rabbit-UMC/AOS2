package com.example.myo_jib_sa.Login.API

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface SignUpAPI {
    @POST("user/login")
    fun SignUp(
        @Body requestBody: SignUpRequest
    ): Call<SignUpResponse>
}