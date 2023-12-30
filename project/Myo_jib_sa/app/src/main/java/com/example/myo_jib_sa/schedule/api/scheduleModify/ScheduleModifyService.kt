package com.example.myo_jib_sa.Schedule.api.scheduleModify

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path

interface ScheduleModifyService {
    companion object {
        private const val authKey = "" //Authorization쓰기!!
    }

    @PATCH("app/schedule/{scheduleId}")//?Authorization=$authKey
    fun scheduleModify(
        @Header("X-ACCESS-TOKEN")
        accessToken: String?,
        @Path("scheduleId") scheduleId: Long,
        @Body requestBody: ScheduleModifyRequest
    ) : Call<ScheduleModifyResponse>


}

data class ScheduleModifyRequest(
    val title: String,
    val content: String,
    val startAt: String,
    val endAt: String,
    @SerializedName("when")
    val scheduleWhen: String,
    val missionId: Long?
)