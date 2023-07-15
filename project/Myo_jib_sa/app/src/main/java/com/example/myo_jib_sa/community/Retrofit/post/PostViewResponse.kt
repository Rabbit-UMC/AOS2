package com.example.myo_jib_sa.community.Retrofit.post

import java.sql.Timestamp

//30번 api (게시물 조회 data class)
data class PostViewResponse(
    val isSuccess:String,
    val code:Int,
    val message:String,
    val articleId:Long,
    val authorId:Long,
    val authorProfileImage:String,
    val authorName:String,
    val uploadTime:String,
    val articleTitle:String,
    val articleContent:String,
    val articleImage:List<ArticleImage>,
    val commentList:List<CommentList>
)
data class ArticleImage(
    val imageId:Long,
    val filePsth:String
)
data class CommentList(
    val commentId:Long,
    val commentAuthorProfileImage:String,
    val commentAuthorName:String,
    val commentContent:String
)


//31번 api 게시물 삭제 data class
data class PostDeleteResponse(
    val isSuccess:String,
    val code:Int,
    val message: String,
    val result: String
)


//32번 api 게시글 생성 data class
data class PostCreateRequest(
    val articleTitle: String,
    val articleContent:String,
    val imageList:List<String>
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
    val imageList: List<ImageList>
)
data class ImageList(
    val id:Long,
    val filePath:String
)


//34번 api 게시물 신고
