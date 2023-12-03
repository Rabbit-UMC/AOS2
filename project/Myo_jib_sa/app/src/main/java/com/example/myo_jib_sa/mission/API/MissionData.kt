package com.example.myo_jib_sa.mission.API

import com.example.myo_jib_sa.base.BaseResponse

data class MissionListResponse(
    val result: List<Mission>,
): BaseResponse()

data class Mission(
    val missionId: Long,
    val title: String,
    val challengerCnt: Int,
    val startAt: String,
    val endAt: String,
    val content: String,
    val categoryId: Long,
    val image: String,
)

data class MissionCategoryListResponse(
    val result: List<MissionCategoryListResult>
): BaseResponse()

data class MissionCategoryListResult(
    val id: Long,
    val title: String
)

data class MissionByCategoryResponse(
    val result: List<Mission>
)