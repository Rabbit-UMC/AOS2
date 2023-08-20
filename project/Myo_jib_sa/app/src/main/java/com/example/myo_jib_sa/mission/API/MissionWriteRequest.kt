package com.example.myo_jib_sa.mission.API

data class MissionWriteRequest(

    var title:String,
    var startAt:String,
    var endAt:String,
    var categoryId:Long,
    var isOpen:Int,
    var content:String
)
