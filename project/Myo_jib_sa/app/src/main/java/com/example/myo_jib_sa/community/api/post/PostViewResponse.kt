package com.example.myo_jib_sa.community.api.post

import com.example.myo_jib_sa.base.BaseResponse
import okhttp3.MultipartBody
import java.io.Serializable

//30번 api (게시물 조회 data class)
data class PostViewResponse(
    val result:PostResult
):BaseResponse()
data class PostResult(
    val categoryName:String,
    val articleId:Long,
    val authorId:Long,
    val authorProfileImage:String,
    val authorName:String,
    val uploadTime:String,
    val articleTitle:String,
    val articleContent:String,
    val articleImage:List<ArticleImage>,
    val commentList:List<CommentList>,
    val likeArticle:Boolean,
    val likeCount:Int
)
data class ArticleImage(
    val imageId:Long,
    val filePath:String
): Serializable
data class CommentList(
    val commentUserId:Long,
    val commentId:Long,
    val commentAuthorProfileImage:String,
    val commentAuthorName:String,
    val commentContent:String,
    val userPermission: String //묘집사 HOST, 유저 USER
)


data class SimpleResponse(
    val result: String
):BaseResponse()


//32번 api 게시글 생성 data class
data class PostCreateRequest(
    val articleTitle: String,
    val articleContent:String
)
data class ImageListC(
    val filePath:String
)
data class PostisSuccessResponse(
    val isSuccess: String,
    val code: Int,
    val message: String
)

//33번 api 게시글 수정 data class
data class PostEditRequest(
    val articleTitle: String,
    val articleContent: String,
    val newImageIdList: List<Long>,
    val deleteImageIdList: List<Long>
)
data class ImageList(
    val id:Long,
    var filePath:String
)



//34번 api 게시물 신고

