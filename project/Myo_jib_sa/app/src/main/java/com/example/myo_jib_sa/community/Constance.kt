package com.example.myo_jib_sa.community

import android.content.Context


object Constance {
    const val BASEURL:String="https://www.myo-zip-sa.store/" //베이스 url
    const val ART_ID:Int=3 //예술 게시판 id
    const val EXERCISE_ID:Int=2 //운동 게시판 id
    const val FREE_ID:Int=1 //자유 게시판 ID
    const val author="X-ACCESS-TOKEN"

    const val ART_COLOR="#FFC436"
    const val EXERCISE_COLOR="#234BD9"
    const val FREE_COLOR="#C1C1C1"

    //유저 id 1인 토큰
    var jwt: String? = null
    const val USER_ID:Long=1

     fun initializeJwt(context: Context) {
        val sharedPreferences = context.getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        jwt = sharedPreferences.getString("jwt", null)
     }
}


//이미지 경로 지정할 떄 사용
object ImgPath{
  const val POST="article"
 const val USER="user"
 const val CATEGORY="category"
}