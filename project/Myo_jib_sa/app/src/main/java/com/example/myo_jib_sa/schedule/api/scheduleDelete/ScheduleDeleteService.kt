package com.example.myo_jib_sa.schedule.api.scheduleDelete

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ScheduleDeleteService {
    @DELETE("app/schedule/{schedulId}")
    fun scheduleDelete(
        @Path("scheduleId") scheduleId: Long
    ) : Call<ScheduleDeleteResponse>
}