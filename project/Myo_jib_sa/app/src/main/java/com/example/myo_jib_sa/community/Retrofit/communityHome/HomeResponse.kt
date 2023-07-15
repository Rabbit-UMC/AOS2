package com.example.myo_jib_sa.community.Retrofit.communityHome

import java.sql.Timestamp

data class HomeResponse(
    val isSuccess:String,
    val code:Int,
    val message:String,
    val mainMission:List<MainMission>,
    val popularArticle:List<PopularTopic>

)
data class MainMission(
    val mainMissionName:String,
    val dDay:String,
    val catagoryImage:String,
    val catagoryName:String,
    val mainMissionId:Long
)
data class PopularTopic(
    val topicTitle:String,
    val likeCount:Int,
    val commentCount:Int,
    val articleId:Long
)
