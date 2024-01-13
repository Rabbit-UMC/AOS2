package com.example.myo_jib_sa.community.api.BoardPost

import com.example.myo_jib_sa.base.BaseResponse

data class PostBoardResponse(
    val result: BoardResult
): BaseResponse()
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

data class PopularPostResponse(
    val isSuccess:String,
    val code:Int,
    val message:String,
    val result: List<PopularPostResult>
)

data class PopularPostResult(
    val categoryName:String
    ,val articleId: Long
    ,val articleTitle: String
    ,val uploadTime: String
    ,val likeCount: Int
    ,val commentCount: Int
)
