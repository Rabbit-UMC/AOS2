package com.example.myo_jib_sa.schedule.api.scheduleMonth

import com.example.myo_jib_sa.base.BaseResponse

data class ScheduleMonthResponse(
    val result: ScheduleMonthResult
):BaseResponse()

data class ScheduleMonthResult(
    val schedulesOfDay: Map<String, Int>
)