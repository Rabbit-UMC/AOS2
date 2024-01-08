package com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMissionSchedule

import com.example.myo_jib_sa.base.BaseResponse
import com.google.gson.annotations.SerializedName


data class CurrentMissionScheduleResponse(
    val result: List<CurrentMissionScheduleResult>
):BaseResponse()

data class CurrentMissionScheduleResult(
    @SerializedName("id")
    var scheduleId: Long,
    @SerializedName("title")
    var scheduleTitle: String,
    @SerializedName("when")
    var scheduleWhen: String
)