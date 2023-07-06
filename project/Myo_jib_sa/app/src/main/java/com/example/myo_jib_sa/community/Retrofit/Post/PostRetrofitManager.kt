package com.example.myo_jib_sa.community.Retrofit.Post

import android.content.Context
import android.util.Log
import com.example.myo_jib_sa.community.Retrofit.Constance
import com.example.myo_jib_sa.community.Retrofit.RetrofitClient
import com.example.myo_jib_sa.community.Retrofit.communityHome.CommunityHomeITFC
import com.example.myo_jib_sa.community.Retrofit.communityHome.CommunityHomeManager
import com.example.myo_jib_sa.community.Retrofit.communityHome.HomeResponse
import retrofit2.Call
import retrofit2.Response

class PostRetrofitManager(context: Context) {
    //레트로핏 인터페이스 가져오기기
    private val retrofit : PostRetrofitITFC? = RetrofitClient.getClient(Constance.BASEURL)?.create(
        PostRetrofitITFC::class.java)

    companion object {
        private var instance: PostRetrofitManager? = null

        // 싱글톤 인스턴스를 가져오는 메서드
        fun getInstance(context: Context): PostRetrofitManager {
            if (instance == null) {
                instance = PostRetrofitManager(context)
            }
            return instance as PostRetrofitManager
        }

    }

    //PostBoardResponse를 반환
    fun board(author:String, completion: (PostBoardResponse) -> Unit){
        val call: Call<PostBoardResponse> = retrofit?.board(author) ?: return

        call.enqueue(object : retrofit2.Callback<PostBoardResponse> {
            override fun onResponse(
                call: Call<PostBoardResponse>,
                response: Response<PostBoardResponse>
            ) {
                Log.d("Post 게시판 api", "RetrofitManager profile onResponse \t :${response.message()} ")
                val response: PostBoardResponse? = response?.body() //LoginResponse 형식의 응답 받음
                if (response != null) {
                    if (response.isSuccess=="TRUE") {
                        Log.d("Post 게시판 api",
                            "RetrofitManager Post 게시판 is Success\t :${response.code} ")
                        completion(response)
                    } else {
                        Log.d("Post 게시판 api",
                            "RetrofitManager Post 게시판 is NOT Success\t :${response.code} ")
                    }
                } else {
                    Log.d("Post 게시판 api", "RetrofitManager Post 게시판 null")
                }
            }

            override fun onFailure(call: Call<PostBoardResponse>, t: Throwable) {
                Log.d("Post 게시판 api", "RetrofitManager Post 게시판 onFailure \t :$t ")
            }
        })
    }
}