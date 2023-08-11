package com.example.myo_jib_sa.community.Retrofit

import com.example.myo_jib_sa.BuildConfig

object Constance {
   const val BASEURL:String="https://www.myo-zip-sa.store/" //베이스 url
    const val ART_ID:Int=3 //예술 게시판 id
    const val EXERCISE_ID:Int=2 //운동 게시판 id
    const val FREE_ID:Int=1 //자유 게시판 ID
    const val author="X-ACCESS-TOKEN"

    //유저 id 13인 토큰
    const val jwt:String="eyJ0eXBlIjoiand0IiwiYWxnIjoiSFMyNTYifQ.eyJ1c2VySWR4IjoxLCJpYXQiOjE2OTE2NjUwMzIsImV4cCI6MTY5MzEzNjI2MX0.mlOx8WnywdEYGNDHkhlqP3agL3rVMyvwgwWP8VlvsXM"
    const val USER_ID:Long=13


}


//이미지 경로 지정할 떄 사용
object ImgPath{
  const val POST="article"
 const val USER="user"
 const val CATEGORY="category"
}