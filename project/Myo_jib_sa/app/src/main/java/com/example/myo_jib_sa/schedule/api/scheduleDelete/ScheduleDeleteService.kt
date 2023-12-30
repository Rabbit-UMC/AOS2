package com.example.myo_jib_sa.Schedule.API.scheduleDelete

import retrofit2.Call
import retrofit2.http.*

interface ScheduleDeleteService {
    companion object {
        private const val authKey =""  //Authorization쓰기!!


    }
    @DELETE("app/schedule/{scheduleId}")
    fun scheduleDelete(
        @Header("X-ACCESS-TOKEN")
        accessToken: String?,
        @Path("scheduleId") scheduleId: Long
    ) : Call<ScheduleDeleteResponse>


    @DELETE//("app/schedule/{scheduleId}")
    fun scheduleDeleteModifyVer(
        @Header("X-ACCESS-TOKEN")
        accessToken: String?,
        @Url url: String
    ) : Call<ScheduleDeleteResponse>
}