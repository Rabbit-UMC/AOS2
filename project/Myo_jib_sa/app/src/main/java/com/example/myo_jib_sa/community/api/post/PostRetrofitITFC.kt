package com.example.myo_jib_sa.community.api.post

import com.example.myo_jib_sa.community.Constance
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface PostRetrofitITFC {

    //게시물 보기
    @GET("app/article/{articleId}")
    fun postView(@Path("articleId") articleId: Long):
            Call<PostViewResponse>

    //게시물 삭제
    @DELETE("app/article/{articleId}")
    fun postDelete(@Path("articleId") articleId: Long):
            Call<SimpleResponse>

    //게시물 생성
    @Multipart
    @POST("app/article")
    fun postArticle(
        @Body postArticleReq: PostCreateRequest,
        @Part files: List<MultipartBody.Part>,
        @Query("categoryId") categoryId: Long
    ):Call <SimpleResponse>

    //게시물 수정
    @PATCH("app/article/{articleID}")
    fun postEdit(
        @Body request:PostEditRequest
        , @Path("articleID") articleID: Long
        , @Query("articleId")  queryArticleId:Long):
            Call<SimpleResponse>

    //게시물 신고
    @POST("app/article/{articleId}/report")
    fun postReport(@Path("articleId") articleId: Long):
            Call<SimpleResponse>

    //좋아요 누르기
    @POST("app/article/{articleId}/like")
    fun postLike(@Path("articleId") articleId: Long):
            Call<SimpleResponse>

    //좋아요 취소
    @DELETE("app/article/{articleId}/unlike")
    fun postUnlike(@Path("articleId") articleId: Long):
            Call<SimpleResponse>

    //게시글 댓글 작성
    @POST("app/comments/{articleId}")
    fun postComment(@Body content:String
                ,@Path("articleId") articleId: Long):
            Call<SimpleResponse>

    //게시글 댓글 삭제
    @DELETE("app/comments/{commentsId}")
    fun postCommentDelete(
        @Path("commentsId") commentsId: Long
    ): Call<SimpleResponse>

    //게시글 댓글 잠그기
    @PATCH("/app/comments/{commentsId}/lock")
    fun commentLock(
        @Path("commentsId") commentsId: Long
    ):Call<SimpleResponse>


}