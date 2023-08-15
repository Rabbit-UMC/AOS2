package com.example.myo_jib_sa.community.Retrofit.manager

import android.content.Context
import android.util.Log
import com.example.myo_jib_sa.community.Retrofit.Constance
import com.example.myo_jib_sa.community.Retrofit.RetrofitClient
import com.example.myo_jib_sa.community.Retrofit.post.PostRetrofitITFC
import com.example.myo_jib_sa.community.Retrofit.post.PostRetrofitManager
import com.example.myo_jib_sa.community.Retrofit.post.SimpleResponse
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
        val call: Call<SimpleResponse> = retrofit?.EditPhoto(author, filePath,boardId) ?: return

        call.enqueue(object : retrofit2.Callback<SimpleResponse> {
            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {
                Log.d("미션 대표 사진 바꾸기", "RetrofitManager 미션 대표 사진 바꾸기 onResponse \t :${response.message()} ")
                val response: SimpleResponse? = response?.body()
                if (response != null) {
                    if (response.isSuccess=="true") {
                        Log.d("미션 대표 사진 바꾸기",
                            "RetrofitManager 미션 대표 사진 바꾸기 is Success\t :${response.code} ")
                        Log.d("미션 대표 사진 바꾸기",
                            "RetrofitManager 미션 대표 사진 바꾸기 is Success\t :${response.result} ")
                        completion(true)
                    } else {
                        Log.d("미션 대표 사진 바꾸기",
                            "RetrofitManager 미션 대표 사진 바꾸기 is NOT Success\t :${response.code} ")
                        completion(false)
                    }
                } else {
                    Log.d("미션 대표 사진 바꾸기", "RetrofitManager 미션 대표 사진 바꾸기 null")
                }
            }
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Log.d("미션 대표 사진 바꾸기", "RetrofitManager 미션 대표 사진 바꾸기 onFailure \t :$t ")
            }
        })
    }

    //미션 생성하기
    //미션 대표 사진 바꾸기
    fun missionCreate(author: String,data:MissionCreateRequest, boardId: Long,completion: (isSucces:Boolean) -> Unit){
        val call: Call<SimpleResponse> = retrofit?.missionCreate(author, data,boardId) ?: return

        call.enqueue(object : retrofit2.Callback<SimpleResponse> {
            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {
                Log.d("미션 생성", "RetrofitManager 미션 생성 onResponse \t :${response.message()} ")
                val response: SimpleResponse? = response?.body()
                if (response != null) {
                    if (response.isSuccess=="true") {
                        Log.d("미션 생성",
                            "RetrofitManager 미션 생성 is Success\t :${response.code} ")
                        Log.d("미션 생성",
                            "RetrofitManager 미션 생성 is Success\t :${response.result} ")
                        completion(true)
                    } else {
                        Log.d("미션 생성",
                            "RetrofitManager 미션 생성 is NOT Success\t :${response.code} ")
                        completion(false)
                    }
                } else {
                    Log.d("미션 생성", "RetrofitManager 미션 생성 null")
                }
            }
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Log.d("미션 생성", "RetrofitManager 미션 생성 onFailure \t :$t ")
            }
        })
    }
}