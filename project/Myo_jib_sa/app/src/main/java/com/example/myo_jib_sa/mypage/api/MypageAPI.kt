package com.example.myo_jib_sa.mypage.API


import retrofit2.Call
import retrofit2.http.*

interface MypageAPI {

    //유저 작성 글 조회
    @GET("app/users/articleList")
    fun getMyPost(
        @Query("page") page: Int,
    ): Call<GetMyPostResponse>

    //유저 작성 댓글 조회
    @GET("app/users/commented-articles")
    fun getMyComment(
        @Query("page") page: Int,
    ): Call<GetMyCommentResponse>

    //유저 정보 가져오기
    @GET("app/users/profile")
    fun getUserProfile(): Call<GetUserProfileResponse>

    //이미지 업로드
    @PATCH("app/users/profileImage")
    fun putUserImage(
        @Query("userProfileImage") userProfileImage: String
    ): Call<PutUserImageResponse>

    //닉네임 업로드
    @GET("app/users/checkDuplication")
    fun getCheckDuplication(
        @Query("userName") userName: String
    ): Call<GetCheckDuplicationResponse>


}