package com.example.myo_jib_sa.Schedule.currentMissionActivity.api.currentMission

import com.google.gson.annotations.SerializedName


data class ProfileNickNameResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: ProfileNickNameResult
)

data class ProfileNickNameResult(
    val userEmail:String,
    @SerializedName("userName")
    val userName:String,
    val userProfileImage:String
)

