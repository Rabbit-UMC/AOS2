package com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMission

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface CurrentMissionService {
    companion object {
        private const val authKey = "" //Authorization쓰기!!
    }

    @GET("app/mission/my-missions")//?Authorization=$authKey
    fun currentMission(
        @Header("X-ACCESS-TOKEN")
        accessToken: String
    ) : Call<CurrentMissionResponse>
}
