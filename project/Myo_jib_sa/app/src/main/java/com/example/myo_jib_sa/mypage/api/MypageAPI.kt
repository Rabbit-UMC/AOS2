package com.example.myo_jib_sa.mypage.api


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

    //닉네임 중복 확인
    @GET("app/users/checkDuplication")
    fun getCheckDuplication(
        @Query("userName") userName: String,
        @Query("hasAccount") hasAccount: Boolean
    ): Call<GetCheckDuplicationResponse>

    //이미지 업로드
    @PATCH("app/users/profile")
    fun patchProfile(
        @Query("userProfileImage") userProfileImage: String,
        @Query("userName") userName: String
    ): Call<PatchProfileResponse>

}