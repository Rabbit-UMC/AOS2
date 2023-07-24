package com.example.myo_jib_sa.community.Retrofit.post

import retrofit2.Call
import retrofit2.http.*

interface PostRetrofitITFC {

    //게시물 보기
    @GET("app/article/{articleId}")
    fun postView(@Header("X-ACCESS-TOKEN")author:String
                 ,@Path("articleId") articleId: Long):
            Call<PostViewResponse>

    //게시물 삭제
    @DELETE("app/article/{articleId}")
    fun postDelete(@Header("X-ACCESS-TOKEN")author:String
                   ,@Path("articleId") articleId: Long):
            Call<SimpleResponse>

    //게시물 생성
    @POST("app/article")
    fun postCreate(
        @Header("X-ACCESS-TOKEN")author:String,
        @Body request:PostCreateRequest,
        @Query("categoryId") categoryId:Long):
            Call <PostisSuccessResponse>

    //게시물 수정
    @PATCH("app/article/{articleID}")
    fun postEdit(
        @Header("X-ACCESS-TOKEN")author:String,
        @Body request:PostEditRequest,
        @Query("articleId") articleId:Long
        ,@Path("articleId") articleID: Long):
            Call<PostisSuccessResponse>

    //게시물 신고
    @POST("app/article/{articleId}/report")
    fun postReport(@Header("X-ACCESS-TOKEN")author:String
                   ,@Path("articleId") articleId: Long):
            Call<PostisSuccessResponse>

    //좋아요 누르기
    @POST("app/article/{articleId}/like")
    fun postLike(@Header("X-ACCESS-TOKEN")author:String
                 ,@Path("articleId") articleId: Long):
            Call<PostisSuccessResponse>

    //좋아요 취소
    @DELETE("app/article/{articleId}/unlike")
    fun postUnlike(@Header("X-ACCESS-TOKEN")author:String
                   ,@Path("articleId") articleId: Long):
            Call<PostisSuccessResponse>

    //게시글 댓글 작성
    @POST("app/comments/{articleId}")
    fun postComment(@Header("X-ACCESS-TOKEN")author:String
                ,@Body content:String
                ,@Path("articleId") articleId: Long):
            Call<PostisSuccessResponse>

    //게시글 댓글 삭제
    @DELETE("app/comments/{articleId}/{commentsId}")
    fun postCommentDelete(@Header("X-ACCESS-TOKEN")author:String
                      ,@Path("articleId") articleId: Long
                      ,@Path("commentsId") commentsId:Long):
            Call<SimpleResponse>


}