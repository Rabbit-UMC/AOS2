package com.example.myo_jib_sa.Schedule.currentMissionActivity.api.currentMissionSchedule

import com.google.gson.annotations.SerializedName


data class CurrentMissionScheduleResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: List<CurrentMissionScheduleResult>
)

data class CurrentMissionScheduleResult(
    @SerializedName("id")
    var scheduleId: Long,
    @SerializedName("title")
    var scheduleTitle: String,
    @SerializedName("when")
    var scheduleWhen: String
)