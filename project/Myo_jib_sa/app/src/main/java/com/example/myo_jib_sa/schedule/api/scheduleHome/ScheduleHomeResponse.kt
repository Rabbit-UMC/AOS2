package com.example.myo_jib_sa.schedule.api.scheduleHome

//일정 홈 화면
data class ScheduleHomeResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: ScheduleHomeResult
)

data class ScheduleHomeResult(
    val missionList: List<Mission>,
    val scheduleList: List<Schedule>
)

data class Mission(
    var missionId: Long,
    val missionTitle : String,
    val challengerCnts: Int,
    val dday: String
)

data class Schedule(
    val scheduleId: Long,
    val scheduleTitle: String,
    val scheduleStart: String,
    val scheduleEnd: String,
    val scheduleWhen: String
)