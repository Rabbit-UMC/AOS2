package com.example.customcalendar_ver2

import android.content.Context
import android.graphics.Point
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.example.customcalendar_ver2.databinding.ActivityMainBinding
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter



class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    lateinit var selectedDate : LocalDate //선택한 날짜
    lateinit var standardDate: LocalDate //캘린더 생성하기 위한 기준 날짜, selectedDate업데이트 하면 얘도 같이 업데이트 해주기
    lateinit var calendarAdapter : CalendarAdapter //calendarRvItemClickEvent() 함수에 사용하기 위해 전역으로 선언
    var firstSelectedDatePosition : Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //calendar 간격 설정
        binding.calendarRv.addItemDecoration(CalendarAdapter.GridSpaceDecoration(getDisplayWidthSize()))


        //오늘 날짜
        selectedDate = LocalDate.now()
        standardDate = selectedDate
        //화면 초기화
        setMonthView()


        //이전달로 이동
        binding.preMonthBtn.setOnClickListener{
            standardDate = standardDate.minusMonths(1)
            //CoroutineScope(Dispatchers.Main).launch {
            binding.selectedMonthTv.text = monthFromDate(standardDate)
            setMonthView()
        }
        //다음달로 이동
        binding.nextMonthBtn.setOnClickListener{
            standardDate =standardDate.plusMonths(1)
            binding.selectedMonthTv.text = monthFromDate(standardDate)
            setMonthView()
        }
    }
    //month화면에 보여주기
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMonthView(){

        //month를 month text view에 보여주기 (결과: 1월)
        binding.selectedMonthTv.text = monthFromDate(standardDate)
        //year를 year text view에 보여주기 (결과: 2023년)
        binding.selectedYearTv.text = yearFromDate(standardDate)

        //이번달 날짜 가져오기
        val dayList = DayInMonthArray(standardDate)

        //리사이클러뷰 연결
        calendarAdapter = CalendarAdapter(dayList)
        binding.calendarRv.layoutManager = GridLayoutManager(this, 7)
        binding.calendarRv.adapter = calendarAdapter
        calendarRvItemClickEvent(dayList)

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

    //YYYY-MM-DD 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    private fun YYYYMMDDFromDate(date: LocalDate?): String? {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date?.format(formatter)
    }

    //날짜 생성
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun dayInMonthArray(date: LocalDate):ArrayList<selectedDate>{
//        val dayList = ArrayList<selectedDate>()
//        var yearMonth = YearMonth.from(date)
//
//        //해당 월의 마지막 날짜 가져오기(결과: 1월이면 31)
//        var lastDay = yearMonth.lengthOfMonth()
//        //해당 월의 첫번째 날 가져오기(결과: 2023-01-01)
//        var firstDay = selectedDate.withDayOfMonth(1)
//        //첫 번째날 요일 가져오기(결과: 월 ~일이 1~7에 대응되어 나타남)
//        var dayOfWeek = firstDay.dayOfWeek.value
//
//        for(i in 1..42){
//            if (dayOfWeek == 7) {//그 달의 첫날이 일요일일때 작동: 한칸 아래줄부터 날짜 표시되는 현상 막기위해
//                if (i > lastDay) {
//                    break
//                    //dayList.add(CalendarData(null)) //끝에 빈칸 자르기
//                } else {
//                    dayList.add(
//                        selectedDate(
//                            LocalDate.of(
//                                selectedDate.year, selectedDate.monthValue, i
//                            )
//                        )
//                    )
//                }
//            }else if (i <= dayOfWeek) { //끝에 빈칸 자르기위해
//                dayList.add(selectedDate(null))
//            } else if (i > (lastDay + dayOfWeek)) {//끝에 빈칸 자르기위해
//                break
//            }else{
//                    dayList.add(selectedDate(LocalDate.of(selectedDate.year, selectedDate.monthValue, i-dayOfWeek)))//얘만 살리기
//            }
//        }
//    return dayList
//    }

    //날짜 생성: ArrayList<CalendarData>()생성
    @RequiresApi(Build.VERSION_CODES.O)
    private fun DayInMonthArray(date: LocalDate): ArrayList<SelectDateData> {
        var yearMonth = YearMonth.from(date)
        val dayList = ArrayList<SelectDateData>()

        //해당 월의 마지막 날짜 가져오기(결과: 1월이면 31)
        var lastDay = yearMonth.lengthOfMonth()
        //해당 월의 첫번째 날 가져오기(결과: 2023-01-01)
        var firstDay = standardDate.withDayOfMonth(1)
        //첫 번째날 요일 가져오기(결과: 월 ~일이 1~7에 대응되어 나타남)
        var dayOfWeek = firstDay.dayOfWeek.value

        for (i in 1..42) {
            if (dayOfWeek == 7) {//그 달의 첫날이 일요일일때 작동: 한칸 아래줄부터 날짜 표시되는 현상 막기위해
                if (i > lastDay) {
                    break
                } else if (standardDate == selectedDate && i == selectedDate.dayOfMonth) {//선택한 날짜일때 파란 동그라미 표시 위해
                    dayList.add(SelectDateData(LocalDate.of(standardDate.year, standardDate.monthValue, i), true))
                    firstSelectedDatePosition = i - 1
                } else {
                    dayList.add(SelectDateData(LocalDate.of(standardDate.year, standardDate.monthValue, i)))
                }
            } else if (i <= dayOfWeek) { //끝에 빈칸 자르기위해
                dayList.add(SelectDateData(null))
            } else if (i > (lastDay + dayOfWeek)) {//끝에 빈칸 자르기위해
                break
            } else if (standardDate == selectedDate && i - dayOfWeek == selectedDate.dayOfMonth) {//선택한 날짜일때 파란 동그라미 표시 위해
                dayList.add(SelectDateData(LocalDate.of(standardDate.year, standardDate.monthValue, i - dayOfWeek), true))
                firstSelectedDatePosition = i - 1
            } else {
                dayList.add(SelectDateData(LocalDate.of(standardDate.year, standardDate.monthValue, i - dayOfWeek)))//얘만 살리기
            }
        }
        return dayList
    }


    //Calendar rv item클릭 이벤트
    fun calendarRvItemClickEvent(dayList: ArrayList<SelectDateData>) {
        calendarAdapter.setItemClickListener(object : CalendarAdapter.OnItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(selectDateData: SelectDateData, position: Int) {
                //오늘 날짜의 파란 동그라미 해제
                if (firstSelectedDatePosition < dayList.size && dayList[firstSelectedDatePosition].date == selectedDate && dayList[firstSelectedDatePosition].isSelected) {
                    dayList[firstSelectedDatePosition].isSelected = false
                    calendarAdapter.notifyItemChanged(firstSelectedDatePosition)
                }

                selectedDate = selectDateData.date!!
                standardDate = selectedDate

                Toast.makeText(this@MainActivity, ""+selectedDate, Toast.LENGTH_SHORT)
                    .show()

            }
        })
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
}