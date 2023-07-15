package com.example.myo_jib_sa.community.Retrofit.communityHome

import java.sql.Timestamp

data class HomeResponse(
    val isSuccess:String,
    val code:Int,
    val message:String,
    val mainMission:List<MainMission>,
    val popularTopic:List<PopularTopic>

)
data class MainMission(
    val mainMissionName:String,
    val endTime:Timestamp,
    val missionImage:String,
    val catagoryName:String
)
data class PopularTopic(
    val topicTitle:String,
    val likeCount:Int,
    val commentCount:Int
)
