package com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMissionDetail

import com.example.myo_jib_sa.base.BaseResponse
import com.google.gson.annotations.SerializedName


data class CurrentMissionDetailResponse(
    val result: CurrentMissionDetailResult
):BaseResponse()

data class CurrentMissionDetailResult(
    @SerializedName("id")
    val missionId:Long,
    @SerializedName("title")
    var missionTitle: String,
    var image:String,
    var startAt: String,
    var endAt: String,
    var content: String,
    var categoryTitle: String,
    val alreadyIn : Boolean
)