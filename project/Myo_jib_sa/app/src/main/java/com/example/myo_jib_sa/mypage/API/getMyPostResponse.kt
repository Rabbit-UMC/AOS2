package com.example.myo_jib_sa.mypage.API

data class getMyPostResponse(
    val isSucess:String,
    val code:Int,
    val messeage:String,
    val result: Post?
)

data class Post(
    val articleId:Long,
    val articleTitle:String,
    val uploadTime:String,
    val likeCount:Int,
    val commentCount:Int
)