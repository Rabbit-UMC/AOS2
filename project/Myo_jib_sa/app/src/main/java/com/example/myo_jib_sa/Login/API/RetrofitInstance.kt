package com.example.myo_jib_sa.Login.API

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private var BASE_URL = "https://www.myo-zip-sa.store/"
    private var instance: Retrofit? = null


    fun getInstance(): Retrofit {
        if (instance == null) {
            // 로깅 인터셉터 생성
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // OkHttpClient에 로깅 인터셉터 추가
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            instance = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client) // OkHttpClient 설정 적용
                .build()
        }
        return instance!!
    }
}
