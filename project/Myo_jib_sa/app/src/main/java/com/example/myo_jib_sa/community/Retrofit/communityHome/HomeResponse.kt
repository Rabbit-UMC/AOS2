package com.example.myo_jib_sa.community.Retrofit.communityHome

import java.sql.Timestamp

data class HomeResponse(
    val isSuccess:String,
    val code:Int,
    val message:String,
    val result: HomeResult
)
data class HomeResult(
    val mainMission:List<MainMission>,
    val popularArticle:List<PopularArticle>
)
data class MainMission(
    val mainMissionTitle:String,
    val dday:String,
    val categoryImage:String,
    val categoryName:String,
    val mainMissionId:Long
)
data class PopularArticle(
    val articleTitle:String,
    val likeCount:Int,
    val commentCount:Int,
    val articleId:Long,
    val uploadTime:String
)
