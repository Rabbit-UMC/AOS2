package com.example.myo_jib_sa.schedule.createScheduleActivity.api.getMissionList

import com.google.gson.annotations.SerializedName

data class GetMyMissionResponse (
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result : ArrayList<GetMyMissionResult>
)

data class GetMyMissionResult(
    @SerializedName("id")
    val missionId : Long,
    val title : String,
    val challengerCnt : Int,
    val categoryId : Long,
    val image : String,
    val dday : String
)
