package com.example.myo_jib_sa.community.Retrofit

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImgUploadRetrofitITFC{
    @Multipart
    @POST("upload/image")
    fun uploadImage(@Part image: MultipartBody.Part): Call<ImageUploadResponse>

}
data class ImageUploadResponse(
    val imageUrl: String,
    val success: Boolean,
    val message: String?
)