package com.example.myo_jib_sa.mypage.api

import com.example.myo_jib_sa.base.BaseResponse

data class GetWritingResponse(
    val result: List<Post>?
): BaseResponse()

data class Post(
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

data class PatchProfileResponse(
    val result:Long
): BaseResponse()

data class GetCheckDuplicationResponse(
    val result: Boolean
): BaseResponse()

data class GetHistoryResponse(
    val code: String,
    val isSuccess: Boolean,
    val message: String,
    val result: GetHistoryResult
)
data class GetHistoryResult(
    val missionCnt: Int,
    val targetCnt: Int,
    val userMissionResDtos: List<UserMissionResDto>
)
data class UserMissionResDto(
    val categoryId: Int,
    val challengerCnt: Int,
    val endAt: String,
    val image: String,
    val missionId: Int,
    val startAt: String,
    val successCnt: Int,
    val title: String
)

data class LogoutResponse(
    val result: Long
):  BaseResponse()

data class UnregisterResponse(
    val result: Long
):  BaseResponse()