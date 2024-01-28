package com.example.myo_jib_sa.schedule.api

import com.google.gson.annotations.SerializedName

//일정 생성
data class CreateScheduleRequest(
    @SerializedName("title")
    var scheduleTitle: String?,
    var content: String?,
    var startAt: String?,
    var endAt: String?,
    @SerializedName("when")
    var scheduleWhen: String?,
    var missionId: Long?
)

//일정 수정
data class UpdateScheduleRequest(
    val title: String,
    val content: String,
    val startAt: String,
    val endAt: String,
    @SerializedName("when")
    val scheduleWhen: String,
    val missionId: Long?
)