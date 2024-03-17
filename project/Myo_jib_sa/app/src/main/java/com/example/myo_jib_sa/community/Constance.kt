package com.example.myo_jib_sa.community

import android.content.Context
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.spfManager


object Constance {
    const val ART_ID:Long=3 //예술 게시판 id
    const val EXERCISE_ID:Long=2 //운동 게시판 id
    const val FREE_ID:Long=1 //자유 게시판 ID

    val USER_ID:Long=spfManager.getUserId()
}


//이미지 경로 지정할 떄 사용
object ImgPath{
  const val POST="article"
 const val CATEGORY="category"
}