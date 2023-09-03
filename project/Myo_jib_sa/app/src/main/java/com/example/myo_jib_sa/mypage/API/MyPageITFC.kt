package com.example.myo_jib_sa.mypage.API


import retrofit2.Call
import retrofit2.http.*

interface MyPageITFC {

    //유저 작성 글 조회
    @GET("app/users/articleList")
    fun getMyPost(
        @Header("X-ACCESS-TOKEN") accessToken: String,
        @Query("page") page: Int,
        ): Call<getMyPostResponse>

    //유저 작성 댓글 조회
    @GET("app/users/commented-articles")
    fun getMyComment(
        @Header("X-ACCESS-TOKEN") accessToken: String,
        @Query("page") page: Int,
    ): Call<getMyCommentResponse>

    //이미지 업로드
    @PATCH("app/users/profileImage")
    fun putUserImage(
        @Header("X-ACCESS-TOKEN") accessToken: String,
        @Query("userProfileImage") userProfileImage: String
    ): Call<putUserImageResponse>

    //닉네임 업로드
    @PATCH("app/users/nickname")
    fun putUserName(
        @Header("X-ACCESS-TOKEN") accessToken: String,
        @Query("userName") userName: String
    ): Call<putUserNameResponse>

    //유저 정보 가져오기
    @GET("app/users/profile")
    fun getUserProfile(
        @Header("X-ACCESS-TOKEN") accessToken: String,
    ): Call<getUserProfileResponse>
}