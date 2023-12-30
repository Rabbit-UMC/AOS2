package com.example.myo_jib_sa.Schedule.currentMissionActivity.api.currentMissionDelete

import retrofit2.Call
import retrofit2.http.*

interface CurrentMissionDeleteService {
    companion object {
        private const val authKey = "" //Authorization쓰기!!
    }

    @DELETE//("app/mission/my-missions/{missionId}")//?Authorization=$authKey
    fun currentMissionDelete(
        @Header("X-ACCESS-TOKEN")
        accessToken: String?,
        @Url url: String
        //@Path("missionId") missionId:MutableList<Long>
    ) : Call<CurrentMissionDeleteResponse>
}
