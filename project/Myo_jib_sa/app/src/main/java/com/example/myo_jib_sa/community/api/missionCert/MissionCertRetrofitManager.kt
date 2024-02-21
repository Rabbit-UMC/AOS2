package com.example.myo_jib_sa.community.api.missionCert

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myo_jib_sa.base.BaseResponse
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.sRetrofit
import com.example.myo_jib_sa.community.Constance
import com.example.myo_jib_sa.community.api.RetrofitClient
import com.example.myo_jib_sa.community.api.post.SimpleResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response

class MissionCertRetrofitManager(context: Context) : ViewModel() {
    //레트로핏 인터페이스 가져오기기
    private val retrofit=sRetrofit.create(MissionCertRetrofitITFC::class.java)

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
    fun mission(day:Int,mainMissionId:Long, completion: (missionResponse: MissionResponse) -> Unit){

            val call: Call<MissionResponse> = retrofit?.mission(mainMissionId, day) ?: return

            call.enqueue(object : retrofit2.Callback<MissionResponse> {
                override fun onResponse(
                    call: Call<MissionResponse>,
                    response: Response<MissionResponse>
                ) {
                    Log.d("미션 화면 조회", "RetrofitManager 미션 화면 조회 onResponse \t :${response.message()} ")
                    val response: MissionResponse? = response?.body()
                    if (response != null) {
                        if (response.isSuccess) {
                            Log.d("미션 화면 조회",
                                "RetrofitManager 미션 화면 조회 is Success\t :${response.errorCode} ")
                            Log.d("미션 화면 조회",
                                "RetrofitManager 미션 화면 조회 is Success\t :${response.errorMessage} ")
                            completion(response)
                        } else {
                            Log.d("미션 화면 조회",
                                "RetrofitManager 미션 화면 조회 is NOT Success\t :${response.errorCode} ")
                            Log.d("미션 화면 조회",
                                "RetrofitManager 미션 화면 조회 is Success\t :${response.errorMessage} ")
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
    fun missionImgLike(imgId:Long, completion: (isSuccess:Boolean) -> Unit){
        val call: Call<BaseResponse> = retrofit?.missionLike(imgId) ?: return

        call.enqueue(object : retrofit2.Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                Log.d("미션 인증 사진 좋아요", "RetrofitManager 미션 인증 사진 좋아요 onResponse \t :${response.message()} ")
                val response: BaseResponse? = response?.body()
                if (response != null) {
                    if (response.isSuccess) {
                        Log.d("미션 인증 사진 좋아요",
                            "RetrofitManager 미션 인증 사진 좋아요 is Success\t :${response.errorCode} ${response.errorMessage} ")
                        Log.d("미션 인증 사진 좋아요",
                            "RetrofitManager 미션 인증 사진 좋아요 is Success\t :${response.errorCode} ${response.errorMessage}")
                        completion(true)
                    } else {
                        Log.d("미션 인증 사진 좋아요",
                            "RetrofitManager 미션 인증 사진 좋아요 is NOT Success\t :${response.errorCode} ${response.errorMessage}")
                        completion(false)
                    }
                } else {
                    Log.d("미션 인증 사진 좋아요", "RetrofitManager 미션 인증 사진 좋아요 null")
                }
            }
            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.d("미션 인증 사진 좋아요", "RetrofitManager 미션 인증 사진 좋아요 onFailure \t :$t ")
            }
        })
    }

    //미션 인증 사진 좋아요 취소
    fun missionImgUnlike(imgId:Long, completion: (isSuccess:Boolean) -> Unit){
        val call: Call<BaseResponse> = retrofit?.missionUnlike(imgId) ?: return

        call.enqueue(object : retrofit2.Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                Log.d("미션 인증 사진 좋아요 취소", "RetrofitManager 미션 인증 사진 좋아요 취소 onResponse \t :${response.message()} ")
                val response: BaseResponse? = response?.body()
                if (response != null) {
                    if (response.isSuccess) {
                        Log.d("미션 인증 사진 좋아요 취소",
                            "RetrofitManager 미션 인증 사진 좋아요 취소 is Success\t :${response.errorCode} ${response.errorMessage} ")
                        Log.d("미션 인증 사진 좋아요 취소",
                            "RetrofitManager 미션 인증 사진 좋아요 취소 is Success\t :${response.errorCode} ${response.errorMessage}")
                        completion(true)
                    } else {
                        Log.d("미션 인증 사진 좋아요 취소",
                            "RetrofitManager 미션 인증 사진 좋아요 취소 is NOT Success\t :${response.errorCode} ${response.errorMessage}")
                        completion(false)
                    }
                } else {
                    Log.d("미션 인증 사진 좋아요 취소", "RetrofitManager 미션 인증 사진 좋아요 취소 null")
                }
            }
            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.d("미션 인증 사진 좋아요 취소", "RetrofitManager 미션 인증 사진 좋아요 취소 onFailure \t :$t ")
            }
        })
    }

    //미션 인증 사진 신고
    fun report(imgId:Long, completion: (isSuccess:Boolean) -> Unit){
        val call: Call<BaseResponse> = retrofit?.missionReport(imgId) ?: return

        call.enqueue(object : retrofit2.Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                Log.d("미션 인증 사진 신고", "RetrofitManager 미션 인증 사진 신고 onResponse \t :${response.message()} ")
                val response: BaseResponse? = response?.body()
                if (response != null) {
                    if (response.isSuccess) {
                        Log.d("미션 인증 사진 신고",
                            "RetrofitManager 미션 인증 사진 신고 is Success\t :${response.errorCode} ${response.errorMessage} ")
                        Log.d("미션 인증 사진 신고",
                            "RetrofitManager 미션 인증 사진 신고 is Success\t :${response.errorCode} ${response.errorMessage} ")
                        completion(true)
                    } else {
                        Log.d("미션 인증 사진 신고",
                            "RetrofitManager 미션 인증 사진 신고 is NOT Success\t :${response.errorCode} ${response.errorMessage}")
                        completion(false)
                    }
                } else {
                    Log.d("미션 인증 사진 신고", "RetrofitManager 미션 인증 사진 신고 null")
                }
            }
            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.d("미션 인증 사진 신고", "RetrofitManager 미션 인증 사진 신고 onFailure \t :$t ")
            }
        })
    }

    //미션 인증 사진 올리기
    fun postImg(boardId:Long, filePath:MultipartBody.Part ,completion: (isSuccess:Boolean) -> Unit){
        val call: Call<BaseResponse> = retrofit?.postImg(boardId, filePath) ?: return

        call.enqueue(object : retrofit2.Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                Log.d("미션 인증 사진 첨부", "RetrofitManager 미션 인증 사진 첨부 onResponse \t :${response.message()} ")
                val response: BaseResponse? = response?.body()
                if (response != null) {
                    if (response.isSuccess) {
                        Log.d("미션 인증 사진 첨부",
                            "RetrofitManager 미션 인증 사진 첨부 is Success\t :${response.errorCode} ${response.errorMessage} ")
                        Log.d("미션 인증 사진 첨부",
                            "RetrofitManager 미션 인증 사진 첨부 is Success\t :${response.errorCode} ${response.errorMessage}")
                        completion(true)
                    } else {
                        Log.d("미션 인증 사진 첨부",
                            "RetrofitManager 미션 인증 사진 첨부 is NOT Success\t :${response.errorCode} ${response.errorMessage} ")
                        completion(false)
                    }
                } else {
                    Log.d("미션 인증 사진 첨부", "RetrofitManager 미션 인증 사진 첨부 null")
                }
            }
            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.d("미션 인증 사진 첨부", "RetrofitManager 미션 인증 사진 첨부 onFailure \t :$t ")
            }
        })
    }
}