package com.example.myo_jib_sa.mission.API

import com.example.myo_jib_sa.Login.API.LoginResponse
import retrofit2.Call
import retrofit2.http.*

interface MissionITFC {
    //미션 홈
    @GET("app/mission")
    fun MissionHome(): Call<MissionHomeResponse>

    //미션 생성
    @POST("app/mission")
    fun MissionWrite(
        @Body requestBody: MissionWriteRequest
    ):Call<MissionWriteResponse>

    //카테고리별로 미션 확인
    @GET("app/mission/category/{categoryId}")
    fun MissionCategory(
        @Query ("categoryId") categoryId: Int
    ): Call<MissionCategoryResponse>

    //미션 상세보기
    @GET("app/mission/{missionId}")
    fun MissionDetail(
        @Header("X-ACCESS-TOKEN") accessToken: String,
        @Path("missionId") missionId: Long
    ): Call<MissionDetailResponse>

    //신고하기
    @POST("app/mission/report/{missionId}")
    fun MissionReport(
        @Path ("missionId") missionId:Long
    ): Call<MissionReportResponse>

    //미션 같이하기
    @POST("app/mission/{missionId}")
    fun MissionWith(
        @Header("X-ACCESS-TOKEN") accessToken: String,
        @Path ("missionId") missionId:Long
    ): Call<MissionWithResponse>
}