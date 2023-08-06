package com.example.myo_jib_sa.community.Retrofit.missionCert

import android.content.Context
import android.util.Log
import com.example.myo_jib_sa.community.Retrofit.Constance
import com.example.myo_jib_sa.community.Retrofit.RetrofitClient
import com.example.myo_jib_sa.community.Retrofit.communityHome.MainMission
import com.example.myo_jib_sa.community.Retrofit.manager.ManagerRetrofitITFC
import com.example.myo_jib_sa.community.Retrofit.manager.ManagerRetrofitManager
import com.example.myo_jib_sa.community.Retrofit.post.SimpleResponse
import retrofit2.Call
import retrofit2.Response

class MissionCertRetrofitManager(context: Context){
    //레트로핏 인터페이스 가져오기기
    private val retrofit : MissionCertRetrofitITFC? = RetrofitClient.getClient(Constance.BASEURL)?.create(
        MissionCertRetrofitITFC::class.java)

    companion object {
        private var instance: MissionCertRetrofitManager? = null

        // 싱글톤 인스턴스를 가져오는 메서드
        fun getInstance(context: Context): MissionCertRetrofitManager {
            if (instance == null) {
                instance = MissionCertRetrofitManager(context)
            }
            return instance as MissionCertRetrofitManager
        }

    }


    //미션 화면 조회
    fun mission(author: String,day:Int,mainMissionId:Long, completion: (missionResponse: MissionResponse) -> Unit){
        val call: Call<MissionResponse> = retrofit?.mission(author, mainMissionId, day) ?: return

        call.enqueue(object : retrofit2.Callback<MissionResponse> {
            override fun onResponse(
                call: Call<MissionResponse>,
                response: Response<MissionResponse>
            ) {
                Log.d("미션 화면 조회", "RetrofitManager 미션 화면 조회 onResponse \t :${response.message()} ")
                val response: MissionResponse? = response?.body()
                if (response != null) {
                    if (response.isSuccess=="true") {
                        Log.d("미션 화면 조회",
                            "RetrofitManager 미션 화면 조회 is Success\t :${response.code} ")
                        Log.d("미션 화면 조회",
                            "RetrofitManager 미션 화면 조회 is Success\t :${response.message} ")
                        completion(response)
                    } else {
                        Log.d("미션 화면 조회",
                            "RetrofitManager 미션 화면 조회 is NOT Success\t :${response.code} ")
                        completion(response)
                    }
                } else {
                    Log.d("미션 화면 조회", "RetrofitManager 미션 화면 조회 null")
                }
            }
            override fun onFailure(call: Call<MissionResponse>, t: Throwable) {
                Log.d("미션 화면 조회", "RetrofitManager 미션 화면 조회 onFailure \t :$t ")
            }
        })
    }

    //미션 인증 사진 좋아요
    fun missionImgLike(author: String,imgId:Int, completion: (isSuccess:Boolean) -> Unit){
        val call: Call<SimpleResponse> = retrofit?.missionLike(author, imgId) ?: return

        call.enqueue(object : retrofit2.Callback<SimpleResponse> {
            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {
                Log.d("미션 인증 사진 좋아요", "RetrofitManager 미션 인증 사진 좋아요 onResponse \t :${response.message()} ")
                val response: SimpleResponse? = response?.body()
                if (response != null) {
                    if (response.isSuccess=="true") {
                        Log.d("미션 인증 사진 좋아요",
                            "RetrofitManager 미션 인증 사진 좋아요 is Success\t :${response.code} ")
                        Log.d("미션 인증 사진 좋아요",
                            "RetrofitManager 미션 인증 사진 좋아요 is Success\t :${response.result} ")
                        completion(true)
                    } else {
                        Log.d("미션 인증 사진 좋아요",
                            "RetrofitManager 미션 인증 사진 좋아요 is NOT Success\t :${response.code} ")
                        completion(false)
                    }
                } else {
                    Log.d("미션 인증 사진 좋아요", "RetrofitManager 미션 인증 사진 좋아요 null")
                }
            }
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Log.d("미션 인증 사진 좋아요", "RetrofitManager 미션 인증 사진 좋아요 onFailure \t :$t ")
            }
        })
    }

    //미션 인증 사진 좋아요 취소
    fun missionImgUnlike(author: String,imgId:Int, completion: (isSuccess:Boolean) -> Unit){
        val call: Call<SimpleResponse> = retrofit?.missionUnlike(author, imgId) ?: return

        call.enqueue(object : retrofit2.Callback<SimpleResponse> {
            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {
                Log.d("미션 인증 사진 좋아요 취소", "RetrofitManager 미션 인증 사진 좋아요 취소 onResponse \t :${response.message()} ")
                val response: SimpleResponse? = response?.body()
                if (response != null) {
                    if (response.isSuccess=="true") {
                        Log.d("미션 인증 사진 좋아요 취소",
                            "RetrofitManager 미션 인증 사진 좋아요 취소 is Success\t :${response.code} ")
                        Log.d("미션 인증 사진 좋아요 취소",
                            "RetrofitManager 미션 인증 사진 좋아요 취소 is Success\t :${response.result} ")
                        completion(true)
                    } else {
                        Log.d("미션 인증 사진 좋아요 취소",
                            "RetrofitManager 미션 인증 사진 좋아요 취소 is NOT Success\t :${response.code} ")
                        completion(false)
                    }
                } else {
                    Log.d("미션 인증 사진 좋아요 취소", "RetrofitManager 미션 인증 사진 좋아요 취소 null")
                }
            }
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Log.d("미션 인증 사진 좋아요 취소", "RetrofitManager 미션 인증 사진 좋아요 취소 onFailure \t :$t ")
            }
        })
    }

    //미션 인증 사진 신고
    fun report(author: String,imgId:Int, completion: (isSuccess:Boolean) -> Unit){
        val call: Call<SimpleResponse> = retrofit?.missionReport(author, imgId) ?: return

        call.enqueue(object : retrofit2.Callback<SimpleResponse> {
            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {
                Log.d("미션 인증 사진 신고", "RetrofitManager 미션 인증 사진 신고 onResponse \t :${response.message()} ")
                val response: SimpleResponse? = response?.body()
                if (response != null) {
                    if (response.isSuccess=="true") {
                        Log.d("미션 인증 사진 신고",
                            "RetrofitManager 미션 인증 사진 신고 is Success\t :${response.code} ")
                        Log.d("미션 인증 사진 신고",
                            "RetrofitManager 미션 인증 사진 신고 is Success\t :${response.result} ")
                        completion(true)
                    } else {
                        Log.d("미션 인증 사진 신고",
                            "RetrofitManager 미션 인증 사진 신고 is NOT Success\t :${response.code} ")
                        completion(false)
                    }
                } else {
                    Log.d("미션 인증 사진 신고", "RetrofitManager 미션 인증 사진 신고 null")
                }
            }
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Log.d("미션 인증 사진 신고", "RetrofitManager 미션 인증 사진 신고 onFailure \t :$t ")
            }
        })
    }

    //미션 인증 사진 올리기
    fun postImg(author: String,boardId:Long, filePath:String,completion: (isSuccess:Boolean) -> Unit){
        val call: Call<SimpleResponse> = retrofit?.postImg(author, boardId, filePath) ?: return

        call.enqueue(object : retrofit2.Callback<SimpleResponse> {
            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {
                Log.d("미션 인증 사진 첨부", "RetrofitManager 미션 인증 사진 첨부 onResponse \t :${response.message()} ")
                val response: SimpleResponse? = response?.body()
                if (response != null) {
                    if (response.isSuccess=="true") {
                        Log.d("미션 인증 사진 첨부",
                            "RetrofitManager 미션 인증 사진 첨부 is Success\t :${response.code} ")
                        Log.d("미션 인증 사진 첨부",
                            "RetrofitManager 미션 인증 사진 첨부 is Success\t :${response.result} ")
                        completion(true)
                    } else {
                        Log.d("미션 인증 사진 첨부",
                            "RetrofitManager 미션 인증 사진 첨부 is NOT Success\t :${response.code} ")
                        completion(false)
                    }
                } else {
                    Log.d("미션 인증 사진 첨부", "RetrofitManager 미션 인증 사진 첨부 null")
                }
            }
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Log.d("미션 인증 사진 첨부", "RetrofitManager 미션 인증 사진 첨부 onFailure \t :$t ")
            }
        })
    }
}