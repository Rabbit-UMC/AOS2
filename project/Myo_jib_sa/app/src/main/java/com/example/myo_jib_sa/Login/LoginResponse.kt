package com.example.myo_jib_sa.Login

data class LoginResponse(
    val isSucces : String,
    val code : Int,
    val message: String,
    val result: LoginResult?
)
data class LoginResult(
    val id:Long,
    val userEmail: Int,
    val userName: String
)
