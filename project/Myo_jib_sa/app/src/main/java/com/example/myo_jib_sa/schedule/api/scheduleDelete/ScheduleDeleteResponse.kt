package com.example.myo_jib_sa.schedule.api.scheduleDelete

data class ScheduleDeleteResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: String
)
