package com.example.myo_jib_sa.community.Retrofit.BoardPost

import com.example.myo_jib_sa.community.Retrofit.Constance
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PostBoardRetrofitITFC {

    //게시판에 게시물 띄우기
    @GET("app/article")
    fun board(
        @Header(Constance.author)author:String,
        @Query("page") page: Int,
        @Query("categoryId")categoryId: Long): Call<PostBoardResponse>
}