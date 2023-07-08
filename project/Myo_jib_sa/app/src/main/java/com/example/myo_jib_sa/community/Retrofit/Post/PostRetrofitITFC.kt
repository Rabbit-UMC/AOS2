package com.example.myo_jib_sa.community.Retrofit.Post

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface PostRetrofitITFC {

    //게시판에 게시물 띄우기
    @GET("app/article")
    fun board(@Header("Authorization")author:String): Call<PostBoardResponse>
}