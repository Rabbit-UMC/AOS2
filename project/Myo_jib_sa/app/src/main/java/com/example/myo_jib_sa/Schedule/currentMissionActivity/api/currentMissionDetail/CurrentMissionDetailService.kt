package com.example.myo_jib_sa.Schedule.currentMissionActivity.api.currentMissionDetail

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface CurrentMissionDetailService {
    companion object {
        private const val authKey = "" //Authorization쓰기!!
    }

    @GET("app/mission/my-missions/{missionId}")//?Authorization=$authKey
    fun currentMissionDetail(
        @Header("X-ACCESS-TOKEN")
        accessToken: String?,
        @Path("missionId") missionId: Long
    ) : Call<CurrentMissionDetailResponse>
}
