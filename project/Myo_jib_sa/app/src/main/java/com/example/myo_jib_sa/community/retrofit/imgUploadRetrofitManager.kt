package com.example.myo_jib_sa.community.retrofit

import android.content.Context
import android.util.Log
import com.example.myo_jib_sa.community.Constance
import com.example.myo_jib_sa.community.retrofit.missionCert.MissionCertRetrofitManager
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class imgUploadRetrofitManager(context: Context) {

        private val retrofit: ImgUploadRetrofitITFC? = RetrofitClient.getClient(Constance.BASEURL)?.create(
            ImgUploadRetrofitITFC::class.java
        )

    companion object {
        private var instance: MissionCertRetrofitManager? = null

        // 싱글톤 인스턴스를 가져오는 메서드
        fun getInstance(context: Context): MissionCertRetrofitManager {
            if (instance == null) {
                instance = MissionCertRetrofitManager(context)
            }
            return instance as MissionCertRetrofitManager
        }

    }


        // 이미지 업로드 메서드 추가
        fun uploadImage(fileList: List<File>, path:String,completion: (ImageUploadResponse?) -> Unit) {


            val imgList:MutableList<MultipartBody.Part> = mutableListOf()
            for(i in 1..fileList.size){
                val requestFile = RequestBody.create(MediaType.parse("image/*"), fileList[i-1])
                val imagePart = MultipartBody.Part.createFormData("file", fileList[i-1].name, requestFile)
                imgList.add(imagePart)
            }

            val call: Call<ImageUploadResponse> = retrofit?.uploadImage(imgList, path) ?: return

            call.enqueue(object : retrofit2.Callback<ImageUploadResponse> {
                override fun onResponse(
                    call: Call<ImageUploadResponse>,
                    response: Response<ImageUploadResponse>
                ) {
                    Log.d("이미지 업로드 결과 RetrofitManager", "RetrofitManager 미션 화면 조회 onResponse \t :${response.message()} ")
                    Log.d("이미지 업로드 결과 RetrofitManager", "RetrofitManager 미션 화면 조회 onResponse \t :${response.code()} ")
                    val response: ImageUploadResponse? = response?.body()
                    if (response != null) {
                        if (response.isSuccess=="true") {
                            Log.d("이미지 업로드 결과 RetrofitManager",
                                "RetrofitManager 미션 화면 조회 is Success\t :${response.code} ")
                            Log.d("이미지 업로드 결과 RetrofitManager",
                                "RetrofitManager 미션 화면 조회 is Success\t :${response.message} ")
                            completion(response)
                        } else {
                            Log.d("이미지 업로드 결과 RetrofitManager",
                                "RetrofitManager 미션 화면 조회 is NOT Success\t :${response.code} ")
                            Log.d("이미지 업로드 결과 RetrofitManager",
                                "RetrofitManager 미션 화면 조회 is NOT Success\t :${response.message} ")
                            completion(response)
                        }
                    } else {
                        Log.d("이미지 업로드 결과 RetrofitManager", "RetrofitManager 미션 화면 조회 null")
                    }
                }
                override fun onFailure(call: Call<ImageUploadResponse>, t: Throwable) {
                    Log.d("이미지 업로드 결과 RetrofitManager", "RetrofitManager 미션 화면 조회 onFailure \t :$t ")
                }
            })



        }
}