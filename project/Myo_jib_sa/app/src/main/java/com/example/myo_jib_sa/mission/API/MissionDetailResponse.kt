package com.example.myo_jib_sa.mission.API

data class MissionDetailResponse(
    val isSucess:String,
    val code:Int,
    val messeage:String,
    val result: Detail
)

data class Detail(
    val id:Long,
    val title:String,
    val categoryTitle:String,
    val content:String,
    val startAt:String,
    val endAt : String
)
