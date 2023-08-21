package com.example.myo_jib_sa.schedule.api.scheduleDelete

import com.example.myo_jib_sa.schedule.api.scheduleHome.Mission
import com.google.gson.annotations.SerializedName

data class ScheduleMonthResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: ScheduleMonthResult
)

data class ScheduleMonthResult(
    val dayList: List<Int>
)