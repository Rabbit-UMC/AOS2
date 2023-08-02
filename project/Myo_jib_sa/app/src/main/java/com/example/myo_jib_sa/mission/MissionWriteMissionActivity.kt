package com.example.myo_jib_sa.mission

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myo_jib_sa.databinding.ActivityMissionWriteMissionBinding
import com.example.myo_jib_sa.mission.Dialog.DataTransferInterface
import com.example.myo_jib_sa.mission.Dialog.MissionReportDialogFragment
import com.example.myo_jib_sa.mission.Dialog.MissionSubjectDialogFragment
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


class MissionWriteMissionActivity : AppCompatActivity(),DataTransferInterface {
    private lateinit var binding:ActivityMissionWriteMissionBinding
    lateinit var calendarAdapter : CalendarAdapter //calendarRvItemClickEvent() 함수에 사용하기 위해 전역으로 선언
    lateinit var selectedDate : LocalDate //오늘 날짜
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMissionWriteMissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val scheduleFrag = ScheduleFragment()


        selectedDate = LocalDate.now()//오늘 날짜 가져오기

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

        //완료 누르면 api 연결하고 종료 되도록
        binding.missionCompleteTxt.setOnClickListener{
            //api연결
            finish()
        }
        //주제 누르면 주제 dialog
        binding.selectSubjectTxt.setOnClickListener {
            val subjectDialog = MissionSubjectDialogFragment(this)
            subjectDialog.show(supportFragmentManager, "mission_subject_dialog")
        }

        //공유 switch 버튼
        binding.shareSwitchBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.shareSwitchBtn.isChecked = true
            }
        }

    }

    // 인터페이스 메서드 구현
    override fun onDataTransfer(subject: String) {
        // Dialog에서 전달된 선택 주제를 기존화면 주제에 연결
        binding.selectSubjectTxt.text = subject
        Log.d("subject", "Received data: $subject")
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

                // 날짜를 클릭했을 때 start_year_txt, start_month_txt, start_day_txt에 해당 날짜 표시
                binding.startYearTxt.text = iYear.toString()
                binding.startMonthTxt.text = iMonth.toString()
                binding.startDayTxt.text = iDay.toString()


                Toast.makeText(this@MissionWriteMissionActivity, "${iYear}년 ${iMonth}월 ${iDay}일", Toast.LENGTH_SHORT).show()

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