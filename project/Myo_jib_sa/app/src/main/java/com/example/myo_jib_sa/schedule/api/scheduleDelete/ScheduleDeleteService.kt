package com.example.myo_jib_sa.schedule.api.scheduleDelete

import retrofit2.Call
import retrofit2.http.*

interface ScheduleDeleteService {
    companion object {
        private const val authKey =""  //Authorization쓰기!!


    }
    @DELETE("app/schedule/{scheduleIds}")
    fun scheduleDelete(
        @Header("X-ACCESS-TOKEN")
        accessToken: String?,
        @Path("scheduleIds") scheduleId: Long
    ) : Call<ScheduleDeleteResponse>


    @DELETE("app/schedule/{scheduleIds}")
    fun scheduleDeleteModifyVer(
        @Header("X-ACCESS-TOKEN")
        accessToken: String?,
//        @Url url: MutableList<Long>
        @Path("scheduleIds") scheduleId: MutableList<Long>

    ) : Call<ScheduleDeleteResponse>
}