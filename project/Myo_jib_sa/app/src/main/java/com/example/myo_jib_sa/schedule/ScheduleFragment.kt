package com.example.myo_jib_sa.schedule

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.FragmentScheduleBinding
import com.example.myo_jib_sa.schedule.adapter.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter


class ScheduleFragment : Fragment() {

    lateinit var binding: FragmentScheduleBinding
    lateinit var calendarAdapter : CalendarAdapter //calendarRvItemClickEvent() 함수에 사용하기 위해 전역으로 선언
    lateinit var selectedDate : LocalDate //오늘 날짜
    var mDataList = ArrayList<currentMissionData>() //미션 리스트 데이터
    var sDataList = ArrayList<scheduleData>() //미션 리스트 데이터

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduleBinding.inflate(inflater, container, false)

        selectedDate = LocalDate.now()//오늘 날짜 가져오기
        setMonthView()//화면 초기화

        calendarRvItemClickEvent()//Calendar rv item클릭 이벤트

        //CurrentMissionAdapter 리사이클러뷰 연결
        mDataList.add(currentMissionData("헬스","10","+ 5"))
        mDataList.add(currentMissionData("헬스","10","- 10"))
        mDataList.add(currentMissionData("헬스","10","- 10"))
        val currentMissionAdapter = CurrentMissionAdapter(mDataList)
        binding.currentMissionRv.layoutManager = LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.currentMissionRv.adapter = currentMissionAdapter

        //ScheduleAdaptar 리사이클러뷰 연결
        sDataList.add(scheduleData("헬스 4일차","19:00","20:00"))
        sDataList.add(scheduleData("헬스 5일차","19:00","20:00"))
        sDataList.add(scheduleData("헬스 6일차","19:00","20:00"))
        sDataList.add(scheduleData("헬스 4일차","19:00","20:00"))
        sDataList.add(scheduleData("헬스 5일차","19:00","20:00"))
        sDataList.add(scheduleData("헬스 6일차","19:00","20:00"))
        val scheduleAdaptar = ScheduleAdaptar(sDataList)
        binding.scheduleRv.layoutManager = LinearLayoutManager(getActivity())
        binding.scheduleRv.adapter = scheduleAdaptar

        //noScheduleIv의 visibility설정
        if(sDataList.size == 0)
            binding.noScheduleIv.visibility = View.VISIBLE
        else
            binding.noScheduleIv.visibility = View.GONE

        //캘린더 visible버튼
        binding.calenderVisibleBtn.setOnClickListener{
            if(binding.calenderLayout.visibility == View.GONE)
                binding.calenderLayout.visibility = View.VISIBLE
            else
                binding.calenderLayout.visibility = View.GONE
        }

        //캘린더에 이전달 다음달 이동 버튼 세팅
        calenderBtn()

        return binding.root
    }

    //month화면에 보여주기
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMonthView(){
        //month를 month text view에 보여주기 (결과: 1월)
        binding.monthTv.text = monthFromDate(selectedDate)
        //2023년 6월 표시
        binding.selectedYearMonthTv.text = YYYYMMFromDate(selectedDate)
        //6월 1일 일정 표시
        binding.selectedMonthDayTv.text = "${MMDDFromDate(selectedDate)} 일정"

        //이번달 날짜 가져오기
        val dayList = dayInMonthArray(selectedDate)

        //CalendarAdapter리사이클러뷰 연결
        calendarAdapter = CalendarAdapter(dayList)
        binding.calendarRv.layoutManager = GridLayoutManager(getActivity(), 7)
        binding.calendarRv.adapter = calendarAdapter
    }

    //M월 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    private fun monthFromDate(date : LocalDate):String{
        var formatter = DateTimeFormatter.ofPattern("M월")
        return date.format(formatter)
    }

    //날짜 생성: ArrayList<CalendarData>()생성
    @RequiresApi(Build.VERSION_CODES.O)
    private fun dayInMonthArray(date: LocalDate):ArrayList<CalendarData>{
        val dayList = ArrayList<CalendarData>()
        var yearMonth = YearMonth.from(date)

        //해당 월의 마지막 날짜 가져오기(결과: 1월이면 31)
        var lastDay = yearMonth.lengthOfMonth()
        //해당 월의 첫번째 날 가져오기(결과: 2023-01-01)
        var firstDay = selectedDate.withDayOfMonth(1)
        //첫 번째날 요일 가져오기(결과: 월 ~일이 1~7에 대응되어 나타남)
        var dayOfWeek = firstDay.dayOfWeek.value

        for(i in 1..42){
            if(dayOfWeek == 7){//그 달의 첫날이 일요일일때 작동: 한칸 아래줄부터 날짜 표시되는 현상 막기위해
                if(i>lastDay)
                    break
                dayList.add(CalendarData(LocalDate.of(selectedDate.year, selectedDate.monthValue, i)))//얘만 살리기
            }
            else if(i<=dayOfWeek || i>(lastDay + dayOfWeek)){//그 외 경우
                dayList.add(CalendarData(null))
            }
            else{
                //test code
                if(i == 15)
                    dayList.add(CalendarData(LocalDate.of(selectedDate.year, selectedDate.monthValue, i-dayOfWeek), true))//test code
                //test code, else 바로 아래 dayList.add 코드는 살리기
                else
                    dayList.add(CalendarData(LocalDate.of(selectedDate.year, selectedDate.monthValue, i-dayOfWeek)))//얘만 살리기
            }
        }
        return dayList
    }

    //캘린더에 이전달 다음달 이동 버튼 세팅
    @RequiresApi(Build.VERSION_CODES.O)
    private fun calenderBtn(){
        //이전달로 이동
        binding.preMonthBtn.setOnClickListener{
            selectedDate = selectedDate.minusMonths(1)
            setMonthView()
            calendarRvItemClickEvent()
        }
        //다음달로 이동
        binding.nextMonthBtn.setOnClickListener{
            selectedDate =selectedDate.plusMonths(1)
            setMonthView()
            calendarRvItemClickEvent()
        }
    }
//Calendar rv item클릭 이벤트--2
    fun calendarRvItemClickEvent() {
    calendarAdapter.setItemClickListener(object : CalendarAdapter.OnItemClickListener {

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onClick(calendarData: CalendarData) {
            // 클릭 시 이벤트 작성
            Log.d("debug", "클릭!")
            //2023년 6월 표시
            binding.selectedYearMonthTv.text = YYYYMMFromDate(calendarData.date)
            //6월 1일 일정 표시
            binding.selectedMonthDayTv.text = "${MMDDFromDate(calendarData.date)} 일정"

            var iYear = calendarData.date?.year
            var iMonth = calendarData.date?.monthValue
            var iDay = calendarData.date?.dayOfMonth

            Toast.makeText(context, "${iYear}년 ${iMonth}월 ${iDay}일", Toast.LENGTH_SHORT)
                .show()

        }
    })
}
    
    //YYYY년 M월 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    private fun YYYYMMFromDate(date : LocalDate?):String?{
        var formatter = DateTimeFormatter.ofPattern("YYYY년 M월")
        return date?.format(formatter)
    }
    //M월 D일 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    private fun MMDDFromDate(date : LocalDate?):String?{
        var formatter = DateTimeFormatter.ofPattern("M월 d일")
        return date?.format(formatter)
    }
}