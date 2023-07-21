package com.example.myo_jib_sa.schedule.api.scheduleDetail

import com.google.gson.annotations.SerializedName


data class ScheduleDetailResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: Result
)

data class Result(
    @SerializedName("id")
    val scheduleId: Long,
    val missionId: Long,
    val missionTitle: String,
    val scheduleTitle: String,
    val startAt: String,
    val endAt: String,
    val content: String,
    @SerializedName("when")
    val scheduleWhen: String
)

