package com.example.myo_jib_sa.mission.api

import retrofit2.Call
import retrofit2.http.*

interface MissionAPI {
    // 일반 미션 리스트 조회
    @GET("app/mission")
    fun getMissionList(
        @Query("page") page: Int
    ): Call<MissionListResponse>

    // 일반 미션 카테고리 리스트 조회
    @GET("app/mission/category")
    fun getCategoryList(): Call<MissionCategoryListResponse>

    //미션 생성
    @POST("app/mission")
    fun postMissionCreate(
        @Body requestBody: MissionCreateRequests
    ):Call<MissionCreateResponse>

    // 카테고리 별 미션 조회
    @GET("app/mission/category/{categoryId}")
    fun getMissionListByCategory(
        @Path ("categoryId") categoryId: Int,
        @Query("page") page: Int
    ): Call<MissionListResponse>

    //미션 상세보기
    @GET("app/mission/{missionId}")
    fun getMissionDetail(
        @Path("missionId") missionId: Long
    ): Call<MissionDetailResponse>

    //신고하기
    @POST("app/mission/report/{missionId}")
    fun postMissionReport(
        @Path ("missionId") missionId:Long
    ): Call<MissionReportResponse>

    //미션 같이하기
    @POST("app/mission/{missionId}")
    fun postMissionWith(
        @Path ("missionId") missionId:Long
    ): Call<MissionWithResponse>
}