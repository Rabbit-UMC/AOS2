package com.example.myo_jib_sa.Login.API

data class SignUpResponse(
    val isSucces : String,
    val code : Int,
    val message: String,
    val result: SignUpResult?
)
data class SignUpResult(
    val id:Long,
    val userEmail: String,
    val userName: String
)
