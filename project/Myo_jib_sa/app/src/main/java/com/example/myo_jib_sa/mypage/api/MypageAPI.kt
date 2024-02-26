package com.example.myo_jib_sa.mypage.api


import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface MypageAPI {

    //유저 작성 글 조회
    @GET("app/users/articleList")
    fun getMyPost(
        @Query("page") page: Int,
    ): Call<GetWritingResponse>

    //유저 작성 댓글 조회
    @GET("app/users/commented-articles")
    fun getMyComment(
        @Query("page") page: Int,
    ): Call<GetWritingResponse>

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
    @Multipart
    @PATCH("app/users/profile")
    fun patchProfile(
        @Part userProfileImage: MultipartBody.Part?,
        @Query("userName") userName: String?
    ): Call<PatchProfileResponse>


    // 성공 히스토리 조회
    @GET("app/users/success")
    fun getSuccessHistory(): Call<GetHistoryResponse>

    // 실패 히스토리 조회
    @GET("app/users/failure")
    fun getFailureHistory(): Call<GetHistoryResponse>

    //로그아웃
    @GET("app/users/kakao-logout")
    fun getLogout(
        @Header("Authorization") Authorization: String,
    ): Call<LogoutResponse>
    //회원탈퇴
    @GET("app/users/kakao-unlink")
    fun getUnregister(
        @Header("Authorization") Authorization: String,
    ): Call<UnregisterResponse>

}