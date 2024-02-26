package com.example.myo_jib_sa.mypage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.sRetrofit
import com.example.myo_jib_sa.community.missionCert.MissionCertificationWriteActivity
import com.example.myo_jib_sa.databinding.ActivityEditProfileBinding
import com.example.myo_jib_sa.databinding.ToastMissionBinding
import com.example.myo_jib_sa.mypage.api.GetCheckDuplicationResponse
import com.example.myo_jib_sa.mypage.api.GetUserProfileResponse
import com.example.myo_jib_sa.mypage.api.MypageAPI
import com.example.myo_jib_sa.mypage.api.PatchProfileResponse
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding

    private var imgUri: String? = null
    private var userName: String? = null
    private var previousName: String? = null

    val retrofit: MypageAPI = sRetrofit.create(MypageAPI::class.java)

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

    // 유저 정보 가져오기
    private fun getUserProfileInfo() {
        retrofit.getUserProfile().enqueue(object : Callback<GetUserProfileResponse> {
            override fun onResponse(
                call: Call<GetUserProfileResponse>,
                response: Response<GetUserProfileResponse>
            ) {
                if (response.isSuccessful) {
                    val profileData = response.body()?.result
                    if (profileData != null) {
                        Log.d("img", profileData.userProfileImage)
                        Glide.with(this@EditProfileActivity)
                            .load(profileData.userProfileImage)
                            .error(R.drawable.ic_profile)
                            .into(binding.editProfileImgBtn)
                        previousName = profileData.userName
                        binding.editProfileNicknameEt.hint = previousName
                    }
                } else {
                    // API 요청 실패 처리
                    Toast.makeText(this@EditProfileActivity, "API 요청 실패 처리", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GetUserProfileResponse>, t: Throwable) {
                // 네트워크 등의 문제로 API 요청이 실패한 경우 처리
                Toast.makeText(this@EditProfileActivity, "서버 오류 발생", Toast.LENGTH_SHORT).show()
            }
        })

    }
    private fun initListener() {
        with(binding) {
            editProfileNicknameEt.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    editProfileDuplicateBtn.isEnabled = isValidNickname(editProfileNicknameEt.text.toString())
                    editProfileDuplicateStateTxt.visibility = View.GONE
                    editProfileCompleteBtn.isEnabled = false
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
                patchProfile()
            }

            editProfileBackBtn.setOnClickListener {
                finish()
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
                imgUri = getRealPathFromURI(uri).toString()

                Log.d("img","img uri $imgUri")
            }
        }
    }

    //닉네임 중복 검사
    private fun checkNicknameDuplicate(nickName: String) {
        retrofit.getCheckDuplication(nickName, true).enqueue(object : Callback<GetCheckDuplicationResponse> {
            override fun onResponse(call: Call<GetCheckDuplicationResponse>, response: Response<GetCheckDuplicationResponse>) {
                if (response.body() != null) {
                    binding.editProfileDuplicateStateTxt.visibility = View.VISIBLE
                    if(!response.body()!!.result) {
                        binding.editProfileDuplicateStateTxt.apply {
                            text = "사용 가능한 닉네임이에요."
                            setTextColor(getColor(R.color.complete))
                            userName = binding.editProfileNicknameEt.text.toString()
                            binding.editProfileCompleteBtn.isEnabled = true
                        }
                    }
                    else {
                        binding.editProfileDuplicateStateTxt.apply {
                            text = "사용 불가능한 닉네임이에요."
                            setTextColor(getColor(R.color.alert))
                            userName = null
                            binding.editProfileCompleteBtn.isEnabled = false
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

    // 프로필 편집
    private fun patchProfile() {
        val file = if(imgUri != null) File(imgUri)
        else { File.createTempFile("empty", null, this.cacheDir).apply { writeText("") } }

        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
        // MultipartBody.Part를 생성
        val body = MultipartBody.Part.createFormData("userProfileImage", file.name, requestFile)
        val nickname = if(userName.isNullOrEmpty()) previousName else userName

        retrofit.patchProfile(body, nickname).enqueue(object : Callback<PatchProfileResponse> {
            override fun onResponse(call: Call<PatchProfileResponse>, response: Response<PatchProfileResponse>) {
                if(response.isSuccessful && response.body()?.isSuccess == true) {
                    setResult(Activity.RESULT_OK, Intent().putExtra("resultMessage", "프로필 편집 사항이 저장되었어요!"))
                    finish()
                } else {
                    showSnackbar("오류가 발생했습니다. 다시 시도해주세요.")
                    Log.d("patchProfile onFailure", "onFailure : ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<PatchProfileResponse>, t: Throwable) {
                showSnackbar("오류가 발생했습니다. 다시 시도해주세요.")
                Log.d("patchProfile onFailure", "onFailure : $t")
            }
        })
    }

    private fun showSnackbar(message : String){
        // 뷰 바인딩을 사용하여 커스텀 레이아웃을 인플레이트합니다.
        val snackbarBinding = ToastMissionBinding.inflate(layoutInflater)
        snackbarBinding.toastMissionReportIv.setImageResource(R.drawable.ic_toast_fail)
        snackbarBinding.toastMissionReportTxt.text = message

        // 스낵바 생성 및 설정
        val snackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_SHORT).apply {
            animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
            (view as Snackbar.SnackbarLayout).apply {
                setBackgroundColor(Color.TRANSPARENT)
                addView(snackbarBinding.root)
                translationY = -70.dpToPx().toFloat()
                elevation = 0f
            }
        }
        // 스낵바 표시
        snackbar.show()
    }
    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()

    private fun isValidNickname(nickName: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9가-힣]{1,6}$")
        return regex.matches(nickName)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

}