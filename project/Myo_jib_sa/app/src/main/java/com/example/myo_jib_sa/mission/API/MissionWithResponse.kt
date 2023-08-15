package com.example.myo_jib_sa.mission.API

data class MissionWithResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: String
)
