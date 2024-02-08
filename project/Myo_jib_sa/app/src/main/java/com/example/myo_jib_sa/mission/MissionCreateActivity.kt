package com.example.myo_jib_sa.mission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import androidx.fragment.app.DialogFragment
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.sRetrofit
import com.example.myo_jib_sa.databinding.ActivityMissionCreateBinding
import com.example.myo_jib_sa.databinding.ToastMissionCreateBinding
import com.example.myo_jib_sa.mission.api.MissionAPI
import com.example.myo_jib_sa.mission.api.MissionCategoryListResponse
import com.example.myo_jib_sa.mission.api.MissionCategoryListResult
import com.example.myo_jib_sa.mission.api.MissionCreateRequests
import com.example.myo_jib_sa.mission.api.MissionCreateResponse
import com.example.myo_jib_sa.mission.dialog.MissionCreateCalendarDialogFragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MissionCreateActivity : AppCompatActivity(), MissionCreateCalendarDialogFragment.OnDateSelectedListener {
    private lateinit var binding:ActivityMissionCreateBinding

    private lateinit var referenceDate : LocalDate //오늘 날짜

    private lateinit var startSelectedDate : LocalDate //시작 날짜
    private lateinit var endSelectedDate : LocalDate //종료 날짜

    private var isCategorySelected = false
    private var isMissionTitleInputted = false
    private var isMissionMemoInputted = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMissionCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //오늘 날짜
        referenceDate = LocalDate.now()

        startSelectedDate = referenceDate
        endSelectedDate = referenceDate

        binding.missionCreateStartDateBtnTxt.text = fromDateYYYYMMDD(startSelectedDate)
        binding.missionCreateEndDateBtnTxt.text = fromDateYYYYMMDD(endSelectedDate)

        // 카테고리 리스트 조회 api 호출
        getMissionCategoryListApi()

        initListener()

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

    // 카테고리 라디오 그룹 간격 설정
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

    // 카테고리 라디오 그룹 버튼 생성
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
            setTextColor(ContextCompat.getColor(context, R.color.gray7))
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
        var missionRequest : MissionCreateRequests
        // Retrofit을 사용한 API 호출
        with(binding) {
            missionRequest = MissionCreateRequests(
                title = missionCreateTitleEt.text.toString(),
                startAt = missionCreateStartDateBtnTxt.text.toString(),
                endAt = missionCreateEndDateBtnTxt.text.toString(),
                categoryId = missionCreateCategoryRadioGroup.checkedRadioButtonId.toLong(),
                isOpen = if(missionCreateOpenSwitch.isChecked) 0 else 1,
                content = missionCreateMemoEt.text.toString(),
                status = "ACTIVE"
            )
            Log.d("postMissionCreate", "missionRequest : $missionRequest")
        }

        sRetrofit.create(MissionAPI::class.java).postMissionCreate(missionRequest).enqueue(object : Callback<MissionCreateResponse> {
            override fun onResponse(call: Call<MissionCreateResponse>, response: Response<MissionCreateResponse>) {
                val writeResponse = response.body()
                if (writeResponse != null) {
                    if(writeResponse.isSuccess){
                        setResult(
                            Activity.RESULT_OK,
                            Intent().putExtra("resultMessage", "미션 생성 성공!")
                            .putExtra("isSuccess", true)
                        )
                        finish()
                    }
                    else {
                        if(writeResponse.errorCode == "MISSION4013" || writeResponse.errorCode == "MISSION4005")
                            showSnackbar(writeResponse.errorMessage)
                        else showSnackbar()
                    }

                }
            }
            override fun onFailure(call: Call<MissionCreateResponse>, t: Throwable) {
                showSnackbar("네트워크 요청 실패")
            }
        })

    }

    // 리스너 초기화
    private fun initListener(){
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

            // 시작일
            missionCreateStartDateBtnTxt.setOnClickListener {
                showCalendarDialog(true, startSelectedDate)
            }

            // 종료일
            missionCreateEndDateBtnTxt.setOnClickListener {
                showCalendarDialog(false, endSelectedDate)
            }

            // 완료 버튼
            missionCreateCompleteBtn.setOnClickListener {
                postMissionCreate()
            }

            // 뒤로가기 버튼
            missionBackBtn.setOnClickListener {
                finish()
            }
        }
    }

    // Flag 값에 따라 Create 버튼 enabled 값 설정
    private fun setCreateButtonIsEnabled() {
        binding.missionCreateCompleteBtn.isEnabled =
            isCategorySelected && isMissionTitleInputted && isMissionMemoInputted
    }

    private fun showCalendarDialog(isStartDate: Boolean, date: LocalDate) {
        val calendarDialog = MissionCreateCalendarDialogFragment(isStartDate, date). apply {
            setStyle(DialogFragment.STYLE_NORMAL, R.style.roundCornerBottomSheetDialogTheme)
            setDateSelectedListener(this@MissionCreateActivity)
        }
        calendarDialog.show(supportFragmentManager, "MissionCreateCalendarDialogFragment")
    }

    private fun showSnackbar(message: String = "오류가 발생했습니다. 다시 시도해주세요.") {
        val snackbarBinding = ToastMissionCreateBinding.inflate(layoutInflater)
        snackbarBinding.toastMissionReportTxt.text = message
        snackbarBinding.toastMissionReportIv.setImageResource(R.drawable.ic_toast_fail)

        val snackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_SHORT).apply {
            animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
            (view as Snackbar.SnackbarLayout).apply {
                setBackgroundColor(Color.TRANSPARENT)
                addView(snackbarBinding.root)
                translationY = -70.dpToPx().toFloat()
                elevation = 0f
            }
        }

        snackbar.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartDateSelected(date: LocalDate) {
        startSelectedDate = date
        binding.missionCreateStartDateBtnTxt.text = fromDateYYYYMMDD(date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onEndDateSelected(date: LocalDate) {
        endSelectedDate = date
        binding.missionCreateEndDateBtnTxt.text = fromDateYYYYMMDD(date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fromDateYYYYMMDD(date: LocalDate?): String? {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date?.format(formatter)
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}