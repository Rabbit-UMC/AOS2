package com.example.myo_jib_sa.mission.API

data class MissionWriteRequest(

    val title:String,
    val startAt:String,
    val endAt:String,
    val categoryId:Long,
    val isOpen:Int,
    val content:String
)
