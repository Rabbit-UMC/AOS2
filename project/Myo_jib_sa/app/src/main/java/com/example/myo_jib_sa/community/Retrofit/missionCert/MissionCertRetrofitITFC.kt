package com.example.myo_jib_sa.community.Retrofit.missionCert

import com.example.myo_jib_sa.community.Retrofit.post.SimpleResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MissionCertRetrofitITFC {

    //미션 인증 화면 조회
    @GET("app/main-mission/{mainMissionId}")
    fun mission(@Header("X-ACCESS-TOKEN")author:String
                  ,@Path("mainMissionId") mainMissionId: Long
                ,@Query("day") day:Int): Call<MissionResponse>

    //미션 인증 사진 좋아요
    @POST("app/main-mission/proof/{mainMissionProofId}/like")
    fun missionLike(@Header("X-ACCESS-TOKEN")author:String,
                     @Path("mainMissionId") mainMissionId: Int):Call<SimpleResponse>

    //미션 인증 사진 좋아요 취소
    @POST("app/main-mission/proof/{mainMissionProofId}/unlike")
    fun missionUnlike(@Header("X-ACCESS-TOKEN")author:String,
                    @Path("mainMissionId") mainMissionId: Int):Call<SimpleResponse>

    //미션 인증 사진 신고
    @POST("app/main-mission/proof/{mainMissionProofId}/report")
    fun missionReport(@Header("X-ACCESS-TOKEN")author:String,
                    @Path("mainMissionId") mainMissionId: Int):Call<SimpleResponse>

    //미션 인증 사진 올리기
    @POST("app/main-mission/upload/{categoryId}")
    fun postImg(@Header("X-ACCESS-TOKEN")author:String,
                    @Path("categoryId") categoryId:Long,
                    @Query("filePath") filePath:String):Call<SimpleResponse>

}