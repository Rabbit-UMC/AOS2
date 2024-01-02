package com.example.myo_jib_sa.community.api.communityHome

import com.example.myo_jib_sa.community.Constance
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface CommunityHomeITFC {
    //홈 API
    @GET("app/home/v2")
    fun home(@Header(Constance.author) author: String): Call<HomeResponse>

}