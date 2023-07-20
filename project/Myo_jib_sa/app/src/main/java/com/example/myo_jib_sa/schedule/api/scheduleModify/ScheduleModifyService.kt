package com.example.myo_jib_sa.schedule.api.scheduleModify

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH

interface ScheduleModifyService {
    companion object {
        private const val authKey = "" //Authorization쓰기!!
    }

    @PATCH("app/schedule/{scheduleId}?Authorization=$authKey")//?Authorization=$authKey
    fun scheduleModify(
//        @Header("Authorization")
//        authorization: String = authKey,
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