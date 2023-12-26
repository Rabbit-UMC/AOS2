package com.example.myo_jib_sa.mypage.API

import com.example.myo_jib_sa.base.BaseResponse

data class GetMyCommentResponse(
    val result: List<GetMyCommentResult>?
): BaseResponse()

data class GetMyCommentResult(
    val articleId:Long,
    val articleTitle:String,
    val uploadTime:String,
    val likeCount:Int,
    val commentCount:Int
)

data class GetMyPostResponse(
    val result: List<GetMyPostResult>?
): BaseResponse()

data class GetMyPostResult(
    val articleId:Long,
    val articleTitle:String,
    val uploadTime:String,
    val likeCount:Int,
    val commentCount:Int
)

data class GetUserProfileResponse(
    val result: GetUserProfileResult?
): BaseResponse()

data class GetUserProfileResult(
    val createAt:String,
    val userName:String,
    val userProfileImage:String
)

data class PutUserImageResponse(
    val result:Long
): BaseResponse()

data class GetCheckDuplicationResponse(
    val result: Boolean
): BaseResponse()
