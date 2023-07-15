package com.example.myo_jib_sa.community.Retrofit.Post

import java.sql.Timestamp

data class PostBoardResponse(
    val isSuccess:String,
    val code:Int,
    val message:String,
    val articles:List<Articles>
)
data class Articles(
    val articleTitle:String,
    val uploadTime:Timestamp,
    val LikeCount:Int,
    val commentCount:Int
)
