package com.example.myo_jib_sa.schedule.api

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface MissionAPI {
    //내 미션 조회
    @GET("app/mission/my-missions")
    fun getMyMission(
    ) : Call<MyMissionResponse>

    //내 미션 상세 조회
    @GET("app/mission/my-missions/{missionId}")
    fun getMyMissionDetail(
        @Path("missionId") missionId: Long
    ) : Call<MyMissionDetailResponse>

    //내 미션 하위 일정 조회
    @GET("app/mission/my-missions/schedule/{missionId}")
    fun getMyMissionSchedule(
        @Path("missionId") missionId: Long
    ) : Call<MyMissionScheduleResponse>

    //내 미션 지우기
    @DELETE("app/mission/my-missions/{missionId}")
    fun deleteMyMission(
        @Path("missionId") missionId:Long
    ) : Call<DeleteMyMissionResponse>

    //내 미션과 하위 일정 지우기
    @DELETE("app/mission/my-missions/missionId={missionId}/scheduleIds={scheduleIds}")
    fun deleteMyMissionNSchedule(
        @Path("missionId") missionId: String?,
        @Path("scheduleIds") scheduleIds : String
    ) : Call<DeleteMyMissionNScheduleResponse>


}