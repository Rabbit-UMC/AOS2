package com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMissionNScheduleDelete

import io.reactivex.annotations.Nullable
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface CurrentMissionNScheduleDeleteService {
    @DELETE("app/mission/my-missions/missionId={missionId}/scheduleIds={scheduleIds}")//?Authorization=$authKey
    fun currentMissionNScheduleDelete(
        @Header("X-ACCESS-TOKEN")
        accessToken: String?,
        @Path("missionId") missionId: String?,
        @Path("scheduleIds") scheduleIds : String
    ) : Call<CurrentMissionNScheduleDeleteResponse>
}
