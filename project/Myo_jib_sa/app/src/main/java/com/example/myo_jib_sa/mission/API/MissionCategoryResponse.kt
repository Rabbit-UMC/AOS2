package com.example.myo_jib_sa.mission.API

data class MissionCategoryResponse(
    val result: List<Category>
)
data class Category(
    val image: String,
    val missionId: Long,
    val title: String,
    val challengerCnt: Int,
    val endAt: String,
    val startAt: String,
    val content: String,
    val categoryId: Long
)
