package com.example.myo_jib_sa.schedule.api.scheduleOfDay


data class ScheduleOfDayResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: List<ScheduleOfDayResult>
)
data class ScheduleOfDayResult(
    val scheduleId: Long,
    val scheduleTitle: String,
    val scheduleStart: String,
    val scheduleEnd: String,
    val scheduleWhen: String
)
