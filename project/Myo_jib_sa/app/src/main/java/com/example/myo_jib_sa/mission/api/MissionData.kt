package com.example.myo_jib_sa.mission.api

import com.example.myo_jib_sa.base.BaseResponse
import java.time.LocalDate
import java.util.Objects

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
    val successCnt: Int,
    val dday: String
)

data class MissionCategoryListResponse(
    val result: List<MissionCategoryListResult>
): BaseResponse()

data class MissionCategoryListResult(
    val id: Long,
    val title: String
)

data class MissionCreateRequests(
    var title:String,
    var startAt:String,
    var endAt:String,
    var categoryId:Long,
    var isOpen:Int,
    var content:String,
    var status: String
)

data class MissionCreateResponse(
    val result: String
): BaseResponse()

data class MissionDetailResponse(
    val result: Detail
): BaseResponse()

data class Detail(
    val id:Long,
    val title:String,
    val categoryTitle:String,
    val content:String,
    val startAt:String,
    val endAt : String,
    val alreadyIn: Boolean
)

data class MissionWithResponse(
    val result: String
): BaseResponse()

data class MissionReportResponse(
    val result: String
): BaseResponse()

data class MissionCreateDate(
    val date: LocalDate?,
    var isSelected :Boolean = false//ture이면 일정 있음, false이면 일정 없음
)