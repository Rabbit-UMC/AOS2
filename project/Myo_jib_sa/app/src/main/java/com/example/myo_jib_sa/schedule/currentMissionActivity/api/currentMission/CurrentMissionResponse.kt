package com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMission

import com.google.gson.annotations.SerializedName


data class CurrentMissionResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: List<CurrentMissionResult>
)

data class CurrentMissionResult(
    @SerializedName("id")
    var missionId: Long,
    @SerializedName("title")
    var missionTitle: String,
    var challengerCnt: Int,
    var categoryId: Long,
    var image: String,
    var dday: String
)