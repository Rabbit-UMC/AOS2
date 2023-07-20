package com.example.myo_jib_sa.schedule.api.scheduleOfDay

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ScheduleOfDayService {

    @GET("app/schedule/when")
    fun scheduleOfDay(
        @Body requestBody: ScheduleOfDayRequest
    ) : Call<ScheduleOfDayResponse>
}

data class ScheduleOfDayRequest(
    @SerializedName("when") val scheduleWhen: String,
)
