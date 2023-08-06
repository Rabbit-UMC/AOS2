package com.example.myo_jib_sa.community

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Retrofit.Constance
import com.example.myo_jib_sa.community.Retrofit.manager.ManagerRetrofitManager
import com.example.myo_jib_sa.community.Retrofit.manager.MissionCreateRequest
import com.example.myo_jib_sa.community.dialog.CommunityPopupOk
import com.example.myo_jib_sa.databinding.ActivityManagerPageMissionBinding
import com.example.myo_jib_sa.schedule.ScheduleFragment
import com.example.myo_jib_sa.schedule.adapter.CalendarAdapter
import com.example.myo_jib_sa.schedule.adapter.CalendarData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class ManagerPageMissionActivity : AppCompatActivity(){

    private lateinit var binding:ActivityManagerPageMissionBinding
    lateinit var calendarAdapter : CalendarAdapter //calendarRvItemClickEvent() 함수에 사용하기 위해 전역으로 선언
    lateinit var selectedDate : LocalDate //오늘 날짜

    //시작일, 종료일
    private var isStartDay:Boolean=true

    //게시판 id
    private var boardId:Int=0

    //제목 글자수
    private var noTitle:Boolean=true

    //묘방생 미션인지
    private var isBye:Boolean=false


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityManagerPageMissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val scheduleFrag = ScheduleFragment()

        //게시판 아이디 설정
        boardId=intent.getIntExtra("boardId",0)
        isBye=intent.getBooleanExtra("isBye", false)

        selectedDate = LocalDate.now()//오늘 날짜 가져오기

        setCalendarBtn() //달력 이미지 버튼 설정


        //calendarAdapter 임시 초기화
        calendarAdapter = CalendarAdapter(ArrayList())
        calendarRvItemClickEvent()//Calendar rv item클릭 이벤트

        setCalendarAdapter()//화면 초기화
        //calendarRvItemClickEvent()//Calendar rv item클릭 이벤트

        //캘린더에 이전달 다음달 이동 버튼 세팅
        calenderBtn()

        //뒤로가기 버튼
        binding.missionWriteBackBtn.setOnClickListener{
            finish()
        }

        //택스트 수 리스너
        // TextWatcher를 등록하여 EditText의 텍스트 변경 이벤트를 감지.
        binding.missionMissionNameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 입력하기 전에 호출됩니다.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 입력이 변경될 때마다 호출
                // 현재 입력된 글자 수를 계산하여 처리
                val currentText = s.toString()
                val charCount = currentText.length

                // 글자 수가 0인 경우 noTitle 변수를 true로 설정
                noTitle = charCount == 0

                // 예시로 글자 수를 로그로 출력합니다.
                println("현재 글자 수: $charCount")
                println("notitle : $noTitle")
            }

            override fun afterTextChanged(s: Editable?) {
                // 입력이 끝났을 때 호출
            }
        })


        //완료 누르면 api 연결하고 종료 되도록
        binding.missionCompleteTxt.setOnClickListener{
            checkDate { isSuccess-> //todo: 시작, 종료일 형식 확인 부분 미완, 이 부분에서 오류 남
                if(!isSuccess){
                    showDialog("날짜, 제목을 다시 확인해주세요")
                    Log.d("미션 생성", "날짜 형식 오류")
                }else{
                    if(noTitle){ //todo: 글자수 부분 체크 한ㄴ 거 문제 없는 거 확인함
                        Log.d("미션 생성", "제목이 비었다")
                        Log.d("미션 생성", "notitle ${noTitle.toString()}")
                        showDialog("날짜, 제목을 다시 확인해주세요")
                    }else{
                        makeRequestData { Rdata ->
                            missionCreate(Constance.jwt, Rdata) { isSuccess ->
                                if (isSuccess) {
                                    finish() // 서버 전송 성공
                                } else {
                                    Toast.makeText(this, "미션 저장에 실패 했습니다.", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                    }
                }
            }
        }

        //시작일, 종료일 설정
        binding.calenderStartDateCheckBtn.setOnClickListener {
            isStartDay=true
            setCalendarBtn()
        }
        binding.calenderEndDateCheckBtn.setOnClickListener {
            isStartDay=false
            setCalendarBtn()
        }


    }

    //팝업창 띄우기
    private fun showDialog(txt:String){
        val DelDialog = CommunityPopupOk(this,txt)
        DelDialog.show()}

    //api 연결
    private fun missionCreate(author:String, data:MissionCreateRequest, callback: (Boolean)->Unit){
        val retrofitManager=ManagerRetrofitManager.getInstance(this)
        retrofitManager.missionCreate(author, data, boardId.toLong()){isSuccess->
            if(isSuccess){
                callback(true)
            }else{
                callback(false)
            }
        }
    }

    //데이터 모으기
    private fun makeRequestData(callback: (MissionCreateRequest) -> Unit){
        val start="${binding.startYearTxt.text}-${binding.startMonthTxt.text}-${binding.startDayTxt.text}"
        val end="${binding.endYearTxt.text}-${binding.endMonthTxt.text}-${binding.endDayTxt.text}"

        val request=MissionCreateRequest(
            binding.missionMissionNameInput.text.toString(),
            binding.subMemoEt.text.toString(),
            start,
            end,
            isBye
        )
        callback(request)
    }

    //버튼 아이콘 설정
    private fun setCalendarBtn(){
        if(isStartDay){
            binding.calenderStartDateCheckBtn.setImageResource(R.drawable.ic_schedule_calendar)
            binding.calenderEndDateCheckBtn.setImageResource(R.drawable.ic_schedule_calendar_black)
        }else{
            binding.calenderStartDateCheckBtn.setImageResource(R.drawable.ic_schedule_calendar_black)
            binding.calenderEndDateCheckBtn.setImageResource(R.drawable.ic_schedule_calendar)
        }
    }

    //시작, 종료일 형식 확인
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkDate(callback: (Boolean) -> Unit){
        val endYear=binding.endYearTxt.text.toString().toIntOrNull()
        val startYear=binding.startYearTxt.text.toString().toIntOrNull()
        println("시작년도 : $startYear")
        println("종료년도  : $endYear")
        if(endYear == null || startYear == null ||endYear<startYear){
            callback(false)
            return
        }
        val endMonth=binding.endMonthTxt.text.toString().toIntOrNull()
        val startMonth=binding.startMonthTxt.text.toString().toIntOrNull()
        println("시작년도 : $startMonth")
        println("종료년도  : $endMonth")
        if(endMonth == null || startMonth == null ||endMonth<startMonth){
            callback(false)
            return
        }
        val endDay=binding.endDayTxt.text.toString().toIntOrNull()
        val startDay=binding.startDayTxt.text.toString().toIntOrNull()
        println("시작년도 : $startDay")
        println("종료년도  : $endDay")
        if(endDay == null || startDay == null ||endDay<startDay){
            callback(false)
            return
        }
        // 시작일과 종료일의 차이가 30일 이상인지 확인
        val startDate = LocalDate.of(startYear, startMonth, startDay)
        val endDate = LocalDate.of(endYear, endMonth, endDay)
        val difference = endDate.toEpochDay() - startDate.toEpochDay()
        if (difference >= 30) {
            callback(false)
            return
        }

        // 시작일이 현재 날짜보다 과거인지 확인
        val currentDate = LocalDate.now()
        if (startDate.isBefore(currentDate)) {
            callback(false)
            return
        }

        callback(true)
    }


    //날짜 생성: ArrayList<CalendarData>()생성
    @RequiresApi(Build.VERSION_CODES.O)
    fun dayInMonthArray(): ArrayList<CalendarData> {
        val dayList = ArrayList<CalendarData>()
        var yearMonth = YearMonth.from(selectedDate)

        //해당 월의 마지막 날짜 가져오기(결과: 1월이면 31)
        var lastDay = yearMonth.lengthOfMonth()
        //해당 월의 첫번째 날 가져오기(결과: 2023-01-01)
        var firstDay = selectedDate.withDayOfMonth(1)
        //첫 번째날 요일 가져오기(결과: 월 ~일이 1~7에 대응되어 나타남)
        var dayOfWeek = firstDay.dayOfWeek.value

        for (i in 1..42) {
            if (dayOfWeek == 7) {//그 달의 첫날이 일요일일때 작동: 한칸 아래줄부터 날짜 표시되는 현상 막기위해
                if (i > lastDay)
                    break
                dayList.add(CalendarData(LocalDate.of(selectedDate.year, selectedDate.monthValue, i)))
            } else if (i <= dayOfWeek || i > (lastDay + dayOfWeek)) {//그 외 경우
                dayList.add(CalendarData(null))
            } else {
                dayList.add(CalendarData(LocalDate.of(selectedDate.year, selectedDate.monthValue, i - dayOfWeek)))//얘만 살리기
            }
        }

        return dayList
    }

    //Calendar rv item클릭 이벤트
    fun calendarRvItemClickEvent() {
        calendarAdapter.setItemClickListener(object : CalendarAdapter.OnItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(calendarData: CalendarData) {
                // 클릭 시 이벤트 작성
                Log.d("debug", "클릭!")

                var iYear = calendarData.date?.year
                var iMonth = calendarData.date?.monthValue
                var iDay = calendarData.date?.dayOfMonth

                if(isStartDay){
                    // 날짜를 클릭했을 때 start_year_txt, start_month_txt, start_day_txt에 해당 날짜 표시
                    binding.startYearTxt.text = iYear.toString()
                    binding.startMonthTxt.text = iMonth.toString()
                    binding.startDayTxt.text = iDay.toString()
                }else{
                    binding.endYearTxt.text=iYear.toString()
                    binding.endMonthTxt.text=iMonth.toString()
                    binding.endDayTxt.text=iDay.toString()
                }


                Toast.makeText(this@ManagerPageMissionActivity, "${iYear}년 ${iMonth}월 ${iDay}일", Toast.LENGTH_SHORT).show()

                CoroutineScope(Dispatchers.Main).launch {
                    /*scheduleOfDayApi(YYYYMMDDFromDate(calendarData.date))//scheduleOfDay api연결*/
                    delay(100)
                    /*setScheduleAdapter(calendarData.date)*/
                }
            }
        })
    }

    //month화면에 보여주기
    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setCalendarAdapter(){
        //month를 month text view에 보여주기 (결과: 1월)
        binding.monthTv.text = monthFromDate(selectedDate)

        CoroutineScope(Dispatchers.Main).launch {

            delay(1000)
            //이번달 날짜 가져오기
            val dayList = dayInMonthArray()

            // CalendarAdapter 리사이클러뷰 연결
            calendarAdapter = CalendarAdapter(dayList)
            binding.calendarRv.layoutManager = GridLayoutManager(applicationContext, 7)
            binding.calendarRv.adapter = calendarAdapter

            calendarRvItemClickEvent()//Calendar rv item클릭 이벤트
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun calenderBtn(){
        //이전달로 이동
        binding.preMonthBtn.setOnClickListener{
            selectedDate = selectedDate.minusMonths(1)
            CoroutineScope(Dispatchers.Main).launch {
                setCalendarAdapter()
                //calendarRvItemClickEvent()
            }
        }
        //다음달로 이동
        binding.nextMonthBtn.setOnClickListener{
            selectedDate =selectedDate.plusMonths(1)
            CoroutineScope(Dispatchers.Main).launch {
                setCalendarAdapter()
                //calendarRvItemClickEvent()
            }
        }
    }

    //M월 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    private fun monthFromDate(date : LocalDate):String{
        var formatter = DateTimeFormatter.ofPattern("M월")
        return date.format(formatter)
    }
}