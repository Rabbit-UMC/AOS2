package com.example.myo_jib_sa.Schedule.currentMissionActivity.api.currentMissionSchedule

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface CurrentMissionScheduleService {
    companion object {
        private const val authKey = "" //Authorization쓰기!!
    }

    @GET("app/mission/my-missions/schedule/{missionId}")//?Authorization=$authKey
    fun currentMissionSchedule(
        @Header("X-ACCESS-TOKEN")
        accessToken: String?,
        @Path("missionId") missionId: Long
    ) : Call<CurrentMissionScheduleResponse>
}
