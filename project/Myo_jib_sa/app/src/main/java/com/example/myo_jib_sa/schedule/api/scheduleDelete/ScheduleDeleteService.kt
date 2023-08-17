package com.example.myo_jib_sa.schedule.api.scheduleDelete

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.myo_jib_sa.BuildConfig
import com.example.myo_jib_sa.schedule.api.scheduleModify.ScheduleModifyService
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