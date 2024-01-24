package com.example.myo_jib_sa.signup

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.myo_jib_sa.login.api.SignUpTFC
import com.example.myo_jib_sa.signup.api.LoginResponse
import com.example.myo_jib_sa.MainActivity
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.sRetrofit
import com.example.myo_jib_sa.databinding.ActivityLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var accessToken:String
    // Retrofit 객체 가져오기
    private val retrofit = sRetrofit.create(SignUpTFC::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        installSplashScreen()
        setContentView(binding.root)

/*        // 앱 시작 시 자동 로그인 체크
        val sharedPreferences = getSharedPreferences("getJwt", Context.MODE_PRIVATE)

        val jwtToken = sharedPreferences.getString("jwt", null)
        if (jwtToken != null) {
            // 저장된 토큰이 있다면 자동으로 로그인
            autoLogin(jwtToken)
        }*/


        binding.kakaoLoginBtn.setOnClickListener {
            kakaoLogin()
        }


    }


    private fun kakaoLogin() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("LOGIN", "Login fail with kakao account", error)
            } else if (token != null) {
                Log.i("LOGIN", "Login success with kakao account : ${token.idToken}")
                //getLogin(token.idToken!!)
            }
        }
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Log.e("LOGIN", "Login fail with kakaoTalk", error)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                } else if (token != null) {
                    Log.i("LOGIN", "Login success with kakao account : ${token.idToken}")
                    //getLogin(token.idToken!!)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }

    private fun autoLogin(jwtToken: String) {
        // 로그인 성공 시 메인 화면으로 이동
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun getLogin(accessToken: String) {
        retrofit.getLogin(accessToken).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    loginResponse?.let {
                        Log.d("LoginResponse", "code: ${it.errorCode}")
                        Log.d("LoginResponse", "message: ${it.errorMessage}")
                        it.result?.let { loginResult ->
                            val jwtToken = loginResult.jwtAccessToken
                            Log.d("LoginResponse", "id: ${loginResult.id}")
                            Log.d("LoginResponse", "jwt: ${jwtToken}")

                            // jwtToken을 sharedPreference에 저장하기
                            val sharedPreferences = getSharedPreferences("getJwt", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            // JWT 저장
                            val jwt = jwtToken
                            if (jwt != null) {
                                editor.putString("jwt", jwt)
                            }
                            // userId 저장
                            val userId = loginResult.id // 이 부분을 실제 userId 값으로 바꿔주세요
                            editor.putLong("userId", userId)

                            editor.apply()
                            /* val sharedPreferences = getSharedPreferences("getJwt", Context.MODE_PRIVATE)
                             val getJwt = sharedPreferences.edit()
                             // JWT 저장
                             val jwt ="eyJ0eXBlIjoiand0IiwiYWxnIjoiSFMyNTYifQ.eyJ1c2VySWR4IjoxLCJpYXQiOjE2OTE2NjUwMzIsImV4cCI6MTY5MzEzNjI2MX0.mlOx8WnywdEYGNDHkhlqP3agL3rVMyvwgwWP8VlvsXM"
                             if (jwt != null) {
                                 getJwt.putString("jwt", jwt)
                             }
                             getJwt.apply()*/
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LoginResponse", "API 호출 실패: ${response.code()}, ${response.message()}")
                    Log.e("LoginResponse", "Error Body: $errorBody")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginResponse", "onFail API 호출 실패: ${t.message}")
            }
        })
    }

}