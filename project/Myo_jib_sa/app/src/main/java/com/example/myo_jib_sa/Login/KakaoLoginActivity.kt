package com.example.myo_jib_sa.Login

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.myo_jib_sa.BuildConfig
import com.example.myo_jib_sa.Login.API.LoginITFC
import com.example.myo_jib_sa.Login.API.LoginResponse
import com.example.myo_jib_sa.Login.API.RetrofitInstance
import com.example.myo_jib_sa.MainActivity
import com.example.myo_jib_sa.databinding.ActivityKakaoLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KakaoLoginActivity : AppCompatActivity(),OnEmailEnteredInterface {
    private lateinit var binding: ActivityKakaoLoginBinding
    private lateinit var accessToken:String
    var kakaoEmail: String? = null
    // Retrofit 객체 가져오기
    val retrofit = RetrofitInstance.getInstance().create(LoginITFC::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKakaoLoginBinding.inflate(layoutInflater)
        installSplashScreen()
        setContentView(binding.root)

        // 자동로그인 처리
        val sharedPreferences = getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        val savedJwt = sharedPreferences.getString("jwt", null)

        Log.d("Login","${savedJwt}")

        //저장된 Jwt가 있다면 바로 홈으로
        if (savedJwt != null ) {
            // 로그인 성공 처리
            Log.d("Login","${savedJwt}")
            val intent = Intent(this@KakaoLoginActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
        //아닐경우 회원가입

            binding.kakaoLoginBtn.setOnClickListener {

                //kakao로그인
                if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@KakaoLoginActivity)) {
                    UserApiClient.instance.loginWithKakaoTalk(this@KakaoLoginActivity, callback = callback)
                } else {
                    UserApiClient.instance.loginWithKakaoAccount(this@KakaoLoginActivity, callback = callback)
                }
            }


    }

    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error)
        } else if (token != null) {
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e("kakao", "사용자 정보 요청 실패", error)
                } else if (user != null) {
                    kakaoEmail = user.kakaoAccount?.email
                    accessToken=token.accessToken
                    Log.d("token", accessToken)
                    if (kakaoEmail != null) {
                        // 카카오이메일 있으면 바로 묘집사 회원가입으로 이동
                        val clientId = BuildConfig.KAKAO_API_KEY
                        val redirectUri = BuildConfig.Redirect_URI
                        val responseType = "code"

                        //Login API 연결
                        retrofit.Login(accessToken,clientId, redirectUri, responseType).enqueue(object :
                            Callback<LoginResponse> {
                            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                                if (response.isSuccessful) {
                                    val loginResponse = response.body()
                                    loginResponse?.let {
                                        Log.d("LoginResponse", "isSuccess: ${it.isSucces}")
                                        Log.d("LoginResponse", "code: ${it.code}")
                                        Log.d("LoginResponse", "message: ${it.message}")
                                        it.result?.let { loginResult ->
                                            val jwtToken = loginResult.jwtAccessToken
                                            Log.d("LoginResponse", "id: ${loginResult.id}")
                                            Log.d("LoginResponse", "jwt: ${jwtToken}")

                                            // jwtToken을 sharedPreference에 저장하기
                                            val sharedPreferences = getSharedPreferences("getJwt", Context.MODE_PRIVATE)
                                            val getJwt = sharedPreferences.edit()
                                            // JWT 저장
                                            val jwt = jwtToken
                                            if (jwt != null) {
                                                getJwt.putString("jwt", jwt)
                                            }
                                            getJwt.apply()
                                        }
                                    }
                                } else {
                                    val errorBody = response.errorBody()?.string()
                                    Log.e("LoginResponse", "API 호출 실패: ${response.code()}, ${response.message()}")
                                    Log.e("LoginResponse", "Error Body: $errorBody")}
                            }

                            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                Log.e("LoginResponse", "onFail API 호출 실패: ${t.message}")
                            }
                        })
                        val intent = Intent(this@KakaoLoginActivity, MyoSignUpActivity::class.java)
                        intent.putExtra("accessToken",accessToken)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        Log.d("token", accessToken)
                        finish()
                    } else {
                        // 카카오이메일 없으면 추가 이메일 입력 다이얼로그로 이동
                        showAddEmailDialog()
                    }
                }
            }
            Log.d("token", "카카오계정으로 로그인 성공 ${token.accessToken}")
        }
    }

    // 이메일 입력 완료 시 호출되는 콜백 함수
    override fun onEmailEntered(email: String) {
        // 이메일 값 처리 후 MyoSignUpActivity로 이동
        val intent = Intent(this@KakaoLoginActivity, MyoSignUpActivity::class.java)
        intent.putExtra("email", email)
        intent.putExtra("accessToken",accessToken)
        startActivity(intent)
        finish()
    }
    private fun showAddEmailDialog() {
        val dialog = LoginAddEmailDialogFragment(this)
        //dialog.setOnEmailEnteredListener(this)
        dialog.show(supportFragmentManager, "add_email_dialog")
    }

}
