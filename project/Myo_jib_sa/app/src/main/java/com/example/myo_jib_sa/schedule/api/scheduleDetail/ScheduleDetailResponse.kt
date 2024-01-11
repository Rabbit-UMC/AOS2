package com.example.myo_jib_sa.schedule.api.scheduleDetail
import com.example.myo_jib_sa.base.BaseResponse
import com.google.gson.annotations.SerializedName


data class ScheduleDetailResponse(
    val result: ScheduleDetailResult
):BaseResponse()

data class ScheduleDetailResult(
    @SerializedName("id")
    var scheduleId: Long,
    var missionId: Long?,
    var missionTitle: String,
    var scheduleTitle: String,
    var startAt: String,
    var endAt: String,
    var content: String,
    @SerializedName("when")
    var scheduleWhen: String
)

