package com.example.myo_jib_sa.schedule.api.scheduleHome

//일정 홈 화면
data class ScheduleHomeResponse(
    val result: String?,
    val missionList: List<Mission>?,
    val scheduleList: List<Schedule>?
)

data class Mission(
    val missionId: Long?,
    val challengerCnts: Int?,
    val dday: String?
)

data class Schedule(
    val scheduleId: Long?,
    val scheduleTitle: String?,
    val scheduleStart: String?,
    val scheduleEnd: String?,
    val scheduleWhen: String?
)