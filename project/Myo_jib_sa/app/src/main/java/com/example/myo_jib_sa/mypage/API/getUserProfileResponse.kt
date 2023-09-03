package com.example.myo_jib_sa.mypage.API

data class getUserProfileResponse(
    val isSucess:String,
    val code:Int,
    val messeage:String,
    val result: Profile?
)

data class Profile(
    val userEmail:String,
    val userName:String,
    val userProfileImage:String
)
