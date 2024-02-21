package com.example.myo_jib_sa.community.manager

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.base.BaseResponse
import com.example.myo_jib_sa.base.MyojibsaApplication
import com.example.myo_jib_sa.community.Constance
import com.example.myo_jib_sa.community.api.manager.ManagerRetrofitITFC
import com.example.myo_jib_sa.community.api.manager.MissionCreateRequest
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

class ManagerMissionCreateActivity2 : AppCompatActivity(), MissionCreateCalendarDialogFragment.OnDateSelectedListener {
    private lateinit var binding: ActivityMissionCreateBinding

    private lateinit var referenceDate : LocalDate //오늘 날짜
    private lateinit var selectedDate : LocalDate //종료 날짜

    private lateinit var startSelectedDate : LocalDate //시작 날짜
    private lateinit var endSelectedDate : LocalDate //종료 날짜

    private var isStartDateSelected = false
    private var isEndDateSelected = false
    private var isMissionTitleInputted = false
    private var isMissionMemoInputted = false

    private var boardId:Long=0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMissionCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //관리자 전용 뷰 세팅
        setView()

        //오늘 날짜
        referenceDate = LocalDate.now()
        selectedDate = referenceDate

        startSelectedDate = referenceDate
        endSelectedDate = referenceDate

        binding.missionCreateStartDateBtnTxt.text = fromDateYYYYMMDD(startSelectedDate)
        binding.missionCreateEndDateBtnTxt.text = fromDateYYYYMMDD(endSelectedDate)

        boardId=intent.getLongExtra("boardId", 0)

        initListener()

    }

    //관리자 전용 뷰 세팅
    private fun setView(){
        binding.missionCreateCategoryRadioGroup.visibility= View.GONE
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.missionCreateInfoCl)

        constraintSet.connect(
            binding.missionCreateTitleEt.id,
            ConstraintSet.TOP,
            com.example.myo_jib_sa.R.id.mission_create_info_desc_txt,
            ConstraintSet.BOTTOM,
            20
        )

        constraintSet.applyTo(binding.missionCreateInfoCl)

        binding.titleTv.text="미션 공양"
        binding.missionCreateInfoDescTxt.text="달토끼에게 공양하는 미션입니다. 신중히 적어주세요."
        binding.missionCreateMemoOpenTxt.text="인수인계 활성화"

    }


    //미션 생성 API 연결
    private fun postMissionCreate() {
        var missionRequest : MissionCreateRequest
        // Retrofit을 사용한 API 호출
        with(binding) {
            missionRequest = MissionCreateRequest(
                mainMissionTitle = missionCreateTitleEt.text.toString(),
                missionStartTime = missionCreateStartDateBtnTxt.text.toString(),
                missionEndTime = missionCreateEndDateBtnTxt.text.toString(),
                lastMission = missionCreateOpenSwitch.isChecked,
                mainMissionContent = missionCreateMemoEt.text.toString()
            )
            Log.d("postMissionCreate", "missionRequest : $missionRequest")
        }


            MyojibsaApplication.sRetrofit.create(ManagerRetrofitITFC::class.java).missionCreate(boardId,missionRequest).enqueue(object :
                Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                    val writeResponse = response.body()
                    if (writeResponse != null) {
                        if(writeResponse.isSuccess){
                            showSnackbar(writeResponse.errorMessage)
                            finish()
                        } else {
                            showSnackbar(writeResponse.errorMessage)
                        }

                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
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
            isMissionTitleInputted && isMissionMemoInputted
    }

    private fun showCalendarDialog(isStartDate: Boolean, date: LocalDate) {
        val calendarDialog = MissionCreateCalendarDialogFragment(isStartDate, date)
        calendarDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.roundCornerBottomSheetDialogTheme)
        calendarDialog.setDateSelectedListener(this)
        calendarDialog.show(supportFragmentManager, "MissionCreateCalendarDialogFragment")
    }

    private fun showSnackbar(message: String) {
        val snackbarBinding = ToastMissionCreateBinding.inflate(layoutInflater)
        snackbarBinding.toastMissionReportTxt.text = message

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