package com.example.myo_jib_sa.community.Retrofit.manager

import com.example.myo_jib_sa.community.Constance
import com.example.myo_jib_sa.community.Retrofit.post.SimpleResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ManagerRetrofitITFC {

    //메인 미션 이미지 바꾸기
    @PATCH("app/host/main-image/{categoryId}")
    fun EditPhoto(@Header(Constance.author)author:String
                  ,@Path("categoryId") categoryId: Long,
                  @Body request: PatchCategoryImageReq): Call<SimpleResponse>

    //미션 생성
    @POST("app/host/main-mission/{categoryId}")
    fun missionCreate(@Header(Constance.author)author:String
                      ,@Path("categoryId")categoryId:Long
                      ,@Body request: MissionCreateRequest
                      ): Call<SimpleResponse>
}

data class MissionCreateRequest(
    val mainMissionTitle:String,
    val mainMissionContent:String,
    val missionStartTime:String,
    val missionEndTime:String,
    val lastMission:Boolean
)

data class PatchCategoryImageReq(
    val filePath: String
)

data class ManagerMissionJoinRequest(
    val missionTitle:String,
    val missionContent:String,
    val missionStartTime:String,
    val missionEndTime:String,
    val missionImg:String
)