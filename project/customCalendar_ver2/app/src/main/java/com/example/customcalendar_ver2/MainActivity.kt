package com.example.customcalendar_ver2

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.example.customcalendar_ver2.databinding.ActivityMainBinding
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter



class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    lateinit var selectedDate : LocalDate //오늘 날짜
    lateinit var calendarAdapter : CalendarAdapter //calendarRvItemClickEvent() 함수에 사용하기 위해 전역으로 선언
    var prePosition : Int = 0 ////calendarRvItemClickEvent() 함수에 사용하기 위해 선언


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //오늘 날짜
        selectedDate = LocalDate.now()
        //화면 초기화
        setMonthView()

        //아이템 클릭이벤트
        calendarRvItemClickEvent()

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
    //month화면에 보여주기
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMonthView(){
        //month를 month text view에 보여주기 (결과: 1월)
        binding.monthTv.text = monthFromDate(selectedDate)
        //year를 year text view에 보여주기 (결과: 2023년)
        binding.yearTv.text = yearFromDate(selectedDate)

        //이번달 날짜 가져오기
        val dayList = dayInMonthArray(selectedDate)

        //리사이클러뷰 연결
        calendarAdapter = CalendarAdapter(dayList)
        binding.calendarRv.layoutManager = GridLayoutManager(this, 7)
        binding.calendarRv.adapter = calendarAdapter
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

    //날짜 생성
    @RequiresApi(Build.VERSION_CODES.O)
    private fun dayInMonthArray(date: LocalDate):ArrayList<SelectDateData>{
        val dayList = ArrayList<SelectDateData>()
        var yearMonth = YearMonth.from(date)

        //해당 월의 마지막 날짜 가져오기(결과: 1월이면 31)
        var lastDay = yearMonth.lengthOfMonth()
        //해당 월의 첫번째 날 가져오기(결과: 2023-01-01)
        var firstDay = selectedDate.withDayOfMonth(1)
        //첫 번째날 요일 가져오기(결과: 월 ~일이 1~7에 대응되어 나타남)
        var dayOfWeek = firstDay.dayOfWeek.value

        for(i in 1..42){
            if(i<=dayOfWeek || i>(lastDay + dayOfWeek)){
                dayList.add(SelectDateData(null))
            }else{
                    dayList.add(SelectDateData(LocalDate.of(selectedDate.year, selectedDate.monthValue, i-dayOfWeek)))//얘만 살리기
            }
        }
    return dayList
    }


    //Calendar rv item클릭 이벤트
    fun calendarRvItemClickEvent() {
        calendarAdapter.setItemClickListener(object : CalendarAdapter.OnItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(selectDateData: SelectDateData, position: Int) {


                var iYear = selectDateData.date?.year
                var iMonth = selectDateData.date?.monthValue
                var iDay = selectDateData.date?.dayOfMonth

                Toast.makeText(this@MainActivity, "${iYear}년 ${iMonth}월 ${iDay}일", Toast.LENGTH_SHORT)
                    .show()

            }
        })
    }
}