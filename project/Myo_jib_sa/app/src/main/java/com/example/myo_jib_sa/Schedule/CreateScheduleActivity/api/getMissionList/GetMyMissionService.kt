package com.example.myo_jib_sa.Schedule.CreateScheduleActivity.api.getMissionList

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface GetMyMissionService {
    companion object {
        private const val authKey = "" //Authorization쓰기!!
    }

    @GET("app/mission/my-missions")//?Authorization=$authKey
    fun getMyMission(
        @Header("X-ACCESS-TOKEN")
        accessToken: String?,
    ) : Call<GetMyMissionResponse>
}