package com.example.myo_jib_sa.community.api.manager

import com.example.myo_jib_sa.base.BaseResponse
import com.example.myo_jib_sa.community.Constance
import com.example.myo_jib_sa.community.api.post.SimpleResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ManagerRetrofitITFC {

    //메인 미션 이미지 바꾸기
    @PATCH("app/host/main-image/{categoryId}")
    fun EditPhoto(@Path("categoryId") categoryId: Long,
                  @Body request: PatchCategoryImageReq): Call<BaseResponse>

    //미션 생성
    @POST("app/host/main-mission/{categoryId}")
    fun missionCreate(@Path("categoryId")categoryId:Long
                      ,@Body request: MissionCreateRequest
                      ): Call<BaseResponse>

    @GET("app/host/main-mission/{mainMissionId}")
    fun joinManagerMission(@Path("mainMissionId")mainMissionId:Long
    ): Call<JoinManagerMissionResponse>
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

data class JoinManagerMissionResponse(
    val userName:String,
    val missionImageUrl:String,
    val missionTitle:String,
    val missionStartDay:String,
    val missionEndDay:String,
    val memo:String
):BaseResponse()
