package com.example.myo_jib_sa.community.Retrofit.manager

import com.bumptech.glide.load.resource.SimpleResource
import com.example.myo_jib_sa.community.Retrofit.Constance
import com.example.myo_jib_sa.community.Retrofit.post.SimpleResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ManagerRetrofitITFC {

    //메인 미션 이미지 바꾸기
    @PATCH("app/admin/main-image/{categoryId}")
    fun EditPhoto(@Header(Constance.author)author:String
                  ,@Body filePath:String
            ,@Path("categoryId") categoryId: Long): Call<SimpleResponse>

    //미션 생성
    @POST("/app/host/main-mission/{categoryId}")
    fun missionCreate(@Header(Constance.author)author:String
                      ,@Body request: MissionCreateRequest
                      ,@Path("categoryId")categoryId:Long): Call<SimpleResponse>
}

data class MissionCreateRequest(
    val mainMissionTitle:String,
    val mainMissionContent:String,
    val missionStartTime:String,
    val missionEndTime:String,
    val lastMission:Boolean
)