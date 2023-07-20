package com.example.myo_jib_sa.schedule.api.scheduleOfDay

data class ScheduleOfDayResponse(
    val scheduleId: Long,
    val scheduleTitle: String,
    val scheduleStart: String,
    val scheduleEnd: String,
    val scheduleWhen: String
)
