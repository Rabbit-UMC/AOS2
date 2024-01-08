package com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMissionDetail

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface CurrentMissionDetailService {
    @GET("app/mission/my-missions/{missionId}")//?Authorization=$authKey
    fun currentMissionDetail(
        @Header("X-ACCESS-TOKEN")
        accessToken: String?,
        @Path("missionId") missionId: Long
    ) : Call<CurrentMissionDetailResponse>
}
