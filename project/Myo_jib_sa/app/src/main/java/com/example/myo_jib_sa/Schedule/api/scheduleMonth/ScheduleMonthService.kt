package com.example.myo_jib_sa.Schedule.API.scheduleDelete

import com.example.myo_jib_sa.Schedule.API.scheduleMonth.ScheduleMonthResponse
import retrofit2.Call
import retrofit2.http.*

interface ScheduleMonthService {
    @GET("app/schedule/month/{month}")
    fun scheduleMonth(
        @Header("X-ACCESS-TOKEN")
        accessToken: String?,
        @Path("month") month: String?
    ) : Call<ScheduleMonthResponse>

}