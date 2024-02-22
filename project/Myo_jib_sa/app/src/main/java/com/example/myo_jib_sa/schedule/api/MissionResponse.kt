package com.example.myo_jib_sa.schedule.api

import com.example.myo_jib_sa.base.BaseResponse
import com.google.gson.annotations.SerializedName

//내 미션 조회
data class MyMissionResponse(
    val result: MutableList<MyMissionResult>
): BaseResponse()
data class MyMissionResult(
    @SerializedName("id")
    var missionId: Long,
    @SerializedName("title")
    var missionTitle: String,
    var challengerCnt: Int,
    var categoryId: Long,
    var during:Int,
    var image: String,
    var dday: String
)

//내 미션 상세 조회
data class MyMissionDetailResponse(
    val result: MyMissionDetailResult
):BaseResponse()
data class MyMissionDetailResult(
    @SerializedName("id")
    val missionId:Long,
    @SerializedName("title")
    var missionTitle: String,
    var image:String,
    var startAt: String,
    var endAt: String,
    var content: String,
    var categoryTitle: String,
    var categoryId:Long,
    val alreadyIn : Boolean
)

//내 미션 하위 일정
data class MyMissionScheduleResponse(
    val result: List<MyMissionScheduleResult>
):BaseResponse()
data class MyMissionScheduleResult(
    @SerializedName("id")
    var scheduleId: Long,
    @SerializedName("title")
    var scheduleTitle: String,
    @SerializedName("when")
    var scheduleWhen: String,
    var startAt:String,
    var endAt: String
)

//내미션 지우기
data class DeleteMyMissionResponse(
    val result: String
):BaseResponse()

data class DeleteMyMissionNScheduleResponse(
    val result: String
):BaseResponse()