package com.example.myo_jib_sa.base

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import com.example.myo_jib_sa.BuildConfig
import com.example.myo_jib_sa.utils.SharedPreferencesUtil
import com.example.myo_jib_sa.utils.Utils.BASE_URL
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MyojibsaApplication : Application() {
    companion object {
        private lateinit var appInstance: MyojibsaApplication
        fun getApplication() = appInstance

        lateinit var spfManager: SharedPreferencesUtil

        // JWT Token Header 키 값
        const val X_ACCESS_TOKEN = "X-ACCESS-TOKEN"
        const val X_REFRESH_TOKEN = "X-REFRESH-TOKEN"

        // Retrofit 인스턴스, 앱 실행시 한번만 생성하여 사용합니다.
        lateinit var sRetrofit: Retrofit
    }

    // 앱이 처음 생성되는 순간, SP를 새로 만들어주고, 레트로핏 인스턴스를 생성합니다.
    override fun onCreate() {
        super.onCreate()
        appInstance = this

        //KakaoSdk.init(this, "${BuildConfig.KAKAO_API_KEY}")
        // 레트로핏 인스턴스 생성
        initRetrofitInstance()

        spfManager = SharedPreferencesUtil(applicationContext)

//        val keyHash = Utility.getKeyHash(this)
//        Log.d("Hash", keyHash)
        val nativeKey = BuildConfig.KAKAO_NATIVE_KEY
        KakaoSdk.init(this, nativeKey)
    }
    // 레트로핏 인스턴스를 생성하고, 레트로핏에 각종 설정값들을 지정해줍니다.
    // 연결 타임아웃시간은 5초로 지정이 되어있고, HttpLoggingInterceptor를 붙여서 어떤 요청이 나가고 들어오는지를 보여줍니다.
    private fun initRetrofitInstance() {
        val client: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(10000, TimeUnit.MILLISECONDS)
            .connectTimeout(10000, TimeUnit.MILLISECONDS)
            // 로그캣에 okhttp.OkHttpClient로 검색하면 http 통신 내용을 보여줍니다.
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(AuthInterceptor()) // JWT 자동 헤더 전송
            .build()

        // sRetrofit 이라는 전역변수에 API url, 인터셉터, Gson을 넣어주고 빌드해주는 코드
        // 이 전역변수로 http 요청을 서버로 보내면 됩니다.
        sRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}