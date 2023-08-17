package com.example.myo_jib_sa.mission

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.ActivityMissionWriteMissionBinding
import com.example.myo_jib_sa.mission.Dialog.DataTransferInterface
import com.example.myo_jib_sa.mission.Dialog.MissionErrorDialogFragment
import com.example.myo_jib_sa.mission.Dialog.MissionReportDialogFragment
import com.example.myo_jib_sa.mission.Dialog.MissionSubjectDialogFragment
import com.example.myo_jib_sa.schedule.ScheduleFragment
import com.example.myo_jib_sa.schedule.adapter.CalendarAdapter
import com.example.myo_jib_sa.schedule.adapter.CalendarData
import com.example.myo_jib_sa.schedule.createScheduleActivity.ErrorDialogFragment
import com.example.myo_jib_sa.schedule.createScheduleActivity.adapter.CreateScheduleCalendarAdapter
import com.example.myo_jib_sa.schedule.createScheduleActivity.adapter.SelectDateData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern


class MissionWriteMissionActivity : AppCompatActivity(),DataTransferInterface{
    private lateinit var binding:ActivityMissionWriteMissionBinding

    private lateinit var referenceDate : LocalDate //오늘 날짜
    private lateinit var selectedDate : LocalDate //종료 날짜

    private lateinit var startSelectedDate : LocalDate //시작 날짜
    private lateinit var endSelectedDate : LocalDate //종료 날짜

    private lateinit var calendarAdapter : CreateScheduleCalendarAdapter //calendarRvItemClickEvent() 함수에 사용하기 위해 전역으로 선언
    private var dayList = ArrayList<SelectDateData>() //calendarAdapter item list

    private var isClickStartCalendarImgBtn = false //달력이미지버튼 클릭에서 사용
    private var isClickEndCalendarImgBtn = false //달력이미지버튼 클릭에서 사용

    private var selectedDateIndex : Int = 0//referenceDate의 dayList에서 index값 //달력이미지버튼 클릭에서 사용

    // 시작일 종료일 설정하기 위한 상태 변수
    private var isSelectingStartTime = false
    private var isSelectingEndTime = false

    //카테고리 설정 상태 변수
    private var isSelectingCategory = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMissionWriteMissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //일정 제목 특수문자 제어
        binding.missionTitleInput.filters = arrayOf(editTextFilter)

        //오늘 날짜
        referenceDate = LocalDate.now()
        selectedDate = referenceDate

        startSelectedDate = referenceDate
        endSelectedDate = referenceDate

        //화면 초기화
        setMonthView()

        //오늘날짜 편집 화면에 표시
        //시작
        binding.missionStartYearTxt.text = referenceDate?.year.toString()
        binding.missionStartMonthTxt.text = referenceDate?.monthValue.toString()
        binding.missionStartDateTxt.text = referenceDate?.dayOfMonth.toString()
        //종료
        binding.missionEndYearTxt.text = referenceDate?.year.toString()
        binding.missionEndMonthTxt.text = referenceDate?.monthValue.toString()
        binding.missionEndDayTxt.text = referenceDate?.dayOfMonth.toString()


        setBtn()//버튼 setting
        setMemoMaxLine()//메모 최대 3줄로 제한
        startCalendarRvItemClickEvent()


        //주제 누르면 주제 dialog
        binding.missionSelectSubjectTxt.setOnClickListener {
            isSelectingCategory=true
            val subjectDialog = MissionSubjectDialogFragment(this)
            subjectDialog.show(supportFragmentManager, "mission_subject_dialog")
        }

        //공유 switch 버튼
        binding.shareSwitchBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.shareSwitchBtn.isChecked = true
            }
        }
        if(!isSelectingCategory){
            //api 주제 부분 "자유"로 설정되도록~~
        }
    }

    // 인터페이스 메서드 구현
    override fun onDataTransfer(subject: String) {
        // Dialog에서 전달된 선택 주제를 기존화면 주제에 연결
        binding.missionSelectSubjectTxt.text = if (subject.isNotEmpty()) { //주제가 선택 되지 않으면 자유로 설정
            subject
        } else {
            "자유"
        }
        Log.d("subject", "Received data: $subject")
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
        binding.calendarRv.layoutManager = GridLayoutManager(this, 7)
        binding.calendarRv.adapter = calendarAdapter
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

            if(dayOfWeek == 7){//그 달의 첫날이 일요일일때 작동: 한칸 아래줄부터 날짜 표시되는 현상 막기위해
                if(i>lastDay) {
                    //break
                    dayList.add(SelectDateData(null))
                }
                else {
                    if (i == referenceDate.dayOfMonth) {//referenceDate의 dayList에서 index값
                        selectedDateIndex = i - 1
                    }
                    if (selectedDate == LocalDate.of(
                            referenceDate.year,
                            referenceDate.monthValue,
                            i
                        )
                    ) { //현재 선택한 date
                        dayList.add(
                            SelectDateData(
                                LocalDate.of(
                                    referenceDate.year,
                                    referenceDate.monthValue,
                                    i
                                ), true
                            )
                        )
                    } else {
                        dayList.add(
                            SelectDateData(
                                LocalDate.of(
                                    referenceDate.year,
                                    referenceDate.monthValue,
                                    i
                                )
                            )
                        )
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
    private fun startCalendarRvItemClickEvent() {
        calendarAdapter.setItemClickListener(object : CreateScheduleCalendarAdapter.OnItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(selectDateData: SelectDateData, position: Int) {

                // 오늘날짜 파란 동그라미 없애기
                val temp = dayList[selectedDateIndex]
                if (temp.isSelected) {
                    dayList[selectedDateIndex] = SelectDateData(temp.date, false)
                    calendarAdapter.notifyItemChanged(selectedDateIndex)
                }

                selectedDate = selectDateData.date!!

                if (isSelectingStartTime) {
                    startSelectedDate = selectDateData.date
                    updateStartDateOnUI(startSelectedDate!!)
                    //Toast.makeText(this@MissionWriteMissionActivity, "$startSelectedDate", Toast.LENGTH_SHORT).show()

                } else if (isSelectingEndTime) {

                    endSelectedDate = selectDateData.date
                    updateEndDateOnUI(endSelectedDate!!)

                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateStartDateOnUI(date: LocalDate?) {
        // 선택한 시작일을 UI에 반영
        if (date != null) {
            binding.missionStartYearTxt.text = date.year.toString()
            binding.missionStartMonthTxt.text = date.monthValue.toString()
            binding.missionStartDateTxt.text = date.dayOfMonth.toString()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateEndDateOnUI(date: LocalDate?) {
        // 선택한 종료일을 UI에 반영
        if (date != null) {
            binding.missionEndYearTxt.text = date.year.toString()
            binding.missionEndMonthTxt.text = date.monthValue.toString()
            binding.missionEndDayTxt.text = date.dayOfMonth.toString()
        }
    }



    //버튼 setting
    @RequiresApi(Build.VERSION_CODES.O)
    fun setBtn() {

        //뒤로가기 버튼 클릭
        binding.missionBackBtn.setOnClickListener {
            finish()
        }

        //완료 버튼
        binding.missionCompleteTxt.setOnClickListener {
            if (binding.missionTitleInput.text.isEmpty()||startSelectedDate >= endSelectedDate){ //제목이 공백이거나 종료 날짜가 시작 날짜보다 크거나 같으면
                Log.d("exitDebug", "no!!")
                val missionErrorDialogFragment = MissionErrorDialogFragment()
                missionErrorDialogFragment.show(supportFragmentManager, "MissionErrorDialogFragment")
            } else {//정상적일때
                Log.d("exitDebug", "yes!!")
                //미션 작성 api 호출
                finish()
            }
        }

        //캘린더: 이전달로 이동
        binding.preMonthBtn.setOnClickListener {
            referenceDate = referenceDate.minusMonths(1)
            if(isSelectingStartTime){
                isSelectingStartTime=true
                isSelectingEndTime = false
            }
            else{
                isSelectingStartTime=false
                isSelectingEndTime = true
            }
            setMonthView()
            startCalendarRvItemClickEvent()
        }
        //캘린더: 다음달로 이동
        binding.nextMonthBtn.setOnClickListener {
            referenceDate = referenceDate.plusMonths(1)
            if(isSelectingStartTime){
                isSelectingStartTime=true
                isSelectingEndTime = false
            }
            else{
                isSelectingStartTime=false
                isSelectingEndTime = true
            }
            setMonthView()
            startCalendarRvItemClickEvent()
        }

        //캘린더에서 완료 버튼 클릭
        binding.calendarCompleteTv.setOnClickListener {
            binding.calendarLayout.visibility = View.GONE
            binding.guidebanner.visibility = View.VISIBLE
            binding.startCalendarBtn.setImageResource(R.drawable.ic_schedule_calendar_black)
            binding.endCalendarBtn.setImageResource(R.drawable.ic_schedule_calendar_black)
            isClickStartCalendarImgBtn = false
            isClickEndCalendarImgBtn = false
            isSelectingStartTime=false
            isSelectingEndTime = false
        }

        // 시작 달력이미지 클릭
        binding.startCalendarBtn.setOnClickListener {
            if (!isClickStartCalendarImgBtn) {//캘린더 보이게
                binding.calendarLayout.visibility = View.VISIBLE
                binding.guidebanner.visibility = View.GONE
                binding.startCalendarBtn.setImageResource(R.drawable.ic_schedule_calendar)
                isClickStartCalendarImgBtn = true
                isSelectingStartTime = true
                isSelectingEndTime = false // 시작일 선택 중일 때는 종료일 선택 모드 해제
            } else { //캘린더 안보이게
                binding.calendarLayout.visibility = View.GONE
                binding.guidebanner.visibility = View.VISIBLE
                binding.startCalendarBtn.setImageResource(R.drawable.ic_schedule_calendar_black)
                isClickStartCalendarImgBtn = false
                isSelectingStartTime = false
            }
        }

        //종료 달력이미지 클릭
        binding.endCalendarBtn.setOnClickListener {
            if (!isClickEndCalendarImgBtn) {//캘린더 보이게
                binding.calendarLayout.visibility = View.VISIBLE
                binding.guidebanner.visibility = View.GONE
                binding.endCalendarBtn.setImageResource(R.drawable.ic_schedule_calendar)
                isClickEndCalendarImgBtn = true
                isSelectingEndTime = true
                isSelectingStartTime = false // 종료일 선택 중일 때는 시작일 선택 모드 해제
            } else {//캘린더 안보이게
                binding.calendarLayout.visibility = View.GONE
                binding.guidebanner.visibility = View.VISIBLE
                binding.endCalendarBtn.setImageResource(R.drawable.ic_schedule_calendar_black)
                isClickEndCalendarImgBtn = false
                isSelectingEndTime = false
            }
        }


    }

    //메모 최대 3줄로 제한
    private fun setMemoMaxLine(){
        binding.missionMemoInput.addTextChangedListener(object: TextWatcher {
            var maxText = ""
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                maxText = p0.toString()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(binding.missionMemoInput.lineCount > 3){//최대 3줄까지 제한
                    binding.missionMemoInput.setText(maxText)
                    binding.missionMemoInput.setSelection(binding.missionMemoInput.length())//커서
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

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