package com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMissionDelete

import com.google.gson.annotations.SerializedName


data class CurrentMissionDeleteResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: String
)

