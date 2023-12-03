package com.example.myo_jib_sa.mission.API

import retrofit2.Call
import retrofit2.http.*

interface MissionITFC {
    // 일반 미션 리스트 조회
    @GET("app/mission")
    fun getMissionList(): Call<MissionListResponse>

    // 일반 미션 카테고리 리스트 조회
    @GET("app/mission/category")
    fun getCategoryList(): Call<MissionCategoryListResponse>

    //미션 생성
    @POST("app/mission")
    fun MissionWrite(
        @Header("X-ACCESS-TOKEN") accessToken: String,
        @Body requestBody: MissionWriteRequest
    ):Call<MissionWriteResponse>

    // 카테고리 별 미션 조회
    @GET("app/mission/category/{categoryId}")
    fun getMissionByCategory(
        @Header("X-ACCESS-TOKEN") accessToken: String,
        @Path ("categoryId") categoryId: Int
    ): Call<MissionByCategoryResponse>

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