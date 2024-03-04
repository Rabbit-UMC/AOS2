package com.example.myo_jib_sa.mission.dialog

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.sRetrofit
import com.example.myo_jib_sa.databinding.DialogMissionCreateCalendarBinding
import com.example.myo_jib_sa.mission.adapter.MissionCreateCalendarAdapter
import com.example.myo_jib_sa.mission.api.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

class MissionCreateCalendarDialogFragment(private val isStartDate: Boolean, private val date: LocalDate) : BottomSheetDialogFragment() {
    private lateinit var binding : DialogMissionCreateCalendarBinding

    private var dateSelectedListener: OnDateSelectedListener? = null
    fun setDateSelectedListener(listener: OnDateSelectedListener) {
        dateSelectedListener = listener
    }

    lateinit var selectedDate : LocalDate //선택한 날짜
    lateinit var standardDate: LocalDate //캘린더 생성하기 위한 기준 날짜, selectedDate업데이트 하면 얘도 같이 업데이트 해주기
    private var firstSelectedDatePosition : Int = 0

    interface OnDateSelectedListener {
        fun onStartDateSelected(date: LocalDate)
        fun onEndDateSelected(date: LocalDate)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogMissionCreateCalendarBinding.inflate(inflater, container, false)

        //오늘 날짜
        selectedDate = date
        standardDate = selectedDate

        // calendar 간격 설정
        binding.missionCreateCalendarRv.addItemDecoration(
            MissionCreateCalendarAdapter.GridSpaceDecoration(resources.displayMetrics.widthPixels))
        //화면 초기화
        setMonthView()

        // 클릭 리스너 초기화
        initClickListener()

        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initClickListener() {
        // 이전달로 이동
        binding.missionCreatePreMonthBtn.setOnClickListener{
            standardDate = standardDate.minusMonths(1)
            //CoroutineScope(Dispatchers.Main).launch {
            binding.missionCreateSelectedMonthTv.text = monthFromDate(standardDate)
            setMonthView()
        }
        // 다음달로 이동
        binding.missionCreateNextMonthBtn.setOnClickListener{
            standardDate = standardDate.plusMonths(1)
            binding.missionCreateSelectedMonthTv.text = monthFromDate(standardDate)
            setMonthView()
        }
        // 확인 버튼
        binding.missionCreateCalendarCompleteBtn.setOnClickListener {
            if(isStartDate) {
                dateSelectedListener?.onStartDateSelected(selectedDate)
            } else {
                dateSelectedListener?.onEndDateSelected(selectedDate)
            }

            dismiss()
        }
    }


    //month화면에 보여주기
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMonthView(){
        //month를 month text view에 보여주기 (결과: 1월)
        binding.missionCreateSelectedMonthTv.text = monthFromDate(standardDate)
        //year를 year text view에 보여주기 (결과: 2023년)
        binding.missionCreateSelectedYearTv.text = yearFromDate(standardDate)

        //이번달 날짜 가져오기
        val dayList = dayInMonthArray(standardDate)

        // 리사이클러뷰 연결
        val calendarAdapter = MissionCreateCalendarAdapter(dayList)
        binding.missionCreateCalendarRv.apply {
            layoutManager = GridLayoutManager(requireContext(), 7)
            // 날짜 클릭 이벤트
            adapter = calendarAdapter.apply {
                setItemClickListener(object : MissionCreateCalendarAdapter.OnItemClickListener {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onClick(selectDateData: MissionCreateDate, position: Int) {
                        // 빈칸 or 이미 선택한 날짜 클릭 시 아무 동작도 하지 않음
                        if (selectDateData.date == null || selectDateData.date == selectedDate) return

                        // 기존 선택된 날짜 해제
                        if (firstSelectedDatePosition != -1) {
                            dayList[firstSelectedDatePosition].isSelected = false
                            notifyItemChanged(firstSelectedDatePosition)
                        }

                        // 새로 선택된 날짜 업데이트
                        selectDateData.isSelected = true
                        notifyItemChanged(position)
                        firstSelectedDatePosition = position
                        selectedDate = selectDateData.date

                        Log.d("calendarRvItemClickEvent", "$selectedDate")
                    }
                })
            }
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

    //YYYY-MM-DD 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fromDateYYYYMMDD(date: LocalDate?): String? {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date?.format(formatter)
    }

    //날짜 생성: ArrayList<CalendarData>()생성
    @RequiresApi(Build.VERSION_CODES.O)
    private fun dayInMonthArray(date: LocalDate): ArrayList<MissionCreateDate> {
        val yearMonth = YearMonth.from(date)
        val lastDayOfMonth = yearMonth.lengthOfMonth()
        val firstDayOfMonth = yearMonth.atDay(1)
        val dayOfWeekOfFirstDay = firstDayOfMonth.dayOfWeek.value
        val dayList = ArrayList<MissionCreateDate>()

        // 이전 달의 마지막 일자를 채우기 위한 빈 날짜 추가
        val paddingDays = if (dayOfWeekOfFirstDay == 7) 0 else dayOfWeekOfFirstDay
        for (i in 1..paddingDays) {
            dayList.add(MissionCreateDate(null))
        }

        // 현재 달의 날짜 추가
        for (day in 1..lastDayOfMonth) {
            val currentDate = yearMonth.atDay(day)
            val isSelected = currentDate == selectedDate
            dayList.add(MissionCreateDate(currentDate, isSelected))
            if (isSelected) firstSelectedDatePosition = dayList.size - 1
        }

        // 다음 달의 시작 일자를 채우기 위한 빈 날짜 추가
        while (dayList.size < 42) {
            dayList.add(MissionCreateDate(null))
        }

        return dayList
    }

}