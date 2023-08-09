package com.example.myo_jib_sa.schedule.createScheduleActivity.api.scheduleAdd

import com.example.myo_jib_sa.schedule.api.scheduleModify.ScheduleModifyResponse
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST

interface ScheduleAddService {
    companion object {
        private const val authKey = "" //Authorization쓰기!!
    }

    @POST("app/schedule")//?Authorization=$authKey
    fun scheduleAdd(
        @Header("X-ACCESS-TOKEN")
        accessToken: String,
        @Body requestBody: ScheduleAddRequest
    ) : Call<ScheduleModifyResponse>
}


data class ScheduleAddRequest(
    val title: String,
    val content: String,
    val startAt: String,
    val endAt: String,
    @SerializedName("when")
    val scheduleWhen: String,
    val missionId: Long

)