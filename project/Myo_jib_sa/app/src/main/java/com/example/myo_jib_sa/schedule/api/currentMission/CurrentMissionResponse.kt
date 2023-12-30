package com.example.myo_jib_sa.Schedule.API.currentMission

import com.example.myo_jib_sa.base.BaseResponse
import com.google.gson.annotations.SerializedName


data class CurrentMissionResponse(
    val result: List<CurrentMissionResult>
):BaseResponse()

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