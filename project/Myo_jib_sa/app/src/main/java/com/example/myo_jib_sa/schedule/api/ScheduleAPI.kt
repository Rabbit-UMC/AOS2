package com.example.myo_jib_sa.schedule.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ScheduleAPI {
    @GET("app/schedule")
    fun getScheduleHome(
    ) : Call<ScheduleHomeResponse>

    @GET("app/schedule/{scheduleId}")
    fun getScheduleDetail(
        @Path("scheduleId") scheduleId: Long
    ):Call<ScheduleDetailResponse>

    @GET("app/schedule/when/{when}")
    fun getScheduleOfDay(
        @Path("when") scheduleWhen:String?
    ):Call<ScheduleOfDayResponse>

    @GET("app/schedule/month/{month}")
    fun scheduleMonth(
        @Path("month") month: String?
    ) : Call<ScheduleMonthResponse>

    @POST("app/schedule")
    fun createSchedule(
        @Body requestBody: CreateScheduleRequest
    ) : Call<CreateScheduleResponse>

    @PATCH("app/schedule/{scheduleId}")
    fun updateSchedule(
        @Path("scheduleId") scheduleId: Long,
        @Body requestBody: UpdateScheduleRequest
    ) : Call<UpdateScheduleResponse>

    @DELETE("app/schedule/{scheduleIds}")
    fun deleteSchedule(
        @Path("scheduleIds") scheduleId: Long
    ) : Call<DeleteScheduleResponse>

}