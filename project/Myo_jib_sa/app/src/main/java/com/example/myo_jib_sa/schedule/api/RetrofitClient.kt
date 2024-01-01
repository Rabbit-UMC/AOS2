package com.example.myo_jib_sa.schedule.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient{
    private const val BASE_URL = "https://www.myo-zip-sa.store/"//"https://umc.ljhhosting.com/api/"//교체하기!!!!!!!!!1

    private var instance: Retrofit? = null

    open fun getInstance() : Retrofit {
        if (instance == null) {
            instance = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        } // end if

        return instance!!
    }
}