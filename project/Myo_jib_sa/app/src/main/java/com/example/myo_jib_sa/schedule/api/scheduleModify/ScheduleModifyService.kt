package com.example.myo_jib_sa.schedule.api.scheduleModify

import com.example.myo_jib_sa.schedule.api.scheduleOfDay.ScheduleOfDayRequest
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH

interface ScheduleModifyService {
    @PATCH("app/schedule/{scheduleId}")
    fun scheduleModify(
        @Body requestBody: ScheduleModifyRequest
    ) : Call<ScheduleModifyResponse>


}

data class ScheduleModifyRequest(
    val title: String,
    val content: String,
    val startAt: String,
    val endAt: String,
    val missionId: Long
)