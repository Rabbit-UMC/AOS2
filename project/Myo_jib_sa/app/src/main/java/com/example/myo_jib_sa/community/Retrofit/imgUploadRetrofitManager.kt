package com.example.myo_jib_sa.community.Retrofit

import android.content.Context
import android.util.Log
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import java.io.File

class imgUploadRetrofitManager(context: Context) {

        private val retrofit: ImgUploadRetrofitITFC? = RetrofitClient.getClient(Constance.BASEURL)?.create(
            ImgUploadRetrofitITFC::class.java
        )

        // 이미지 업로드 메서드 추가
        fun uploadImage(fileList: List<File>, path:String,completion: (ImageUploadResponse?) -> Unit) {
            val imgList:MutableList<MultipartBody.Part> = mutableListOf()

            for(i in 1..fileList.size){
                val requestFile = RequestBody.create(MediaType.parse("image/*"), fileList[i-1])
                val imagePart = MultipartBody.Part.createFormData("image", fileList[i-1].name, requestFile)
                imgList.add(imagePart)
            }

            val call = retrofit?.uploadImage(imgList, path)
            call?.enqueue(object : Callback<ImageUploadResponse> {
                override fun onResponse(call: Call<ImageUploadResponse>, response: Response<ImageUploadResponse>) {
                    if (response.isSuccessful) {
                        val imageUploadResponse = response.body()
                        Log.d("이미지 업로드 결과 RetrofitManager", "RetrofitManager 이미지 업로드 성공 ${response.code()}")
                        Log.d("이미지 업로드 결과 RetrofitManager", "RetrofitManager 이미지 업로드 성공 ${response.message()}")

                        completion(imageUploadResponse)
                    } else {
                        Log.d("이미지 업로드 결과 RetrofitManager", "RetrofitManager 이미지 업로드 성공 ${response.code()}")
                        Log.d("이미지 업로드 결과 RetrofitManager", "RetrofitManager 이미지 업로드 성공 ${response.message()}")

                        Log.d("이미지 업로드 결과 RetrofitManager", "RetrofitManager 이미지 업로드 null")
                        completion(null)
                    }
                }

                override fun onFailure(call: Call<ImageUploadResponse>, t: Throwable) {
                    completion(null)
                    Log.d("이미지 업로드 결과 RetrofitManager", "RetrofitManager 이미지 업로드 onFailure \t :$t ")
                }
            })
        }
}