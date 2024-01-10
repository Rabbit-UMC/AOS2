package com.example.myo_jib_sa.schedule.createScheduleActivity.api.scheduleAdd

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ScheduleAddService {
    companion object {
        private const val authKey = "" //Authorization쓰기!!
    }

    @POST("app/schedule")//?Authorization=$authKey
    fun scheduleAdd(
        @Header("X-ACCESS-TOKEN")
        accessToken: String?,
        @Body requestBody: ScheduleAddRequest
    ) : Call<ScheduleAddResponse>
}


data class ScheduleAddRequest(
    @SerializedName("title")
    var scheduleTitle: String,
    var content: String,
    var startAt: String,
    var endAt: String,
    @SerializedName("when")
    var scheduleWhen: String,
    var missionId: Long?
)