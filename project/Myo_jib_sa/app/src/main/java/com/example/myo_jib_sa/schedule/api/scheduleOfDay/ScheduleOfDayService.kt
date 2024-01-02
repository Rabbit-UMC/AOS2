package com.example.myo_jib_sa.schedule.API.scheduleOfDay

import retrofit2.Call
import retrofit2.http.*

interface ScheduleOfDayService {

    @GET("app/schedule/when/{when}")
    fun scheduleOfDay(
        @Header("X-ACCESS-TOKEN")
        accessToken: String?,
        @Path("when") scheduleWhen: String?
    ) : Call<ScheduleOfDayResponse>
}

//data class ScheduleOfDayRequest(
//    @SerializedName("when") val scheduleWhen: String?
//)
