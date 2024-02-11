package com.example.myo_jib_sa.community.manager

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Constance
import com.example.myo_jib_sa.community.api.manager.ManagerRetrofitManager
import com.example.myo_jib_sa.community.api.manager.MissionCreateRequest
import com.example.myo_jib_sa.community.adapter.CreateScheduleCalendarAdapter
import com.example.myo_jib_sa.community.adapter.SelectDateData
import com.example.myo_jib_sa.databinding.ActivityManagerPageMissionBinding
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

class ManagerMissionCreateActivity : AppCompatActivity(){
    private lateinit var binding: ActivityManagerPageMissionBinding
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

    //게시판 id
    private var boardId:Long=0

    //묘방생 미션 구분
    private var isBye=false


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityManagerPageMissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        boardId=intent.getIntExtra("boardId",0).toLong() //게시판 id
        isBye=intent.getBooleanExtra("isBye", false) //묘방생 미션 구분

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
        setDateTxt()


        setBtn()//버튼 setting, 뒤로가기, 달력
        setMemoMaxLine()//메모 최대 3줄로 제한
        startCalendarRvItemClickEvent()

        //완료 버튼
        complete()

    }

    //미션 생성 완료하기
    @RequiresApi(Build.VERSION_CODES.O)
    private fun complete(){
        binding.missionCompleteTxt.setOnClickListener {
            var is30Down=true
            checkDate { callback->
                is30Down=callback
            }

            if (binding.missionTitleInput.text.isEmpty()||startSelectedDate >= endSelectedDate || !is30Down){ //제목이 공백이거나 종료 날짜가 시작 날짜보다 크거나 같으면
                Log.d("exitDebug", "no!!")
                Log.d("날짜 or 제목 잘못됨", "${binding.missionTitleInput.text.toString()}")
                Log.d("날짜 or 제목 잘못됨", "start:${startSelectedDate}  end:$endSelectedDate")
                Log.d("날짜 or 제목 잘못됨", is30Down.toString())
            } else {//정상적일때
                Log.d("exitDebug", "yes!!")
                //미션 작성 api 호출
                makeRequestData { request->
                    Log.d("request 데이터", request.mainMissionContent)
                    Log.d("request 데이터", request.mainMissionTitle)
                    Log.d("request 데이터", request.missionStartTime)
                    Log.d("request 데이터", request.missionEndTime)
                    Log.d("request 데이터", request.lastMission.toString())
                    Constance.jwt?.let { it1 ->
                        missionCreate(it1,request){ isSuccess->
                            if(isSuccess){
                                finish()
                            }else{
                                Toast.makeText(this, "미션 생성에 실패 했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    //오늘 날짜 화면에 표시
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDateTxt(){
        //시작
        binding.missionStartYearTxt.text = referenceDate?.year.toString()
        binding.missionStartMonthTxt.text = referenceDate?.monthValue?.let { dateFormat(it) }
        binding.missionStartDateTxt.text = referenceDate?.dayOfMonth?.let { dateFormat(it) }
        //종료
        binding.missionEndYearTxt.text = referenceDate?.year.toString()
        binding.missionEndMonthTxt.text = referenceDate?.monthValue?.let { dateFormat(it) }
        binding.missionEndDayTxt.text = referenceDate?.dayOfMonth?.let { dateFormat(it) }
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
        var day:String=""
        var month:String=""
        if (date != null) {
            day =dateFormat(date.dayOfMonth)
        }
        if (date != null) {
            month=dateFormat(date.monthValue)
        }
        // 선택한 시작일을 UI에 반영
        if (date != null) {
            binding.missionStartYearTxt.text = date.year.toString()
            binding.missionStartMonthTxt.text = month
            binding.missionStartDateTxt.text = day
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateEndDateOnUI(date: LocalDate?) {
        var day:String=""
        var month:String=""
        if (date != null) {
            day =dateFormat(date.dayOfMonth)
        }
        if (date != null) {
            month=dateFormat(date.monthValue)
        }
        // 선택한 종료일을 UI에 반영
        if (date != null) {
            binding.missionEndYearTxt.text = date.year.toString()
            binding.missionEndMonthTxt.text = month
            binding.missionEndDayTxt.text = day
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun dateFormat(date: Int):String{
        if (date != null) {
            return if(date<10){
                "0${date}"
            }else{
                date.toString()
            }
        }
        return date.toString()
    }

    //30일 차이 검증, 30이내면 true
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkDate(callback :(Boolean)->Unit){
        // 시작일과 종료일의 차이가 30일 이상인지 확인
        val startDate = startSelectedDate
        Log.d("시작 날짜", startDate.toString())
        val endDate = endSelectedDate
        val difference = endDate.toEpochDay() - startDate.toEpochDay()
        if (difference >= 30) {
            callback(false)
            return
        }else{
            callback(true)
        }
    }

    //버튼 setting
    @RequiresApi(Build.VERSION_CODES.O)
    fun setBtn() {

        //뒤로가기 버튼 클릭
        binding.missionBackBtn.setOnClickListener {
            finish()
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
                binding.endCalendarBtn.setImageResource(R.drawable.ic_schedule_calendar_black)
                binding.calendarLayout.visibility = View.VISIBLE
                binding.guidebanner.visibility = View.GONE
                binding.startCalendarBtn.setImageResource(R.drawable.ic_schedule_calendar)
                isClickStartCalendarImgBtn = true
                isSelectingStartTime = true
                isSelectingEndTime = false
                isClickEndCalendarImgBtn=false
                // 시작일 선택 중일 때는 종료일 선택 모드 해제
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
                binding.startCalendarBtn.setImageResource(R.drawable.ic_schedule_calendar_black)
                binding.calendarLayout.visibility = View.VISIBLE
                binding.guidebanner.visibility = View.GONE
                binding.endCalendarBtn.setImageResource(R.drawable.ic_schedule_calendar)
                isClickEndCalendarImgBtn = true
                isSelectingEndTime = true
                isSelectingStartTime = false
                isClickStartCalendarImgBtn=false
                // 종료일 선택 중일 때는 시작일 선택 모드 해제
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

    //미션 생성 api 연결
    //api 연결
    private fun missionCreate(author:String, data: MissionCreateRequest, callback: (Boolean)->Unit){
        val retrofitManager= ManagerRetrofitManager.getInstance(this)
        retrofitManager.missionCreate(author, data, boardId){isSuccess->
            if(isSuccess){
                callback(true)
            }else{
                callback(false)
            }
        }
    }

    //데이터 모으기
    private fun makeRequestData(callback: (MissionCreateRequest) -> Unit){
        val start="${binding.missionStartYearTxt.text}-${binding.missionStartMonthTxt.text}-${binding.missionStartDateTxt.text}"
        val end="${binding.missionEndYearTxt.text}-${binding.missionEndMonthTxt.text}-${binding.missionEndDayTxt.text}"

        val request=MissionCreateRequest(
            binding.missionTitleInput.text.toString(),
            binding.missionMemoInput.text.toString(),
            start,
            end,
            isBye
        )

        callback(request)
    }
}