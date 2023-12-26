package com.example.myo_jib_sa.Schedule.api.scheduleDelete

import retrofit2.Call
import retrofit2.http.*

interface ScheduleMonthService {
    companion object {
        private const val authKey =""  //Authorization쓰기!!


    }
    @GET("app/schedule/month/{month}")
    fun scheduleMonth(
        @Header("X-ACCESS-TOKEN")
        accessToken: String?,
        @Path("month") month: String?
    ) : Call<ScheduleMonthResponse>



}