package com.example.myo_jib_sa.Schedule.api.scheduleOfDay

import com.example.myo_jib_sa.base.BaseResponse


data class ScheduleOfDayResponse(
    val result: List<ScheduleOfDayResult>
):BaseResponse()
data class ScheduleOfDayResult(
    val scheduleId: Long,
    val scheduleTitle: String,
    val scheduleStart: String,
    val scheduleEnd: String,
    val scheduleWhen: String
)
