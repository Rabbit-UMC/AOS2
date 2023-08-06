package com.example.myo_jib_sa.community.Retrofit

import android.content.Context
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class imgUploadRetrofitManager(context: Context) {


        private val retrofit: ImgUploadRetrofitITFC? = RetrofitClient.getClient(Constance.BASEURL)?.create(
            ImgUploadRetrofitITFC::class.java
        )

        // 이미지 업로드 메서드 추가
        fun uploadImage(imageFile: File, completion: (ImageUploadResponse?) -> Unit) {
            val requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile)
            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

            val call = retrofit?.uploadImage(imagePart)
            call?.enqueue(object : Callback<ImageUploadResponse> {
                override fun onResponse(call: Call<ImageUploadResponse>, response: Response<ImageUploadResponse>) {
                    if (response.isSuccessful) {
                        val imageUploadResponse = response.body()
                        completion(imageUploadResponse)
                    } else {
                        completion(null)
                    }
                }

                override fun onFailure(call: Call<ImageUploadResponse>, t: Throwable) {
                    completion(null)
                }
            })
        }
}