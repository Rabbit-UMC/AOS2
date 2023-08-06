package com.example.myo_jib_sa.mission.API

data class MissionHomeResponse(
    val result: List<Home>,
)

data class Home(
    val id: Long,
    val title: String,
    val challengerCnt: Int,
    val startAt: String,
    val endAt: String,
    val content: String,
    val categoryId: Long,
    val image: String,
)
