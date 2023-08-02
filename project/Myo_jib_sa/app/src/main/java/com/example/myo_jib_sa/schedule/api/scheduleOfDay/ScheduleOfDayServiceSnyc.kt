package com.example.myo_jib_sa.schedule.api.scheduleOfDay

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.*

interface ScheduleOfDayServiceSnyc {

    @GET("app/schedule/when/{when}")
    fun scheduleOfDay(
        @Header("Authorization")
        accessToken: String,
        @Path("when") scheduleWhen: String?
    ) : Call<ScheduleOfDayResponse>
}

//data class ScheduleOfDayRequest(
//    @SerializedName("when") val scheduleWhen: String?
//)
