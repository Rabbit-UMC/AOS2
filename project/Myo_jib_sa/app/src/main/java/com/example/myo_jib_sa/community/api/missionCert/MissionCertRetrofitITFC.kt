package com.example.myo_jib_sa.community.api.missionCert

import com.example.myo_jib_sa.base.BaseResponse
import com.example.myo_jib_sa.community.Constance
import com.example.myo_jib_sa.community.api.post.SimpleResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface MissionCertRetrofitITFC {

    //미션 인증 화면 조회
    @GET("app/main-mission/{mainMissionId}")
    fun mission(@Path("mainMissionId") mainMissionId: Long
                ,@Query("day") day:Int): Call<MissionResponse>

    //미션 인증 사진 좋아요
    @POST("app/main-mission/proof/{mainMissionProofId}/like")
    fun missionLike(
                    @Path("mainMissionProofId") mainMissionId: Long):Call<BaseResponse>

    //미션 인증 사진 좋아요 취소
    @DELETE("app/main-mission/proof/{mainMissionProofId}/unlike")
    fun missionUnlike(
                      @Path("mainMissionProofId") mainMissionId: Long):Call<BaseResponse>

    //미션 인증 사진 신고
    @POST("app/main-mission/proof/{mainMissionProofId}/report")
    fun missionReport(
                      @Path("mainMissionProofId") mainMissionId: Long):Call<BaseResponse>

    //미션 인증 사진 올리기
    @Multipart
    @POST("app/main-mission/upload/{categoryId}")
    fun postImg(
                @Path("categoryId") categoryId:Long,
                @Part multipartFile: MultipartBody.Part):Call<BaseResponse>

}