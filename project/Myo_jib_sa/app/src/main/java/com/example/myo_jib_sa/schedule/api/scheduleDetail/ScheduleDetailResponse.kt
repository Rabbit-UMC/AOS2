package com.example.myo_jib_sa.schedule.api.scheduleDetail

import com.google.gson.annotations.SerializedName


data class ScheduleDetailResponse(
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: Result
)

data class Result(
    val id: Int,
    val missionId: Int,
    val missionTitle: String,
    val scheduleTitle: String,
    val startAt: String,
    val endAt: String,
    val content: String,
    @SerializedName("when")
    val scheduleWhen: String
)

