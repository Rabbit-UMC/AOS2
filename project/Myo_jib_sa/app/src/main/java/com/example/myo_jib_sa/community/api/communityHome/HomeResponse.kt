package com.example.myo_jib_sa.community.api.communityHome

import com.example.myo_jib_sa.base.BaseResponse

data class HomeResponse (
    val result: HomeResult
): BaseResponse()
data class HomeResult(
    val mainMission:List<MainMission>,
    val popularArticle:List<PopularArticle>,
    val userHostCategory:List<Long>
)
data class MainMission(
    val mainMissionId:Long,
    val mainMissionTitle:String,
    val topRankUser:String,
    val missionCategoryId:Long,
    val dday:String
)
data class PopularArticle(
    val articleId:Long,
    val articleTitle:String,
    val uploadTime:String,
    val likeCount:Int,
    val commentCount:Int,
)
