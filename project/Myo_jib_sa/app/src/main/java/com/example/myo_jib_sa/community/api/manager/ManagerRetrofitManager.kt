package com.example.myo_jib_sa.community.api.manager

import android.content.Context
import android.util.Log
import com.example.myo_jib_sa.base.BaseResponse
import com.example.myo_jib_sa.community.Constance
import com.example.myo_jib_sa.community.api.RetrofitClient
import com.example.myo_jib_sa.community.api.post.SimpleResponse
import retrofit2.Call
import retrofit2.Response

class ManagerRetrofitManager (context: Context){
    //레트로핏 인터페이스 가져오기기
    private val retrofit : ManagerRetrofitITFC? = RetrofitClient.getClient(Constance.BASEURL)?.create(
        ManagerRetrofitITFC::class.java)

    companion object {
        private var instance: ManagerRetrofitManager? = null

        // 싱글톤 인스턴스를 가져오는 메서드
        fun getInstance(context: Context): ManagerRetrofitManager {
            if (instance == null) {
                instance = ManagerRetrofitManager(context)
            }
            return instance as ManagerRetrofitManager
        }

    }


    //미션 대표 사진 바꾸기
    fun missionImgEdit(author: String,filePath:String,boardId:Long, completion: (isSucces:Boolean) -> Unit){
        val request=PatchCategoryImageReq(filePath)
        val call: Call<BaseResponse> = retrofit?.EditPhoto(author,boardId, request) ?: return
        Log.d("RetrofitManager 미션 대표 사진 바꾸기", "데이터 확인 $filePath")
        call.enqueue(object : retrofit2.Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                Log.d("RetrofitManager 미션 대표 사진 바꾸기", "RetrofitManager 미션 대표 사진 바꾸기 onResponse \t :${response.message()} ")
                Log.d("RetrofitManager 미션 대표 사진 바꾸기", "RetrofitManager 미션 대표 사진 바꾸기 onResponse \t :${response.code()} ")
                val response: BaseResponse? = response?.body()
                if (response != null) {
                    if (response.isSuccess) {
                        Log.d("RetrofitManager 미션 대표 사진 바꾸기",
                            "RetrofitManager 미션 대표 사진 바꾸기 is Success\t :${response.errorMessage}   ${response.errorCode}")
                        Log.d("RetrofitManager 미션 대표 사진 바꾸기",
                            "RetrofitManager 미션 대표 사진 바꾸기 is Success\t :${response} ")
                        completion(true)
                    } else {
                        Log.d("RetrofitManager 미션 대표 사진 바꾸기",
                            "RetrofitManager 미션 대표 사진 바꾸기 is NOT Success\t :${response.errorMessage}   ${response.errorCode}")
                        completion(false)
                    }
                } else {
                    Log.d("RetrofitManager 미션 대표 사진 바꾸기", "RetrofitManager 미션 대표 사진 바꾸기 null")
                }
            }
            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.d("미션 대표 사진 바꾸기", "RetrofitManager 미션 대표 사진 바꾸기 onFailure \t :$t ")
            }
        })
    }

    //미션 생성하기
    //미션 대표 사진 바꾸기
    fun missionCreate(author: String,data:MissionCreateRequest, boardId: Long,completion: (isSucces:Boolean) -> Unit){
        val call: Call<BaseResponse> = retrofit?.missionCreate(author,boardId,data) ?: return

        call.enqueue(object : retrofit2.Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                Log.d("미션 생성", "RetrofitManager 미션 생성 onResponse \t :${response.message()} ")
                val response: BaseResponse? = response?.body()
                if (response != null) {
                    if (response.isSuccess) {
                        Log.d("미션 생성",
                            "RetrofitManager 미션 생성 is Success\t :${response.errorMessage}   ${response.errorCode}")
                        Log.d("미션 생성",
                            "RetrofitManager 미션 생성 is Success\t :${response.errorMessage}   ${response.errorCode}")
                        completion(true)
                    } else {
                        Log.d("미션 생성",
                            "RetrofitManager 미션 생성 is NOT Success\t :${response.errorMessage}   ${response.errorCode}")
                        completion(false)
                    }
                } else {
                    Log.d("미션 생성", "RetrofitManager 미션 생성 null")
                }
            }
            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.d("미션 생성", "RetrofitManager 미션 생성 onFailure \t :$t ")
            }
        })
    }
}