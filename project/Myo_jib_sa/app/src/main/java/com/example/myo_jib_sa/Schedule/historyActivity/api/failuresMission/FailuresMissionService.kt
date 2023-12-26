package com.example.myo_jib_sa.Schedule.currentMissionActivity.api.currentMission

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface FailuresMissionService {
    companion object {
        private const val authKey = "" //Authorization쓰기!!
    }

    @GET("app/mission/failures")//?Authorization=$authKey
    fun failuresMission(
        @Header("X-ACCESS-TOKEN")
        accessToken: String?
    ) : Call<HistoryMissionResponse>
}
