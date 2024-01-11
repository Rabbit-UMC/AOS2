package com.example.myo_jib_sa.schedule.createScheduleActivity

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.DialogFragmentCalendarBinding
import com.example.myo_jib_sa.schedule.createScheduleActivity.adapter.DialogCalendarAdapter
import com.example.myo_jib_sa.schedule.createScheduleActivity.adapter.SelectDateData
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CalendarDialogFragment(private var setOnClickListener : SetOnClickListener) : DialogFragment() {
    private lateinit var binding : DialogFragmentCalendarBinding
    lateinit var selectedDate : LocalDate //선택한 날짜
    lateinit var standardDate: LocalDate //캘린더 생성하기 위한 기준 날짜, selectedDate업데이트 하면 얘도 같이 업데이트 해주기
    lateinit var dialogCalendarAdapter : DialogCalendarAdapter //calendarRvItemClickEvent() 함수에 사용하기 위해 전역으로 선언
    var firstSelectedDatePosition : Int = 0

    interface SetOnClickListener{
        fun onCompleteClick(selectedDate : LocalDate)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentCalendarBinding.inflate(inflater,container, false)
        // 레이아웃 배경을 투명하게 해줌
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setGravity(Gravity.BOTTOM)
        isCancelable = false //외부 클릭시 dismiss 막기


        //calendar 간격 설정
        binding.calendarRv.addItemDecoration(DialogCalendarAdapter.GridSpaceDecoration(getDisplayWidthSize()))

        //오늘 날짜
        selectedDate = LocalDate.now()
        standardDate = selectedDate
        //화면 초기화
        setMonthView()

        //이전달로 이동
        binding.preMonthBtn.setOnClickListener{
            standardDate = standardDate.minusMonths(1)
            //CoroutineScope(Dispatchers.Main).launch {
            binding.monthTv.text = monthFromDate(standardDate)
            setMonthView()
        }
        //다음달로 이동
        binding.nextMonthBtn.setOnClickListener{
            standardDate =standardDate.plusMonths(1)
            binding.monthTv.text = monthFromDate(standardDate)
            setMonthView()
        }
        binding.calendarCompleteTv.setOnClickListener {
            setOnClickListener.onCompleteClick(selectedDate)
            dismiss()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        resizeDialog()
    }
    //month화면에 보여주기
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMonthView(){

        //month를 month text view에 보여주기 (결과: 1월)
        binding.monthTv.text = monthFromDate(standardDate)
        //year를 year text view에 보여주기 (결과: 2023년)
        binding.yearTv.text = yearFromDate(standardDate)

        //이번달 날짜 가져오기
        val dayList = DayInMonthArray(standardDate)

        //리사이클러뷰 연결
        dialogCalendarAdapter = DialogCalendarAdapter(dayList)
        binding.calendarRv.layoutManager = GridLayoutManager(requireActivity(), 7)
        binding.calendarRv.adapter = dialogCalendarAdapter
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
        dialogCalendarAdapter.setItemClickListener(object : DialogCalendarAdapter.OnItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(selectDateData: SelectDateData, position: Int) {
                //오늘 날짜의 파란 동그라미 해제
                if (firstSelectedDatePosition < dayList.size && dayList[firstSelectedDatePosition].date == selectedDate && dayList[firstSelectedDatePosition].isSelected) {
                    dayList[firstSelectedDatePosition].isSelected = false
                    dialogCalendarAdapter.notifyItemChanged(firstSelectedDatePosition)
                }

                selectedDate = selectDateData.date!!
                standardDate = selectedDate

//                Toast.makeText(requireActivity(), ""+selectedDate, Toast.LENGTH_SHORT)
//                    .show()

            }
        })
    }

    fun resizeDialog() {
        val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        val deviceHeight = size.y

        params?.width = deviceWidth.toInt()

        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    //dp -> px
    fun ConvertDPtoPX(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }

    private fun getDisplayWidthSize(): Int {

        val windowManager = requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val deviceWidth = size.x
        //val deviceHeight = size.y

        return deviceWidth
    }


}