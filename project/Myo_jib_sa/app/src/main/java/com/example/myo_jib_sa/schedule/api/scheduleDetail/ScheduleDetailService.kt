package com.example.myo_jib_sa.schedule.api.scheduleDetail

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ScheduleDetailService {
    @GET("app/schedule/{scheduleId}")
    fun scheduleDetail(
        @Header("X-ACCESS-TOKEN")
        accessToken: String,
        @Path("scheduleId") scheduleId: Long
    ) : Call<ScheduleDetailResponse>
}