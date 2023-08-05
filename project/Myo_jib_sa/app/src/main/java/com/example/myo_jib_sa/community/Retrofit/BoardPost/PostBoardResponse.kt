package com.example.myo_jib_sa.community.Retrofit.BoardPost

import java.sql.Timestamp

data class PostBoardResponse(
    val isSuccess:String,
    val code:Int,
    val message:String,
    val result: BoardResult
)
data class BoardResult(
    val categoryHostId:Long,
    val articleLists:List<Articles>
)
data class Articles(
    val articleId:Long,
    val articleTitle:String,
    val uploadTime:String,
    val likeCount:Int,
    val commentCount:Int
)
