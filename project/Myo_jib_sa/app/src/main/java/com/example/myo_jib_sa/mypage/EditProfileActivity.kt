package com.example.myo_jib_sa.mypage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.sRetrofit
import com.example.myo_jib_sa.community.missionCert.MissionCertificationWriteActivity
import com.example.myo_jib_sa.databinding.ActivityEditProfileBinding
import com.example.myo_jib_sa.mypage.API.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding

    private var imgUri: Uri = Uri.EMPTY
    private var imgPath: String = ""

    private  var userName: String = ""

    private var returnCode: Int? = null

    val retrofit: UserAPI = sRetrofit.create(UserAPI::class.java)

    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getUserProfileInfo()
        initListener()
    }

    private fun getUserProfileInfo() {
        binding.editProfileNicknameEt.hint = intent.getStringExtra("nickname")
        Glide.with(this@EditProfileActivity)
            .load(intent.getStringExtra("uri"))
            .error(R.drawable.ic_profile)
            .into(binding.editProfileImgBtn)
    }
    private fun initListener() {
        with(binding) {
            editProfileNicknameEt.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    editProfileDuplicateBtn.isEnabled = editProfileNicknameEt.text.isNotEmpty()
                }
            })

            editProfileDuplicateBtn.setOnClickListener {
                checkNicknameDuplicate(editProfileNicknameEt.text.toString())
            }

            editProfileImgBtn.setOnClickListener {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
            }

            editProfileCompleteBtn.setOnClickListener {
            }
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
                val imageView: ImageView = binding.editProfileImgBtn
                imageView.setImageURI(uri)
                imgUri = uri

                imgPath = getRealPathFromURI(imgUri).toString()
                Log.d("img","img uri"+imgUri)
                Log.d("img",imgPath)

                /*   binding.missionWriteImgLayout.backgroundTintList=
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
            }*/
            }
        }
    }

    //닉네임 중복 검사
    private fun checkNicknameDuplicate(nickName: String) {
        retrofit.getCheckDuplication(nickName).enqueue(object : Callback<GetCheckDuplicationResponse> {
            override fun onResponse(call: Call<GetCheckDuplicationResponse>, response: Response<GetCheckDuplicationResponse>) {
                if (response.body() != null) {
                    binding.editProfileDuplicateStateTxt.visibility = View.VISIBLE
                    if(response.body()!!.result) {
                        binding.editProfileDuplicateStateTxt.apply {
                            text = "사용 가능한 닉네임이에요."
                            setTextColor(getColor(R.color.complete))
                        }
                    }
                    else {
                        binding.editProfileDuplicateStateTxt.apply {
                            text = "사용 불가능한 닉네임이에요."
                            setTextColor(getColor(R.color.alert))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GetCheckDuplicationResponse>, t: Throwable) {
                // 네트워크 요청 실패 처리
                Log.d("checkNicknameDuplicate", "$t")
            }
        })
    }

    /*fun ProfileAPI(){
        val sharedPreferences = getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        val jwt = sharedPreferences.getString("jwt", null)
        val userId = sharedPreferences.getLong("userId", 0L)

        if (jwt != null) {
            retrofit.getUserProfile(jwt).enqueue(object : Callback<getUserProfileResponse> {
                override fun onResponse(call: Call<getUserProfileResponse>, response: Response<getUserProfileResponse>) {
                    if (response.isSuccessful) {
                        val profileResponse = response.body()
                        val profileData = profileResponse?.result

                        if (profileData != null) {
                           // val userProfileImageUri = Uri.parse(profileData.userProfileImage)
                            binding.myPageInputNinkName.hint = profileData.userName

                            Log.d("img","edit"+profileData.userProfileImage)

                            // Glide를 사용하여 이미지 설정
                           *//* Glide.with(binding.root.context)
                                .load(profileData.userProfileImage)
                                .error(R.drawable.ic_mypage_profile)
                                .into(binding.myPageProfileImgBtn)*//*

                        }

                    } else {
                        // API 요청 실패 처리
                        Toast.makeText(this@EditMypageActivity, "API 요청 실패 처리", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<getUserProfileResponse>, t: Throwable) {
                    // 네트워크 등의 문제로 API 요청이 실패한 경우 처리
                    Toast.makeText(this@EditMypageActivity, "서버 오류 발생", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }*/



    //이미지 올리기
    fun ImageAPI(){
        val sharedPreferences = getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        val jwt = sharedPreferences.getString("jwt", null)
        val userId = sharedPreferences.getLong("userId", 0L)

        Log.d("img","edit"+imgPath)

        if (jwt != null) {
            retrofit.putUserImage(imgPath).enqueue(object : Callback<PutUserImageResponse> {
                override fun onResponse(call: Call<PutUserImageResponse>, response: Response<PutUserImageResponse>) {
                    if (response.isSuccessful) {
                        val imageResponse = response.body()
                        val dataList = imageResponse?.result

                        if (imageResponse != null) {

                        }

                    } else {
                        // API 요청 실패 처리
                        Toast.makeText(this@EditProfileActivity, "API 요청 실패 처리", Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<PutUserImageResponse>, t: Throwable) {
                    // 네트워크 등의 문제로 API 요청이 실패한 경우 처리
                    Toast.makeText(this@EditProfileActivity, "서버 오류 발생", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

}