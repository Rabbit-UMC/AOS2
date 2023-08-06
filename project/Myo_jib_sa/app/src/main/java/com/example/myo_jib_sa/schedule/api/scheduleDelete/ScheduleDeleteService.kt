package com.example.myo_jib_sa.schedule.api.scheduleDelete

import com.example.myo_jib_sa.BuildConfig
import com.example.myo_jib_sa.schedule.api.scheduleModify.ScheduleModifyService
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ScheduleDeleteService {
    companion object {
        private const val authKey =  //Authorization쓰기!!
    }
    @DELETE("app/schedule/{scheduleId}")
    fun scheduleDelete(
        @Header("Authorization")
        accessToken: String,
        @Path("scheduleId") scheduleId: Long
    ) : Call<ScheduleDeleteResponse>
}