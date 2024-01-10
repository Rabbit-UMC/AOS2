package com.example.myo_jib_sa.schedule.createScheduleActivity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.NumberPicker
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.schedule.api.RetrofitClient
import com.example.myo_jib_sa.databinding.ActivityCreateScheduleBinding
import com.example.myo_jib_sa.databinding.ToastCreateScheduleBinding
import com.example.myo_jib_sa.databinding.ToastCurrentMissionDeleteBinding
import com.example.myo_jib_sa.schedule.ScheduleFragment
import com.example.myo_jib_sa.schedule.api.currentMission.CurrentMissionResponse
import com.example.myo_jib_sa.schedule.api.currentMission.CurrentMissionResult
import com.example.myo_jib_sa.schedule.api.currentMission.CurrentMissionService
import com.example.myo_jib_sa.schedule.createScheduleActivity.adapter.CreateScheduleCalendarAdapter
import com.example.myo_jib_sa.schedule.createScheduleActivity.adapter.SelectDateData

import com.example.myo_jib_sa.schedule.createScheduleActivity.api.scheduleAdd.ScheduleAddRequest
import com.example.myo_jib_sa.schedule.createScheduleActivity.api.scheduleAdd.ScheduleAddResponse
import com.example.myo_jib_sa.schedule.createScheduleActivity.api.scheduleAdd.ScheduleAddService
import com.example.myo_jib_sa.schedule.createScheduleActivity.spinner.ScheduleCreateSpinnerDialogFragment
import com.example.myo_jib_sa.schedule.dialog.DialogMissionAdapter
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

class CreateScheduleActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCreateScheduleBinding
    private lateinit var referenceDate : LocalDate //오늘 날짜
    private lateinit var selectedDate : LocalDate //선택한 날짜
    private lateinit var calendarAdapter : CreateScheduleCalendarAdapter //calendarRvItemClickEvent() 함수에 사용하기 위해 전역으로 선언
    private var dayList = ArrayList<SelectDateData>() //calendarAdapter item list

    private var selectedDateIndex : Int = 0//referenceDate의 dayList에서 index값 //달력이미지버튼 클릭에서 사용

    //일정 생성 조건
    private var isMissionSelect = false
    private var isDateSelect = false
    private var isStartSelect = false
    private var isEndSelect = false

    private var scheduleData : ScheduleAddRequest = ScheduleAddRequest(
        missionId = 0,
        scheduleTitle= "",
        startAt= "08:00",
        endAt= "09:00",
        content= "",
        scheduleWhen= ""
    )
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //일정 제목 특수문자 제어
        binding.scheduleTitleEtv.filters = arrayOf(editTextFilter)

        //오늘 날짜
        referenceDate = LocalDate.now()
        selectedDate = referenceDate

        //화면 초기화
        setMonthView()
//        //오늘날짜 편집 화면에 표시
//        binding.scheduleYearTv.text = referenceDate?.year.toString()
//        binding.scheduleMonthTv.text = referenceDate?.monthValue.toString()
//        binding.scheduleDayTv.text = referenceDate?.dayOfMonth.toString()

        setBtn()//버튼 setting
        setMemoCount()//메모 입력 카운트

        binding.calendarRv.addItemDecoration(CreateScheduleCalendarAdapter.GridSpaceDecoration(getDisplayWidthSize()))
        calendarRvItemClickEvent()//캘린더 아이템 클릭이벤트
        currentMissionApi()
    }




    //month화면에 보여주기
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMonthView(){
        //month를 month text view에 보여주기 (결과: 1월)
        binding.monthTv.text = monthFromDate(referenceDate)
        //year를 year text view에 보여주기 (결과: 2023년)
        binding.yearTv.text = yearFromDate(referenceDate)

        //이번달 날짜 가져오기
        dayInMonthArray(referenceDate)

        //리사이클러뷰 연결
        calendarAdapter = CreateScheduleCalendarAdapter(dayList)

        binding.calendarRv.apply{
            layoutManager= GridLayoutManager(this@CreateScheduleActivity, 7)
            adapter = calendarAdapter
        }
    }
    private fun setDialogMissionAdapter(missionList: List<CurrentMissionResult>){
        var dialogMissionAdapter = DialogMissionAdapter(missionList)
        binding.missionListRv.adapter = dialogMissionAdapter
        binding.missionListRv.layoutManager = LinearLayoutManager(this ,RecyclerView.HORIZONTAL, false)

        dialogMissionAdapter.setItemClickListener(object : DialogMissionAdapter.OnItemClickListener{
            override fun onClick(data: CurrentMissionResult) {
                binding.missionTitleTv.text = data.missionTitle
                scheduleData.missionId = data.missionId
                isMissionSelect = true
                createScheduleBtn()
            }
        })
    }

    //날짜 생성
    @RequiresApi(Build.VERSION_CODES.O)
    private fun dayInMonthArray(date: LocalDate){
        dayList = ArrayList<SelectDateData>()
        var yearMonth = YearMonth.from(date)

        //해당 월의 마지막 날짜 가져오기(결과: 1월이면 31)
        var lastDay = yearMonth.lengthOfMonth()
        //해당 월의 첫번째 날 가져오기(결과: 2023-01-01)
        var firstDay = referenceDate.withDayOfMonth(1)
        //첫 번째날 요일 가져오기(결과: 월 ~일이 1~7에 대응되어 나타남)
        var dayOfWeek = firstDay.dayOfWeek.value

        for(i in 1..42){

            if (dayOfWeek == 7) {//그 달의 첫날이 일요일일때 작동: 한칸 아래줄부터 날짜 표시되는 현상 막기위해
                if (i > lastDay) {
                    //break
                    dayList.add(SelectDateData(null))
                } else {
                    if (i == referenceDate.dayOfMonth) {//referenceDate의 dayList에서 index값
                        selectedDateIndex = i - 1
                    }
                    if (selectedDate == LocalDate.of(
                            referenceDate.year, referenceDate.monthValue, i
                        )
                    ) { //현재 선택한 date
                        dayList.add(
                            SelectDateData(
                                LocalDate.of(referenceDate.year, referenceDate.monthValue, i), true))
                    } else {
                        dayList.add(
                            SelectDateData(
                                LocalDate.of(referenceDate.year, referenceDate.monthValue, i)))
                    }
                }
            }
            else if(i<=dayOfWeek || i>(lastDay + dayOfWeek)){//그 외 경우
                dayList.add(SelectDateData(null))
            }
            else{
                if(i-dayOfWeek == referenceDate.dayOfMonth) {//referenceDate의 dayList에서 index값
                    selectedDateIndex = i - 1
                }
                if( selectedDate == LocalDate.of(referenceDate.year, referenceDate.monthValue, i-dayOfWeek) ){ //현재 선택한 date
                    dayList.add(SelectDateData(LocalDate.of(referenceDate.year, referenceDate.monthValue, i-dayOfWeek), true))
                } else{
                    dayList.add(SelectDateData(LocalDate.of(referenceDate.year, referenceDate.monthValue, i-dayOfWeek)))
                }
            }
        }


    }

    //Calendar rv item클릭 이벤트
    private fun calendarRvItemClickEvent() {
        calendarAdapter.setItemClickListener(object : CreateScheduleCalendarAdapter.OnItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(selectDateData: SelectDateData, position: Int) {
                //오늘날짜 파란 동그라미 없애기
                val temp = dayList[selectedDateIndex]
                if(temp.isSelected){//temp.isSelected == true일때만 false로 변경
                    dayList[selectedDateIndex] = SelectDateData(temp.date, false)
                    calendarAdapter.notifyItemChanged(selectedDateIndex)
                }

                selectedDate = selectDateData.date!!

                var iYear = selectDateData.date?.year
                var iMonth = selectDateData.date?.monthValue
                var iDay = selectDateData.date?.dayOfMonth
            }
        })
    }

    fun createScheduleBtn(){
        //생성 조건 만족시
        if(binding.scheduleTitleEtv.text.isNotEmpty() && isMissionSelect && isDateSelect && isStartSelect && isEndSelect && scheduleData.startAt < scheduleData.endAt && binding.scheduleMemoEtv.text.isNotEmpty()){
            binding.createBtn.isEnabled = true
            binding.createBtn.setBackgroundResource(R.drawable.view_round_r8_blue)
        }else{ //만족 못했을 시
            binding.createBtn.isEnabled = false
            binding.createBtn.setBackgroundResource(R.drawable.view_round_r8_gray3)
        }
    }

    //버튼 setting
    @RequiresApi(Build.VERSION_CODES.O)
    fun setBtn() {

        //뒤로가기 버튼 클릭
        binding.goBackBtn.setOnClickListener {
            val intent = Intent(this, ScheduleFragment::class.java)
            intent.putExtra("isCreate", false)
            setResult(RESULT_OK, intent)
            finish()
        }

        binding.scheduleTitleEtv.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                createScheduleBtn()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        //완료 버튼
        binding.createBtn.setOnClickListener {
            if (binding.scheduleTitleEtv.text.isNotEmpty() && isMissionSelect && isDateSelect && isStartSelect && isEndSelect && scheduleData.startAt < scheduleData.endAt && binding.scheduleMemoEtv.text.isNotEmpty()) {//제목이 공백이거나 시간이 잘못되면
                binding.createBtn.isEnabled = true
                binding.createBtn.setBackgroundResource(R.drawable.view_round_r8_blue)
                Log.d("exitDebug", "yes!!")
                scheduleAddApi()
            } else {//정상적일때
                Log.d("exitDebug", "no!!")
                binding.createBtn.isEnabled = false
                binding.createBtn.setBackgroundResource(R.drawable.view_round_r8_gray3)
//                val errorDialogFragment = ErrorDialogFragment()
//                errorDialogFragment.show(supportFragmentManager, "ErrorDialogFragment")
            }
        }

        //캘린더: 이전달로 이동
        binding.preMonthBtn.setOnClickListener {
                referenceDate = referenceDate.minusMonths(1)
                setMonthView()
                calendarRvItemClickEvent()
        }
        //캘린더: 다음달로 이동
        binding.nextMonthBtn.setOnClickListener {
                referenceDate = referenceDate.plusMonths(1)
                setMonthView()
                calendarRvItemClickEvent()
        }

        //날짜 클릭
        binding.scheduleDateTv.setOnClickListener {
            isDateSelect = true
            createScheduleBtn()
            binding.createBtn.visibility = View.INVISIBLE //버튼이 맨앞에 띄어져서 invisible처리

            binding.calendarLayout.visibility = View.VISIBLE
            binding.toneDownView.visibility = View.VISIBLE
        }
        binding.calendarCompleteTv.setOnClickListener {
            binding.createBtn.visibility = View.VISIBLE

            //데이터 넣기
            scheduleData.scheduleWhen = selectedDate.toString()
            //화면에 표시
            binding.scheduleDateTv.text = selectedDate.toString()
            //캘린더 안보이게
            binding.calendarLayout.visibility = View.GONE
            binding.toneDownView.visibility = View.GONE
        }


        val formatter = DecimalFormat("00")
        var startHour: Int? = null
        var startMinute: Int? = null
        var endHour: Int? = null
        var endMinute: Int? = null
        binding.scheduleStartAtEtv.setOnClickListener{
            isStartSelect = true
            createScheduleBtn()
            binding.createBtn.visibility = View.INVISIBLE //버튼이 맨앞에 띄어져서 invisible처리

            //setSpinnerDialog(1)
            binding.startTimeLayout.visibility = View.VISIBLE
            binding.toneDownView.visibility = View.VISIBLE

            //timepicker 초기값 설정
            if(startHour == null) {
                binding.startTimePicker.hour = 9
                binding.startTimePicker.minute = 0
                startHour = 9
                startMinute = 0
            }else{
                binding.startTimePicker.hour = startHour as Int
                binding.startTimePicker.minute = startMinute!!
            }
            binding.startTimePicker.descendantFocusability =
                NumberPicker.FOCUS_BLOCK_DESCENDANTS //editText가 눌리는 것을 막는다
            binding.startTimePicker.setOnTimeChangedListener(object: TimePicker.OnTimeChangedListener {
                override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    Log.d("debug", "startTime : 시:분 | ${formatter.format(hourOfDay)} : ${formatter.format(minute)}")
                    startHour = hourOfDay
                    startMinute = minute
                }
            })
        }
        binding.startTimeCompleteTv.setOnClickListener {
            binding.createBtn.visibility = View.VISIBLE

            binding.startTimeLayout.visibility = View.GONE
            binding.toneDownView.visibility = View.GONE
            scheduleData.startAt =  "${formatter.format(startHour)}:${formatter.format(startMinute)}"
            binding.scheduleStartAtEtv.setText("${formatter.format(startHour)}시 ${formatter.format(startMinute)}분")
        }
        binding.scheduleEndAtEtv.setOnClickListener{
            isEndSelect = true
            createScheduleBtn()
            binding.createBtn.visibility = View.INVISIBLE //버튼이 맨앞에 띄어져서 invisible처리

            //setSpinnerDialog(2)
            binding.endTimeLayout.visibility = View.VISIBLE
            binding.toneDownView.visibility = View.VISIBLE

            //timepicker 초기값 설정
            if(endHour == null) {
                binding.endTimePicker.hour = 10
                binding.endTimePicker.minute = 0
                endHour = 10
                endMinute = 0
            }else{
                binding.endTimePicker.hour = endHour as Int
                binding.endTimePicker.minute = endMinute!!
            }
            binding.endTimePicker.descendantFocusability =
                NumberPicker.FOCUS_BLOCK_DESCENDANTS //editText가 눌리는 것을 막는다
            binding.endTimePicker.setOnTimeChangedListener(object: TimePicker.OnTimeChangedListener {
                override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    Log.d("debug", "endTime : 시:분 | ${formatter.format(hourOfDay)} : ${formatter.format(minute)}")
                    endHour = hourOfDay
                    endMinute = minute
                }
            })
        }
        binding.endTimeCompleteTv.setOnClickListener {
            binding.createBtn.visibility = View.VISIBLE

            binding.endTimeLayout.visibility = View.GONE
            binding.toneDownView.visibility = View.GONE
            scheduleData.endAt = "${formatter.format(endHour)}:${formatter.format(endMinute)}"
            binding.scheduleEndAtEtv.setText("${formatter.format(endHour)}시 ${formatter.format(endMinute)}분")
        }
    }

    private fun setMemoCount(){
        var userInput = binding.scheduleMemoEtv.text.toString()
        binding.countMemoTv.text = userInput.length.toString()
        binding.scheduleMemoEtv.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var userInput = binding.scheduleMemoEtv.text.toString()
                binding.countMemoTv.text = userInput.length.toString()
                Log.d("debug", "memo"+userInput.length.toString())
                createScheduleBtn()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun setSpinnerDialog(position:Int){
        //sharedPreference저장
        val sharedPreference = getSharedPreferences("scheduleData",
            Context.MODE_PRIVATE
        )
        val editor = sharedPreference.edit()
        editor.putString("scheduleTitle", binding.scheduleTitleEtv.text.toString())
        editor.putString("scheduleDate", binding.scheduleDateTv.text.toString())
        editor.putString("missionTitle", binding.missionTitleTv.text.toString())
        editor.putString("scheduleStartTime", scheduleData?.startAt)
        editor.putString("scheduleEndTime", scheduleData?.endAt)
        editor.putString("scheduleMemo", binding.scheduleMemoEtv.text.toString())
        scheduleData!!.missionId?.let { editor.putLong("missionId", it) }
        editor.apply()

        var bundle = Bundle()
        bundle.putInt("position", position)

        val scheduleSpinnerDialogFragment = ScheduleCreateSpinnerDialogFragment()
        scheduleSpinnerDialogFragment.arguments = bundle
        scheduleSpinnerDialogFragment.show(supportFragmentManager, "ScheduleEditDialog")

        // 데이터 받아 오는 부분
        scheduleSpinnerDialogFragment.setFragmentInterface(object : ScheduleCreateSpinnerDialogFragment.FragmentInterface {
            override fun onBtnClick(isComplete: Boolean) {
               if(isComplete){ //확인 눌렀을때만 값가져오기
                   val sharedPreference = getSharedPreferences("scheduleModifiedData", MODE_PRIVATE)
                   if (sharedPreference.contains("scheduleStartTime")){//데이터 있는지 확인
                       scheduleData.startAt = sharedPreference.getString("scheduleStartTime", "").toString()
                       scheduleData.endAt = sharedPreference.getString("scheduleEndTime", "").toString()
                       scheduleData.missionId = sharedPreference.getLong("missionId", -1)

                       binding.scheduleStartAtEtv.setText(scheduleTimeFormatter(scheduleData?.startAt))
                       binding.scheduleEndAtEtv.setText(scheduleTimeFormatter(scheduleData?.endAt))
                   }
               }
            }
        })
    }

    private fun createToast(message : String){
        // 뷰 바인딩을 사용하여 커스텀 레이아웃을 인플레이트합니다.
        val snackbarBinding = ToastCreateScheduleBinding.inflate(layoutInflater)
        snackbarBinding.toastMessageTv.text = message

        // 스낵바 생성 및 설정
        val snackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_SHORT).apply {
            animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
            (view as Snackbar.SnackbarLayout).apply {
                setBackgroundColor(Color.TRANSPARENT)
                addView(snackbarBinding.root)
                translationY = -30.dpToPx().toFloat()
                elevation = 0f
            }
        }
        // 스낵바 표시
        snackbar.show()
    }
    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()


    private fun scheduleAddApi(){
        // SharedPreferences 객체 가져오기
        val sharedPreferences = getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        // JWT 값 가져오기
        val token = sharedPreferences.getString("jwt", null)

        val requestBody = ScheduleAddRequest(
            scheduleTitle = binding.scheduleTitleEtv.text.toString(),
            content = binding.scheduleMemoEtv.text.toString() ,//메모
            startAt = scheduleData.startAt,
            endAt = scheduleData.endAt,
            missionId = scheduleData.missionId,
            scheduleWhen = scheduleDateFormatter()
        )
        Log.d("retrofit", "ScheduleAddRequest: $requestBody");

        val service = RetrofitClient.getInstance().create(ScheduleAddService::class.java)
        val listCall = service.scheduleAdd(token, requestBody)

        listCall.enqueue(object : Callback<ScheduleAddResponse> {
            override fun onResponse(
                call: Call<ScheduleAddResponse>,
                response: Response<ScheduleAddResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("retrofit", response.body().toString());

                        val createScheduleIntent = Intent(this@CreateScheduleActivity, ScheduleFragment::class.java)
                        createScheduleIntent.putExtra("isCreate", true)
                        createScheduleIntent.putExtra("message", "작성하신 일정이 저장되었어요!")
                        setResult(RESULT_OK, createScheduleIntent)
                        finish()

                }else {
                    createToast("일정 저장 실패")
                    Log.e("retrofit", "scheduleAddApi_onResponse: Error ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("retrofit", "scheduleAddApi_onResponse: Error Body $errorBody")
                }}
            override fun onFailure(call: Call<ScheduleAddResponse>, t: Throwable) {
                createToast("일정 저장 실패")
                Log.e("retrofit", "scheduleAddApi_onFailure: ${t.message}")
            }
        })
    }

    //currentMission api연결
    private fun currentMissionApi() {
        val sharedPreferences =
            getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", null)

        val service = RetrofitClient.getInstance().create(CurrentMissionService::class.java)
        val listCall = service.currentMission(token)
        listCall.enqueue(object : Callback<CurrentMissionResponse> {
            override fun onResponse(
                call: Call<CurrentMissionResponse>, response: Response<CurrentMissionResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("retrofit", "currentMissionApi " + response.body().toString());
                    val missionList:ArrayList<CurrentMissionResult> = (response.body()?.result as ArrayList<CurrentMissionResult>?)!!
                    setDialogMissionAdapter(missionList)
                } else {
                    Log.e("retrofit", "currentMissionApi_onResponse: Error ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("retrofit", "currentMissionApi_onResponse: Error Body $errorBody")
                }
            }

            override fun onFailure(call: Call<CurrentMissionResponse>, t: Throwable) {
                Log.e("retrofit", "currentMissionApi_onFailure: ${t.message}")
            }
        })
    }



    //sharedPreference에 저장할때 사용용
//    private fun getScheduleDate() : String{
//        var year = binding.scheduleYearTv.text.toString()
//        var month = binding.scheduleMonthTv.text.toString()
//        var day = binding.scheduleDayTv.text.toString()
//        return "$year-$month-$day"
//    }

    /**
    1. 정규식 패턴 ^[a-z] : 영어 소문자 허용
    2. 정규식 패턴 ^[A-Z] : 영어 대문자 허용
    3. 정규식 패턴 ^[ㄱ-ㅣ가-힣] : 한글 허용
    4. 정규식 패턴 ^[0-9] : 숫자 허용
    5. 정규식 패턴 ^[ ] or ^[\\s] : 공백 허용
     **/
    private val editTextFilter = InputFilter { source, start, end, dest, dstart, dend ->
        val ps = Pattern.compile("[ㄱ-ㅎㅏ-ㅣ가-힣a-z-A-Z0-9\\s-]+")
        val input = dest.subSequence(0, dstart).toString() + source.subSequence(start, end) + dest.subSequence(dend, dest.length).toString()
        val matcher = ps.matcher(input)

        // 글자수 제한 설정 (예: 최대 10자)
        val maxLength = 10
        if (matcher.matches() && input.length <= maxLength) {
            source
        } else {
            ""
        }
    }

    private fun getDisplayWidthSize(): Int {

        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val deviceWidth = size.x
        //val deviceHeight = size.y

        return deviceWidth
    }


    //startTime, endTime 포맷
    private fun scheduleTimeFormatter(startAt: String?): String {
        if (startAt.isNullOrBlank()) {
            return ""
        }

        val formatter = DecimalFormat("00")

        val time = startAt.split(":")
        val hour = time[0].toInt()
        val minute = time[1].toInt()

        if (hour < 12) {
            return "오전 ${formatter.format(hour)}:${formatter.format(minute)}"
        } else {
            if (hour == 12) {
                return "오후 ${formatter.format(hour)}:${formatter.format(minute)}"
            } else {
                return "오후 ${formatter.format(hour - 12)}:${formatter.format(minute)}"
            }
        }
    }


    //화면의 날짜를 yyyy-mm-dd형식으로 포맷
    private fun scheduleDateFormatter():String{
        val formatter = DecimalFormat("00")

        return binding.scheduleDateTv.text.toString()
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