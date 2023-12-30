package com.example.myo_jib_sa.Schedule.currentMissionActivity.api.currentMission

import com.google.gson.annotations.SerializedName


data class HistoryMissionResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: HistoryMissionResult
)

data class HistoryMissionResult(
    val missionCnt:Long,
    val missionHomeResList:List<HistoryMissionList>,
    val point:Long,
    val targetCnt:Long
)

data class HistoryMissionList(
    val categoryId : Long,
    val challengerCnt : Long,
    val successCnt:Long,
    val content:String,
    val endAt:String,
    val startAt:String,
    val image:String,
    val missionId:Long,
    @SerializedName("title")
    val missionTitle:String
)