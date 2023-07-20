package com.example.myo_jib_sa.schedule.api.scheduleDetail

import com.google.gson.annotations.SerializedName

data class ScheduleDetailResponse(
    val id: Long,
    val missionId: Long,
    val missionTitle: String,
    val scheduleTitle: String,
    val content: String,
    val startAt: String,
    val endAt: String,
    @SerializedName("when")
    val scheduleWhen: String
)
