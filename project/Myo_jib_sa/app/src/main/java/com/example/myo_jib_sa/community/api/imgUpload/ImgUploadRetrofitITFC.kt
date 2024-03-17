package com.example.myo_jib_sa.community.api.imgUpload

import com.example.myo_jib_sa.base.BaseResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ImgUploadRetrofitITFC{

    @Multipart
    @POST("app/file")
    fun uploadImage(@Part file: List<MultipartBody.Part>
                    ,@Query("path") path:String ): Call<ImageUploadResponse>

}
data class ImageUploadResponse(
    val result:List<GenerateImageResDto>
):BaseResponse()
data class GenerateImageResDto(
    val imageId:Long,
    val imageUrl:String
)