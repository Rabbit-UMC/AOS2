package com.example.myo_jib_sa.Schedule.currentMissionActivity.api.currentMissionDetail

import com.google.gson.annotations.SerializedName


data class CurrentMissionDetailResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: CurrentMissionDetailResult
)

data class CurrentMissionDetailResult(
    @SerializedName("title")
    var missionTitle: String,
    var startAt: String,
    var endAt: String,
    var content: String,
    var categoryTitle: String
)