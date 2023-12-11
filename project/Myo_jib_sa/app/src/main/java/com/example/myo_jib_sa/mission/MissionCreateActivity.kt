package com.example.myo_jib_sa.mission

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.myo_jib_sa.Login.API.RetrofitInstance
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.sRetrofit
import com.example.myo_jib_sa.databinding.ActivityMissionCreateBinding
import com.example.myo_jib_sa.mission.api.MissionCategoryListResponse
import com.example.myo_jib_sa.mission.api.MissionCategoryListResult
import com.example.myo_jib_sa.mission.api.MissionAPI
import com.example.myo_jib_sa.mission.api.MissionCreateRequest
import com.example.myo_jib_sa.mission.api.MissionCreateResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MissionCreateActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMissionCreateBinding

    private lateinit var referenceDate : LocalDate //오늘 날짜
    private lateinit var selectedDate : LocalDate //종료 날짜

    private lateinit var startSelectedDate : LocalDate //시작 날짜
    private lateinit var endSelectedDate : LocalDate //종료 날짜

    private var isCategorySelected = false
    private var isStartDateSelected = false
    private var isEndDateSelected = false
    private var isMissionTitleInputted = false
    private var isMissionMemoInputted = false

    companion object {
        lateinit var missionRequest: MissionCreateRequest
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMissionCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //오늘 날짜
        referenceDate = LocalDate.now()
        selectedDate = referenceDate

        startSelectedDate = referenceDate
        endSelectedDate = referenceDate

        setInputListener()
        // 카테고리 리스트 조회 api 호출
        getMissionCategoryListApi()

        binding.missionCreateCreateBtn.setOnClickListener {
            postMissionCreate()
        }
    }
    // 미션 카테고리 리스트 조회
    private fun getMissionCategoryListApi(){
        sRetrofit.create(MissionAPI::class.java).getCategoryList().enqueue(object : Callback<MissionCategoryListResponse> {
            override fun onResponse(call: Call<MissionCategoryListResponse>, response: Response<MissionCategoryListResponse>) {
                if (response.isSuccessful) {
                    val categoryList = response.body()?.result ?: emptyList()
                    Log.d("getMissionCategoryListApi",response.body().toString())
                    // 카테고리 리스트로 탭 레이아웃 구성
                    setRadioGroup(categoryList)
                } else {
                    // API 요청 실패 처리
                }
            }

            override fun onFailure(call: Call<MissionCategoryListResponse>, t: Throwable) {
                // 네트워크 등의 문제로 API 요청이 실패한 경우 처리
            }
        })
    }
    private fun setRadioGroup(categoryList: List<MissionCategoryListResult>) {
        val buttonMargin = resources.getDimensionPixelSize(R.dimen.mission_create_radio_button_margin)
        val buttonWidth = (binding.missionCreateCategoryRadioGroup.width - 2 * buttonMargin) / 3 // 4 * dp7은 간격의 합

        categoryList.forEachIndexed { index, category ->
            val radioButton = createCategoryRadioButton(this, category)
            val params = RadioGroup.LayoutParams(buttonWidth, RadioGroup.LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = buttonMargin
                marginEnd = if(index % 3 == 2) 0 else buttonMargin
            }
            radioButton.layoutParams = params

            binding.missionCreateCategoryRadioGroup.addView(radioButton)
        }
    }

    private fun createCategoryRadioButton(context: Context, category: MissionCategoryListResult): RadioButton {
        return RadioButton(context).apply {
            text = category.title
            val textSizeInSp = 10.0f // 텍스트 크기를 10sp로 설정
            val scaledPixels = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                textSizeInSp,
                resources.displayMetrics
            )
            setTextSize(TypedValue.COMPLEX_UNIT_PX, scaledPixels)
            gravity = Gravity.CENTER
            id = category.id.toInt()
            background = context.getDrawable(R.drawable.selector_mission_create_category)
            buttonDrawable = null
            typeface = ResourcesCompat.getFont(context, R.font.font_caption_text_4)

            setOnCheckedChangeListener { _, isChecked ->
                setTextColor(ContextCompat.getColor(context, if (isChecked) R.color.white else R.color.gray7))
                isCategorySelected = true
                setCreateButtonIsEnabled()
            }
        }
    }

    //미션 생성 API 연결
    private fun postMissionCreate() {
        // Retrofit을 사용한 API 호출
        sRetrofit.create(MissionAPI::class.java).postMissionCreate(missionRequest).enqueue(object : Callback<MissionCreateResponse> {
            override fun onResponse(call: Call<MissionCreateResponse>, response: Response<MissionCreateResponse>) {
                val writeResponse = response.body()
                if (response.isSuccessful) {
                    if (writeResponse != null) {
                        // 응답 데이터 처리
                        Toast.makeText(this@MissionCreateActivity, writeResponse.errorMessage, Toast.LENGTH_SHORT).show()

                        Log.d("postMissionCreate","${writeResponse.errorMessage}\n${writeResponse.errorCode}")
                    }
                } else {
                    // 에러 처리
                    if (writeResponse != null) {
                        Toast.makeText(this@MissionCreateActivity, writeResponse.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<MissionCreateResponse>, t: Throwable) {
                Toast.makeText(this@MissionCreateActivity, "네트워크 요청 실패!", Toast.LENGTH_SHORT).show()

            }
        })
    }

    // 입력 받는 값들(EditText, Date 등) 리스너
    private fun setInputListener(){
        with(binding) {
            missionCreateTitleEt.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    isMissionTitleInputted = missionCreateTitleEt.text.isNotEmpty()
                    setCreateButtonIsEnabled()
                }
            })
            missionCreateMemoEt.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    missionCreateMemoCntTxt.text = missionCreateMemoEt.text.length.toString()
                    isMissionMemoInputted = missionCreateMemoEt.text.isNotEmpty()
                    setCreateButtonIsEnabled()
                }
            })
        }
    }
    // 플래그들 값에 따라 Create 버튼 enabled 값 설정
    private fun setCreateButtonIsEnabled() {
        binding.missionCreateCreateBtn.isEnabled =
            isCategorySelected && isMissionTitleInputted && isMissionMemoInputted
                    //&& isStartDateSelected && isEndDateSelected
    }


    //M월 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    private fun monthFromDate(date : LocalDate):String{
        var formatter = DateTimeFormatter.ofPattern("M월")
        return date.format(formatter)
    }

    //YYYY년 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    private fun yearFromDate(date : LocalDate):String{
        var formatter = DateTimeFormatter.ofPattern("YYYY년")
        return date.format(formatter)
    }
}