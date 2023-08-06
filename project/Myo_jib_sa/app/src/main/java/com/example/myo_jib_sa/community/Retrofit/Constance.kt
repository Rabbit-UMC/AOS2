package com.example.myo_jib_sa.community.Retrofit

import com.example.myo_jib_sa.BuildConfig

object Constance {
   const val BASEURL:String="https://www.myo-zip-sa.store/" //베이스 url
    const val ART_ID:Int=3 //예술 게시판 id
    const val EXERCISE_ID:Int=2 //운동 게시판 id
    const val FREE_ID:Int=1 //자유 게시판 ID
    const val author="Authorization"

    //유저 id 13인 토큰
    const val jwt:String= BuildConfig.API_KEY
    const val USER_ID:Long=13
}

