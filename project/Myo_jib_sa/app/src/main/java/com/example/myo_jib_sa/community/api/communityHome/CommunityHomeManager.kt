package com.example.myo_jib_sa.community.api.communityHome

import android.content.Context
import android.util.Log
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.sRetrofit
import retrofit2.Call
import retrofit2.Response

class CommunityHomeManager() {
    //레트로핏 인터페이스 가져오기기
    private val retrofit:CommunityHomeITFC = sRetrofit.create(CommunityHomeITFC::class.java)

    companion object {
        private var instance: CommunityHomeManager? = null

        // 싱글톤 인스턴스를 가져오는 메서드
        fun getInstance(context: Context): CommunityHomeManager {
            if (instance == null) {
                instance = CommunityHomeManager()
            }
            return instance as CommunityHomeManager
        }

    }

    //homeResponse를 반환
    fun home(completion: (HomeResponse) -> Unit){
        val call: Call<HomeResponse> = retrofit?.home() ?: return

        call.enqueue(object : retrofit2.Callback<HomeResponse> {
            override fun onResponse(
                call: Call<HomeResponse>,
                response: Response<HomeResponse>
            ) {
                Log.d("홈 api", "RetrofitManager profile onResponse \t :${response.message()} ")
                val response: HomeResponse? = response?.body() //response 형식의 응답 받음
                if (response != null) {
                    if (response.isSuccess) {
                        Log.d("홈 api",
                            "RetrofitManager 커뮤니티 홈 is Success\t :${response.errorCode}, ${response.errorMessage} ")

                        completion(response)
                    } else {
                        Log.d("홈 api",
                            "RetrofitManager 커뮤니티 홈 is NOT Success\t :${response.errorCode}, ${response.errorMessage}")
                    }
                } else {
                    Log.d("홈 api", "RetrofitManager 커뮤니티 홈 null")
                }
            }

            override fun onFailure(call: Call<HomeResponse>, t: Throwable) {
                Log.d("홈 api", "RetrofitManager 커뮤니티 홈 onFailure \t :$t ")
            }
        })
    }

}