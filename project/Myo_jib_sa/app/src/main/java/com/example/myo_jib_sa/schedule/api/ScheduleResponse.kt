package com.example.myo_jib_sa.schedule.api

import com.example.myo_jib_sa.base.BaseResponse
import com.google.gson.annotations.SerializedName

//일정 홈 화면
data class ScheduleHomeResponse(
    val result: ScheduleHomeResult
): BaseResponse()
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


//스케줄 상세 조회
data class ScheduleDetailResponse(
    val result: ScheduleDetailResult
): BaseResponse()
data class ScheduleDetailResult(
    @SerializedName("id")
    var scheduleId: Long,
    var missionId: Long?,
    var missionTitle: String,
    var scheduleTitle: String,
    var startAt: String,
    var endAt: String,
    var content: String,
    @SerializedName("when")
    var scheduleWhen: String
)

//날짜별 일정 리스트 조회
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

//월별 일정 조회
data class ScheduleMonthResponse(
    val result: ScheduleMonthResult
):BaseResponse()
data class ScheduleMonthResult(
    val schedulesOfDay: Map<String, Int>
)

//일정 추가
data class CreateScheduleResponse (
    val result: Long
):BaseResponse()

//일정 수정
data class UpdateScheduleResponse(
    val result: String
):BaseResponse()

//일정 삭제
data class DeleteScheduleResponse(
    val result: String
):BaseResponse()