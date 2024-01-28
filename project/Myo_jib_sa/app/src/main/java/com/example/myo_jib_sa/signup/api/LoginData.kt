package com.example.myo_jib_sa.signup.api

import com.example.myo_jib_sa.base.BaseResponse

data class LoginResponse(
    val result: LoginResult?
): BaseResponse()
data class LoginResult(
    val id:Long,
    val jwtAccessToken:String,
    val jwtRefreshToken:String
)

data class SignUpRequest(
    val userName:String
)

data class SignUpResponse(
    val result: SignUpResult?
): BaseResponse()
data class SignUpResult(
    val id:Long,
    val jwtAccessToken:String,
    val jwtRefreshToken:String
)
