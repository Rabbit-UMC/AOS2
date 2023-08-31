package com.example.myo_jib_sa.mypage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.myo_jib_sa.Login.API.RetrofitInstance
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.missionCert.MissionCertificationWriteActivity
import com.example.myo_jib_sa.databinding.ActivityEditMypageBinding
import com.example.myo_jib_sa.mypage.API.*
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditMypageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditMypageBinding

    private var imgUri: Uri = Uri.EMPTY
    private var imgPath: String = ""

    private  var userName:String=""

    val retrofit = RetrofitInstance.getInstance().create(MyPageITFC::class.java)

    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("kakao", "사용자 정보 요청 실패", error)
            } else if (user != null) {
                var kakaoEmail = user.kakaoAccount?.email
                Log.d("kakaoM", kakaoEmail.toString())
                binding.txtMyPageEmail.text = kakaoEmail
            }
        }

        //userName=binding.myPageInputNinkName.text.toString()

        binding.myPageProfileImgBtn.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
        }

        binding.myPageCompleteBtn.setOnClickListener {
            userName=binding.myPageInputNinkName.text.toString()
            ImageAPI()
            NameAPI()

            finish()
        }
        binding.mypageBackBtn.setOnClickListener {
            finish()
        }

    }

    //fun getRealPathFromURI() 이미지 url을 실제 파일 경로로 변환
    private fun getRealPathFromURI(uri: Uri?): String? {
        if (uri == null) return null

        var realPath: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = this.contentResolver.query(uri, projection, null, null, null)
        cursor?.let {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                realPath = it.getString(columnIndex)
            }
            it.close()
        }
        return realPath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MissionCertificationWriteActivity.GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            // 선택한 이미지를 해당 이미지뷰에 표시
            selectedImageUri?.let { uri ->
                val imageView: ImageView = binding.myPageProfileImgBtn
                imageView.setImageURI(uri)
                imgUri = uri

                imgPath = getRealPathFromURI(imgUri).toString()
                Log.d("img","img uri"+imgUri)
                Log.d("img",imgPath)

                binding.myPageProfileImgBtn.clipToOutline = true //둥근 모서리

                /*   binding.missionWriteImgLayout.backgroundTintList=
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
            }*/
            }
        }
    }

    //닉네임
    fun ImageAPI(){
        val sharedPreferences = getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        val jwt = sharedPreferences.getString("jwt", null)
        val userId = sharedPreferences.getLong("userId", 0L)

        if (jwt != null) {
            retrofit.putUserImage(jwt,userName).enqueue(object : Callback<putUserImageResponse> {
                override fun onResponse(call: Call<putUserImageResponse>, response: Response<putUserImageResponse>) {
                    if (response.isSuccessful) {
                        val imageResponse = response.body()
                        val dataList = imageResponse?.result

                        if (imageResponse != null) {

                        }

                    } else {
                        // API 요청 실패 처리
                        Toast.makeText(this@EditMypageActivity, "API 요청 실패 처리", Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<putUserImageResponse>, t: Throwable) {
                    // 네트워크 등의 문제로 API 요청이 실패한 경우 처리
                    Toast.makeText(this@EditMypageActivity, "서버 오류 발생", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun NameAPI(){
        val sharedPreferences = getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        val jwt = sharedPreferences.getString("jwt", null)
        val userId = sharedPreferences.getLong("userId", 0L)

        if (jwt != null) {
            retrofit.putUserName(jwt,imgPath).enqueue(object : Callback<putUserNameResponse> {
                override fun onResponse(call: Call<putUserNameResponse>, response: Response<putUserNameResponse>) {
                    if (response.isSuccessful) {
                        val nameResponse = response.body()
                        val dataList = nameResponse?.result

                        if (nameResponse != null) {

                        }

                    } else {
                        // API 요청 실패 처리
                        Toast.makeText(this@EditMypageActivity, "API 요청 실패 처리", Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<putUserNameResponse>, t: Throwable) {
                    // 네트워크 등의 문제로 API 요청이 실패한 경우 처리
                    Toast.makeText(this@EditMypageActivity, "서버 오류 발생", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}