package com.example.myo_jib_sa.community.Retrofit.communityHome

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface CommunityHomeITFC {
    //홈 API
    @GET("app/home")
    fun home(@Header("Authorization") author: String): Call<HomeResponse>

}