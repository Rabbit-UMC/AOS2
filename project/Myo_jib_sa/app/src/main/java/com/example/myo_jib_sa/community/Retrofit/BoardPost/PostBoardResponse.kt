package com.example.myo_jib_sa.community.Retrofit.BoardPost

import com.example.myo_jib_sa.community.Retrofit.communityHome.MainMission
import java.sql.Timestamp

data class PostBoardResponse(
    val isSuccess:String,
    val code:Int,
    val message:String,
    val result: BoardResult
)
data class BoardResult(
    val categoryHostId:Long,
    val categoryImage:String,
    val mainMissionId: Long,
    val articleLists:List<Articles>
)
data class Articles(
    val articleId:Long,
    val articleTitle:String,
    val uploadTime:String,
    val likeCount:Int,
    val commentCount:Int
)
data class ArticlesPopular(
    val categoryName:String
    ,val articleId: Long
    ,val articleTitle: String
    ,val uploadTime: String
    ,val likeCount: Int
    ,val commentCount: Int
)
