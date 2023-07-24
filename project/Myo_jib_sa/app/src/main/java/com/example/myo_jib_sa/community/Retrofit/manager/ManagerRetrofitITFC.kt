package com.example.myo_jib_sa.community.Retrofit.manager

import com.bumptech.glide.load.resource.SimpleResource
import com.example.myo_jib_sa.community.Retrofit.post.SimpleResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path

interface ManagerRetrofitITFC {
    @PATCH("/app/admin/main-image/{categoryId}")
    fun EditPhoto(@Header("X-ACCESS-TOKEN")author:String
                  ,@Body filePath:String
            ,@Path("categoryId") categoryId: Long): Call<SimpleResponse>
}