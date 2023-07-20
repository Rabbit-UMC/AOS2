package com.example.myo_jib_sa.schedule.api.scheduleHome

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ScheduleHomeService {
    @GET("app/schedule")
    fun scheduleHome(
        @Header("X-ACCESS-TOKEN")
        accessToken: String,
    ) : Call<ScheduleHomeResponse>
}