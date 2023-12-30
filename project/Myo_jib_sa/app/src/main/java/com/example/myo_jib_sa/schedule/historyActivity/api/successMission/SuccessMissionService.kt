package com.example.myo_jib_sa.Schedule.historyActivity.api.successMission

import com.example.myo_jib_sa.Schedule.currentMissionActivity.api.currentMission.HistoryMissionResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface SuccessMissionService {
    companion object {
        private const val authKey = "" //Authorization쓰기!!
    }

    @GET("app/mission/success")//?Authorization=$authKey
    fun successMission(
        @Header("X-ACCESS-TOKEN")
        accessToken: String?
    ) : Call<HistoryMissionResponse>
}
