package com.example.myo_jib_sa.mypage.API

data class getMyCommentResponse(

val isSucess:String,
val code:Int,
val messeage:String,
val result: Comment?
)

data class Comment(
    val articleId:Long,
    val articleTitle:String,
    val uploadTime:String,
    val likeCount:Int,
    val commentCount:Int
)
