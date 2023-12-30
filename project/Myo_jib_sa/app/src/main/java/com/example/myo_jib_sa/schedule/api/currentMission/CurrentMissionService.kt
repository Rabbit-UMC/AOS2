package com.example.myo_jib_sa.Schedule.API.currentMission

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface CurrentMissionService {
    companion object {
        private const val authKey = "" //Authorization쓰기!!
    }

    @GET("app/mission/my-missions")
    fun currentMission(
        @Header("X-ACCESS-TOKEN")
        accessToken: String?
    ) : Call<CurrentMissionResponse>
}
