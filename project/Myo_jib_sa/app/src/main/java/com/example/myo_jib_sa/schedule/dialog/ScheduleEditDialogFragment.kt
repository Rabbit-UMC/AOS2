package com.example.myo_jib_sa.schedule.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.schedule.api.RetrofitClient
import com.example.myo_jib_sa.schedule.api.currentMission.CurrentMissionResponse
import com.example.myo_jib_sa.schedule.api.currentMission.CurrentMissionResult
import com.example.myo_jib_sa.schedule.api.currentMission.CurrentMissionService
import com.example.myo_jib_sa.databinding.DialogFragmentScheduleEditBinding
import com.example.myo_jib_sa.schedule.adapter.CalendarAdapter
import com.example.myo_jib_sa.schedule.api.scheduleDetail.ScheduleDetailResult
import com.example.myo_jib_sa.schedule.api.scheduleModify.ScheduleModifyRequest
import com.example.myo_jib_sa.schedule.api.scheduleModify.ScheduleModifyResponse
import com.example.myo_jib_sa.schedule.api.scheduleModify.ScheduleModifyService
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

class ScheduleEditDialogFragment : DialogFragment() {
    private lateinit var binding: DialogFragmentScheduleEditBinding
    private var scheduleData : ScheduleDetailResult = ScheduleDetailResult(
        scheduleId = 0,
        missionId = 0,
        missionTitle = "",
        scheduleTitle= "",
        startAt= "",
        endAt= "",
        content= "",
        scheduleWhen= ""
    )
    private val tabLayoutText = listOf("시작 시간", "종료 시간")
    lateinit var selectedDate : LocalDate //선택한 날짜
    lateinit var standardDate: LocalDate //캘린더 생성하기 위한 기준 날짜, selectedDate업데이트 하면 얘도 같이 업데이트 해주기
    lateinit var calendarAdapter : DialogCalendarAdapter
    var firstSelectedDatePosition : Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentScheduleEditBinding.inflate(inflater, container, false)

        setCurrentDialog()//ScheduleDetailDialog에서 보낸 데이터 바인딩하기

        // 레이아웃 배경을 투명하게 해줌
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        setButton() //버튼 클릭 sest

        //일정 제목 특수문자 제어
        binding.scheduleTitleEtv.filters = arrayOf(editTextFilter)
        //현재 미션 표시
        currentMissionApi()
        //calendar 간격 설정
        binding.calendarRv.addItemDecoration(DialogCalendarAdapter.GridSpaceDecoration(getDisplayWidthSize()))
        setCalendar()

        //viewPager세팅
        binding.timeViewPager.adapter = TimeViewPagerAdapter(requireActivity())
        //tab layout 세팅
        TabLayoutMediator(binding.timeTablayout, binding.timeViewPager){tab, pos->
            tab.text=tabLayoutText[pos]
        }.attach()

       return binding.root
    }

    override fun onResume() {
        super.onResume()
        resizeDialog()
    }

    private fun setDialogMissionAdapter(missionList: List<CurrentMissionResult>){
        var dialogMissionAdapter = DialogMissionAdapter(missionList)
        binding.missionRv.adapter = dialogMissionAdapter
        binding.missionRv.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        dialogMissionAdapter.setItemClickListener(object : DialogMissionAdapter.OnItemClickListener{
            override fun onClick(data: CurrentMissionResult) {
                binding.missionTitleTv.text = data.missionTitle
                scheduleData.missionTitle = data.missionTitle
                scheduleData.missionId = data.missionId
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setCalendar(){
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
        binding.selectedMonthTv.text = monthFromDate(standardDate)//결과: 1월
        binding.selectedYearTv.text = yearFromDate(standardDate)//결과: 2023년

        //이번달 날짜 가져오기
        val dayList = DayInMonthArray(standardDate)

        //리사이클러뷰 연결
        calendarAdapter = DialogCalendarAdapter(dayList)
        binding.calendarRv.layoutManager = GridLayoutManager(requireContext(), 7)
        binding.calendarRv.adapter = calendarAdapter
        calendarRvItemClickEvent(dayList)
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
        calendarAdapter.setItemClickListener(object : DialogCalendarAdapter.OnItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(selectDateData: SelectDateData, position: Int) {
                //오늘 날짜의 파란 동그라미 해제
                if (firstSelectedDatePosition < dayList.size && dayList[firstSelectedDatePosition].date == selectedDate && dayList[firstSelectedDatePosition].isSelected) {
                    dayList[firstSelectedDatePosition].isSelected = false
                    calendarAdapter.notifyItemChanged(firstSelectedDatePosition)
                }

                selectedDate = selectDateData.date!!
                standardDate = selectedDate

                Toast.makeText(requireContext(), ""+selectedDate, Toast.LENGTH_SHORT)
                    .show()

            }
        })
    }

    private fun getDisplayWidthSize(): Int {

        val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val deviceWidth = size.x
        //val deviceHeight = size.y
        return deviceWidth
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

    //ScheduleDetailDialog에서 보낸 데이터 바인딩하기
    private fun setCurrentDialog() {

//        val bundle = arguments
//        var isEdit = bundle?.getBoolean("isEdit", false)
//
//        if (bundle != null && isEdit == true) {//수정한 값이 있다면
//            Log.d("debug", "isEdit: "+"받았다!"+bundle.getBoolean("isEdit"))
//            val sharedPreferenceModified = requireContext().getSharedPreferences("scheduleModifiedData",
//                Context.MODE_PRIVATE
//            )
//            scheduleData?.scheduleWhen = sharedPreferenceModified.getString("scheduleDate", "").toString()
//            scheduleData?.missionTitle = sharedPreferenceModified.getString("missionTitle", "").toString()
//            scheduleData?.missionId = sharedPreferenceModified.getLong("missionId", -1)
//            scheduleData?.startAt = sharedPreferenceModified.getString("scheduleStartTime", "").toString()
//            scheduleData?.endAt = sharedPreferenceModified.getString("scheduleEndTime", "").toString()
//        }
        //else{//아니면 원래 저장 값으로
        val sharedPreference =
            requireContext().getSharedPreferences("scheduleData", Context.MODE_PRIVATE)
        //Log.d("debug", "isEdit: "+"받았다!"+isEdit)

        scheduleData?.scheduleWhen = sharedPreference.getString("scheduleDate", "").toString()
        scheduleData?.missionTitle = sharedPreference.getString("missionTitle", "").toString()
        scheduleData?.missionId = sharedPreference.getLong("missionId", -1)
        scheduleData?.startAt = sharedPreference.getString("scheduleStartTime", "").toString()
        scheduleData?.endAt = sharedPreference.getString("scheduleEndTime", "").toString()
        //}

//        val sharedPreference = requireContext().getSharedPreferences("scheduleData",
//            Context.MODE_PRIVATE
//        )
        scheduleData?.scheduleTitle = sharedPreference.getString("scheduleTitle", "").toString()
        scheduleData?.content = sharedPreference.getString("scheduleMemo", "").toString()
        scheduleData?.scheduleId = sharedPreference.getLong("scheduleId", 0)
        Log.d("debug", "EditDialog$scheduleData")

        Log.d("Datedebug", "EditSetCurrentDialog = ${scheduleData?.scheduleWhen}")


        //화면에 반영
        binding.scheduleTitleEtv.setText(scheduleData?.scheduleTitle)
        binding.scheduleDateTv.text = scheduleData?.scheduleWhen
        binding.missionTitleTv.text = scheduleData?.missionTitle
        binding.scheduleStartAtTv.text = scheduleTimeFormatter(scheduleData?.startAt)
        binding.scheduleEndAtTv.text = scheduleTimeFormatter(scheduleData?.endAt)
        binding.scheduleMemoEtv.setText(scheduleData?.content)

    }


    private fun setButton(){
        //수정완료 버튼
        binding.modifyBtn.setOnClickListener{
            saveData()//scheduleData&sharedPreference에 저장
            buttonClickListener.onClickEditBtn(scheduleData)
            scheduleModifyApi()//수정완료 누르면 데이터 서버로 보내기
            dismiss()
        }

        //미션 제목 클릭시
        binding.missionTitleTv.setOnClickListener {
            if(binding.missionRv.visibility == View.VISIBLE)
                binding.missionRv.visibility = View.GONE
            else
                binding.missionRv.visibility = View.VISIBLE
            //setSpinnerDialog(0)
        }
        //날짜 클릭시
        binding.scheduleDateTv.setOnClickListener {
            if(binding.calendarLayout.visibility == View.GONE){
                binding.calendarLayout.visibility = View.VISIBLE
                binding.scheduleMemoEtv.visibility = View.GONE
            }
        }
        binding.calendarCompleteTv.setOnClickListener {
            binding.calendarLayout.visibility = View.GONE
            binding.scheduleMemoEtv.visibility = View.VISIBLE
        }
        //시간 클릭시
        binding.scheduleStartAtTv.setOnClickListener {
            if(binding.timeLayout.visibility == View.GONE)
                binding.timeLayout.visibility = View.VISIBLE
            binding.timeViewPager.currentItem = 0
        }
        binding.scheduleEndAtTv.setOnClickListener {
            if(binding.timeLayout.visibility == View.GONE)
                binding.timeLayout.visibility = View.VISIBLE
            binding.timeViewPager.currentItem = 1
        }
        binding.timeApplyBtn.setOnClickListener {
            binding.timeLayout.visibility = View.GONE
        }
    }

    //scheduleData&sharedPreference에 저장
    private fun saveData(){
        scheduleData.scheduleTitle = binding.scheduleTitleEtv.text.toString()
        scheduleData.content = binding.scheduleMemoEtv.text.toString()

        val sharedPreference = requireContext().getSharedPreferences("scheduleData",Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("scheduleTitle", scheduleData.scheduleTitle)
        editor.putString("scheduleDate", scheduleData.scheduleWhen)
        editor.putString("missionTitle", scheduleData.missionTitle)
        editor.putString("scheduleStartTime", scheduleData.startAt)
        editor.putString("scheduleEndTime", scheduleData.endAt)
        editor.putString("scheduleMemo", scheduleData.content)
        if(scheduleData.missionId == null){
            editor.putLong("missionId", -1)
        }else{
            editor.putLong("missionId", scheduleData.missionId!!)
        }
        editor.putLong("scheduleId", scheduleData.scheduleId)
        editor.apply()

        Log.d("Datedebug", "EditSaveData = ${scheduleData?.scheduleWhen}")


    }

    private fun setSpinnerDialog(position:Int){
        //sharedPreference저장
        val sharedPreference = requireContext().getSharedPreferences("scheduleData",
            Context.MODE_PRIVATE
        )
        val editor = sharedPreference.edit()
        editor.putString("scheduleTitle", binding.scheduleTitleEtv.text.toString())
        editor.putString("scheduleDate", binding.scheduleDateTv.text.toString())
        editor.putString("missionTitle", binding.missionTitleTv.text.toString())
        editor.putString("scheduleStartTime", scheduleData?.startAt)
        editor.putString("scheduleEndTime", scheduleData?.endAt)
        editor.putString("scheduleMemo", binding.scheduleMemoEtv.text.toString())
        if(scheduleData.missionId == null){
            editor.putLong("missionId", -1)
        }else{
            editor.putLong("missionId", scheduleData.missionId!!)
        }
        editor.putLong("scheduleId", scheduleData!!.scheduleId)
        editor.apply()

        Log.d("Datedebug", "EditSetSpinnerDialog = ${scheduleData?.scheduleWhen}")


        var bundle = Bundle()
        bundle.putInt("position", position)

        val scheduleSpinnerDialogFragment = ScheduleSpinnerDialogFragment()
        scheduleSpinnerDialogFragment.arguments = bundle
        scheduleSpinnerDialogItemClickEvent(scheduleSpinnerDialogFragment)//scheduleSpinnerDialogFragment Item클릭 이벤트 setting
        scheduleSpinnerDialogFragment.show(requireActivity().supportFragmentManager, "ScheduleEditDialog")

        //dismiss()//editDialog종료
    }

    private fun scheduleSpinnerDialogItemClickEvent(dialog: ScheduleSpinnerDialogFragment){
        dialog.setButtonClickListener(object: ScheduleSpinnerDialogFragment.OnButtonClickListener{
            override fun onClickCompeleteBtn(isEdit: Boolean) {
                if (isEdit) {//수정한 값이 있다면
                    //Log.d("debug", "isEdit: "+"받았다!"+bundle.getBoolean("isEdit"))
                    val sharedPreferenceModified = requireContext().getSharedPreferences("scheduleModifiedData",
                        Context.MODE_PRIVATE
                    )
                    scheduleData?.scheduleWhen = sharedPreferenceModified.getString("scheduleDate", "").toString()
                    scheduleData?.missionTitle = sharedPreferenceModified.getString("missionTitle", "").toString()
                    scheduleData?.missionId = sharedPreferenceModified.getLong("missionId", -1)
                    scheduleData?.startAt = sharedPreferenceModified.getString("scheduleStartTime", "").toString()
                    scheduleData?.endAt = sharedPreferenceModified.getString("scheduleEndTime", "").toString()


                    Log.d("Datedebug", "EditCallback = ${scheduleData?.scheduleWhen}")

                    //화면에 반영
                    binding.scheduleDateTv.text = scheduleData?.scheduleWhen
                    binding.missionTitleTv.text = scheduleData?.missionTitle
                    binding.scheduleStartAtTv.text = scheduleTimeFormatter(scheduleData?.startAt)
                    binding.scheduleEndAtTv.text = scheduleTimeFormatter(scheduleData?.endAt)
                }
            }
        })
    }

    //currentMission api연결
    private fun currentMissionApi() {
        val sharedPreferences =
            requireContext().getSharedPreferences("getJwt", Context.MODE_PRIVATE)
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

    //scheduleModify api연결
    private fun scheduleModifyApi() {
        // SharedPreferences 객체 가져오기
        val sharedPreferences = requireContext().getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        // JWT 값 가져오기
        val token = sharedPreferences.getString("jwt", null)

        //val token : String = BuildConfig.API_TOKEN
//        Log.d("retrofit", "token = "+token+"l");
//
        if(scheduleData.missionId == -1L){
            scheduleData.missionId = null
        }

        val requestBody = ScheduleModifyRequest(
            title = binding.scheduleTitleEtv.text.toString(),
            content = binding.scheduleMemoEtv.text.toString() ,//메모
            startAt = scheduleData.startAt,
            endAt = scheduleData.endAt,
            missionId = scheduleData.missionId,
            scheduleWhen = binding.scheduleDateTv.text.toString()
        )

        Log.d("debug", requestBody.toString())
        val service = RetrofitClient.getInstance().create(ScheduleModifyService::class.java)
        val listCall = service.scheduleModify(token, scheduleData.scheduleId, requestBody)

        listCall.enqueue(object : Callback<ScheduleModifyResponse> {
            override fun onResponse(
                call: Call<ScheduleModifyResponse>,
                response: Response<ScheduleModifyResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("retrofit", response.body().toString());
                }else {
                    Log.e("retrofit", "onResponse: Error ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("retrofit", "onResponse: Error Body $errorBody")
                }}
            override fun onFailure(call: Call<ScheduleModifyResponse>, t: Throwable) {
                Log.e("retrofit", "onFailure: ${t.message}")
            }
        })
    }



    //scheduleFragment에 값을 넘겨주기 위한 인터페이스
    interface OnButtonClickListener {
        fun onClickEditBtn(scheduleData:ScheduleDetailResult)
    }
    // 클릭 이벤트 설정
    fun setButtonClickListener(buttonClickListener: OnButtonClickListener) {
        this.buttonClickListener = buttonClickListener
    }
    // 클릭 이벤트 실행
    private lateinit var buttonClickListener: OnButtonClickListener
    //====================================================================


    //dialog크기 조절
    fun resizeDialog() {
        val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        val deviceHeight = size.y

        var height = (deviceWidth * 0.95 * 1.13).toInt()
//        var minHeight = ConvertDPtoPX(requireContext(), 380)
//        if(minHeight > height){
//            params?.height = minHeight
//        } else{
//            params?.height = height
//        }
        params?.width = (deviceWidth * 0.89).toInt()
//        params?.height = (deviceWidth * 0.89 * 1.08).toInt()

        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    //dp -> px
    fun ConvertDPtoPX(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
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

    //startTime, endTime 포맷
    private fun scheduleTimeFormatter(startAt: String?): String {
        val formatter = DecimalFormat("00")

        val time = startAt!!.split(":")
        val hour = time[0].toInt()
        val minute = time[1].toInt()

        if (hour < 12) {
            return "오전 ${formatter.format(hour)}:${formatter.format(minute)}"
        } else {
            if (hour == 12)
                return "오후 ${formatter.format(hour)}:${formatter.format(minute)}"
            else
                return "오후 ${formatter.format(hour - 12)}:${formatter.format(minute)}"
        }
    }

}