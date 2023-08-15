package com.example.myo_jib_sa.community.Retrofit

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    //레트로핏 클라이언트 선언
    private var retrofitClient: Retrofit?=null

    //레트로핏 클라이언트 가져오기
    fun getClient(baseUrl: String): Retrofit?{
        Log.d("홈 api", "RetrofitClient - getClient() call")

        //없으면 할당
        if(retrofitClient ==null){
            retrofitClient =Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofitClient
    }
}