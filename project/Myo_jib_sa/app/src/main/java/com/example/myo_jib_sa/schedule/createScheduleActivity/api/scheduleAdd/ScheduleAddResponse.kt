package com.example.myo_jib_sa.schedule.createScheduleActivity.api.scheduleAdd

import com.example.myo_jib_sa.base.BaseResponse

data class ScheduleAddResponse (
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Long
)