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
    val result: List<PopularPostResult>
): BaseResponse()

data class PopularPostResult(
    val articleId: Long
    ,val articleTitle: String
    ,val uploadTime: String
    ,val likeCount: Int
    ,val commentCount: Int
)
