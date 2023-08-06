package com.example.myo_jib_sa.mission.API

data class MissionDetailResponse(
    val result: List<Detail>
)

data class Detail(
    val id: Long,
    val title: String,
    val challengerCnt: Int,
    val endAt: String,
    val startAt: String,
    val content: String,
    val categoryId: Long
)
