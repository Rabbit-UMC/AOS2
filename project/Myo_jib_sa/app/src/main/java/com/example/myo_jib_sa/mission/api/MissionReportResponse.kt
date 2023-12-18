package com.example.myo_jib_sa.mission.api

data class MissionReportResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: String
)
