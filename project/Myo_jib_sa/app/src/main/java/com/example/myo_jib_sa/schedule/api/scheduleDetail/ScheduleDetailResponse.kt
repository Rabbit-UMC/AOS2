package com.example.myo_jib_sa.schedule.api.scheduleDetail

import com.google.gson.annotations.SerializedName


data class ScheduleDetailResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: ScheduleDetailResult
)

data class ScheduleDetailResult(
    @SerializedName("id")
    var scheduleId: Long,
    var missionId: Long,
    var missionTitle: String,
    var scheduleTitle: String,
    var startAt: String,
    var endAt: String,
    var content: String,
    @SerializedName("when")
    var scheduleWhen: String
)

