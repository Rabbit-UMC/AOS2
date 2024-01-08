package com.example.myo_jib_sa.schedule


import SwipeHelperCallback
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*

import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myo_jib_sa.BuildConfig
import com.example.myo_jib_sa.schedule.api.RetrofitClient
import com.example.myo_jib_sa.schedule.api.currentMission.CurrentMissionResponse
import com.example.myo_jib_sa.schedule.api.currentMission.CurrentMissionResult
import com.example.myo_jib_sa.schedule.api.currentMission.CurrentMissionService
import com.example.myo_jib_sa.schedule.api.scheduleDelete.ScheduleMonthService
import com.example.myo_jib_sa.schedule.api.scheduleMonth.ScheduleMonthResponse
import com.example.myo_jib_sa.schedule.api.scheduleOfDay.ScheduleOfDayResponse
import com.example.myo_jib_sa.schedule.api.scheduleOfDay.ScheduleOfDayResult
import com.example.myo_jib_sa.schedule.api.scheduleOfDay.ScheduleOfDayService
import com.example.myo_jib_sa.schedule.adapter.*
import com.example.myo_jib_sa.schedule.createScheduleActivity.CreateScheduleActivity
import com.example.myo_jib_sa.schedule.currentMissionActivity.CurrentMissionActivity
import com.example.myo_jib_sa.schedule.dialog.ScheduleDeleteDialogFragment
import com.example.myo_jib_sa.schedule.dialog.ScheduleDetailDialogFragment
import com.example.myo_jib_sa.databinding.FragmentScheduleBinding
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter


class ScheduleFragment() : Fragment() {

    lateinit var binding: FragmentScheduleBinding
    lateinit var calendarAdapter: CalendarAdapter //calendarRvItemClickEvent() 함수에 사용하기 위해 전역으로 선언
    lateinit var scheduleAdaptar: ScheduleAdaptar //scheduleRvItemClickEvent() 함수에 사용하기 위해 전역으로 선언
    lateinit var scheduleDetailDialog: ScheduleDetailDialogFragment
    lateinit var selectedDate: LocalDate //오늘 날짜
    lateinit var standardDate: LocalDate //캘린더의 기준 날짜, selectedDate업데이트 하면 얘도 같이 업데이트 해주기
    var firstSelectedDatePosition: Int = -1


    var mDataList = ArrayList<CurrentMissionResult>() //미션 리스트 데이터
    var sDataList = ArrayList<ScheduleOfDayResult>() //일정 리스트 데이터

    private var adLoader: AdLoader? = null //광고를 불러올 adLoader 객체

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduleBinding.inflate(inflater, container, false)

        //calendar 간격 설정
        binding.calendarRv.addItemDecoration(CalendarAdapter.GridSpaceDecoration(getDisplayWidthSize()))

        //애드몹 광고 표시
        createAd()
        adLoader?.loadAd(AdRequest.Builder().build())

        //오늘 날짜 가져오기
        selectedDate = LocalDate.now()
        standardDate = selectedDate
        //m월 d일 일정 표시 (예: 6월 1일 일정 표시)
        binding.selectedMonthDayTv.text = MMDDFromDate(selectedDate)
        //YYYY년 표시 - calendar
        binding.selectedYearTv.text = yearFromDate(selectedDate)
        //MM월 표시 - calendar
        binding.selectedMonthTv.text = monthFromDate(selectedDate)


        //화면전환
        switchScreen()
        //캘린더 관련 모든 버튼
        calenderBtn()
        //scheduleDetailDialog 설정
        scheduleDetailDialog = ScheduleDetailDialogFragment(requireContext())

        return binding.root
    }


    //화면 reload 기능: 기본적인 데이터 세팅
    //화면이 사용자와 상호작용할수 있는 상태일때 (=화면이 눈에 보일때
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        Log.d("debug", "onResume")
        currentMissionApi()//currentMission api연결

        //ScheduleAdaptar 리사이클러뷰 연결
        setScheduleAdapter(standardDate)
        scheduleRvItemClickEvent()//Schedule rv item클릭 이벤트

        CoroutineScope(Dispatchers.Main).launch {
            delay(50)
            scheduleMonthApi()//달력 초기화
            scheduleOfDayApi(standardDate)//scheduleOfDay api연결
        }
    }


    //month화면에 보여주기
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setCalendarHeader(date: LocalDate) {

        //m월 d일 일정 표시 (예: 6월 1일 일정 표시)
        binding.selectedMonthDayTv.text = MMDDFromDate(date)
        //YYYY년 표시 - calendar
        binding.selectedYearTv.text = yearFromDate(date)
        //MM월 표시 - calendar
        binding.selectedMonthTv.text = monthFromDate(date)

        //일정 있는지 없는지 api로 체크
        //<api 안에서>
        // -> val dayList = dayInMonthArray(standardDate)로 월별 날짜 불러오기
        //CalendarAdapter리사이클러뷰 연결
        //calendarRvItemClickEvent() //Calendar rv item클릭 이벤트
//        setHasScheduleMap(standardDate)
    }


    //날짜 생성: ArrayList<CalendarData>()생성
    @RequiresApi(Build.VERSION_CODES.O)
    private fun DayInMonthArray(): ArrayList<CalendarData> {
        var yearMonth = YearMonth.from(standardDate)
        val dayList = ArrayList<CalendarData>()

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
                    //dayList.add(CalendarData(null)) //끝에 빈칸 자르기
                } else if (standardDate == selectedDate && i == selectedDate.dayOfMonth) {//선택한 날짜일때 파란 동그라미 표시 위해
                    var currentDate = YYYYMMDDFromDate(
                        LocalDate.of(
                            standardDate.year, standardDate.monthValue, i
                        )
                    )
                    dayList.add(
                        CalendarData(
                            LocalDate.of(
                                standardDate.year, standardDate.monthValue, i
                            ), scheduleCntMap["$i"], true
                        )
                    )
                    firstSelectedDatePosition = i - 1
                } else {
                    var currentDate = YYYYMMDDFromDate(
                        LocalDate.of(standardDate.year, standardDate.monthValue, i)
                    )
                    dayList.add(
                        CalendarData(
                            LocalDate.of(
                                standardDate.year, standardDate.monthValue, i
                            ), scheduleCntMap["$i"]
                        )
                    )
                }
            } else if (i <= dayOfWeek) { //끝에 빈칸 자르기위해
                dayList.add(CalendarData(null))
            } else if (i > (lastDay + dayOfWeek)) {//끝에 빈칸 자르기위해
                break
            } else if (standardDate == selectedDate && i - dayOfWeek == selectedDate.dayOfMonth) {//선택한 날짜일때 파란 동그라미 표시 위해
                var currentDate = YYYYMMDDFromDate(
                    LocalDate.of(standardDate.year, standardDate.monthValue, i - dayOfWeek)
                )
                dayList.add(
                    CalendarData(
                        LocalDate.of(
                            standardDate.year, standardDate.monthValue, i - dayOfWeek
                        ), scheduleCntMap["${i-dayOfWeek}"], true
                    )
                )
                firstSelectedDatePosition = i - 1
            } else {
                var currentDate = YYYYMMDDFromDate(
                    LocalDate.of(standardDate.year, standardDate.monthValue, i - dayOfWeek)
                )
                dayList.add(
                    CalendarData(
                        LocalDate.of(
                            standardDate.year, standardDate.monthValue, i - dayOfWeek
                        ), scheduleCntMap["${i-dayOfWeek}"]
                    )
                )//얘만 살리기
            }
        }
        return dayList
    }


    //광고 생성 메소드
    private fun createAd() {
        MobileAds.initialize(requireActivity())
        adLoader = AdLoader.Builder(requireActivity(), BuildConfig.AD_UNIT_ID)//sample아이디
            .forNativeAd { ad: NativeAd ->
                val template: TemplateView = binding.myTemplate
                template.setNativeAd(ad)
            }.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Handle the failure by logging, altering the UI, and so on.
                }
            }).withNativeAdOptions(
                NativeAdOptions.Builder()
                    // Methods in the NativeAdOptions.Builder class can be
                    // used here to specify individual options settings.
                    .build()
            ).build()
    }


    //CurrentMissionAdapter 리사이클러뷰 연결
    private fun setCurrentMissionAdapter() {
        val currentMissionAdapter = CurrentMissionAdapter(mDataList)
        binding.currentMissionRv.layoutManager = LinearLayoutManager(
            activity, LinearLayoutManager.HORIZONTAL, false
        )
        binding.currentMissionRv.adapter = currentMissionAdapter
    }


    //setScheduleAdapterAdapter 리사이클러뷰 연결
    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setScheduleAdapter(date: LocalDate?) {

        Log.d("retrofit", "$date : $sDataList")
        scheduleAdaptar = ScheduleAdaptar(sDataList)
        val item = CustomItemDecoration(requireContext())

        // 리사이클러뷰에 스와이프, 드래그 기능 달기
        val swipeHelperCallback = SwipeHelperCallback().apply {
            // 스와이프한 뒤 고정시킬 위치 지정
            setClamp(requireContext(), 100f)//100dp만큼 이동
        }
        val itemTouchHelper = ItemTouchHelper(swipeHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.scheduleRv)

        binding.scheduleRv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = scheduleAdaptar
//            addItemDecoration(itemDecoration)
            addItemDecoration(CustomItemDecoration(requireActivity()))

            setOnTouchListener { _, _ ->
                swipeHelperCallback.removePreviousClamp(this)
                false
            }
        }

        //noScheduleIv의 visibility설정
        if (sDataList.size == 0) binding.noSchedule.visibility = View.VISIBLE
        else binding.noSchedule.visibility = View.GONE


        //캘린더 클릭할 때 마다 일정리스트가 다시 set되고, 따라서 item클릭 이벤트도 다시 연결해 주어야 함함
        scheduleRvItemClickEvent()//Schedule rv item클릭 이벤트
    }


    //Calendar rv item클릭 이벤트
    fun calendarRvItemClickEvent(dayList: ArrayList<CalendarData>) {
        calendarAdapter.setItemClickListener(object : CalendarAdapter.OnItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(calendarData: CalendarData) {
                //가장 처음 선택한 날짜의 파란 동그라미 해제
                if (firstSelectedDatePosition < dayList.size && dayList[firstSelectedDatePosition].date == selectedDate && dayList[firstSelectedDatePosition].isSelected) {
                    dayList[firstSelectedDatePosition].isSelected = false
                    calendarAdapter.notifyItemChanged(firstSelectedDatePosition)
                }

                selectedDate = calendarData.date!!
                standardDate = selectedDate

                binding.selectedYearTv.text = yearFromDate(selectedDate)//ex. 2023년
                binding.selectedMonthTv.text = monthFromDate(selectedDate)//ex. 6월
                binding.selectedMonthDayTv.text = MMDDFromDate(selectedDate)//ex. 6월 1일

                scheduleOfDayApi(selectedDate)//scheduleOfDay api연결
            }
        })
    }

    //Schedule rv item클릭 이벤트
    fun scheduleRvItemClickEvent() {
        scheduleAdaptar.setItemClickListener(object : ScheduleAdaptar.OnItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(scheduleData: ScheduleOfDayResult) {
                // 클릭 시 이벤트 작성
                var bundle = Bundle()
                bundle.putLong("scheduleId", scheduleData.scheduleId)
                Log.d("debug", "\"scheduleId\", ${scheduleData.scheduleId}")
                scheduleDetailDialog.arguments = bundle

                scheduleDetailDialogItemClickEvent(scheduleDetailDialog)//scheduleDetailDialog Item클릭 이벤트 setting
                scheduleDetailDialog.show(
                    requireActivity().supportFragmentManager, "ScheduleDetailDialog"
                )
            }

            override fun onDeleteClick(scheduleId: Long) {
                var bundle = Bundle()
                var scheduleDeleteDialog = ScheduleDeleteDialogFragment(
                    binding.scheduleRv.adapter as ScheduleAdaptar, scheduleId
                )
                scheduleDeleteDialog.setButtonClickListener(object :
                    ScheduleDeleteDialogFragment.OnButtonClickListener {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onClickExitBtn() {
                        scheduleMonthApi()//달력 초기화
                        scheduleOfDayApi(selectedDate)//scheduleOfDay api연결
                    }
                })
                scheduleDeleteDialog.arguments = bundle
                scheduleDeleteDialog.show(
                    requireActivity().supportFragmentManager, "scheduleDeleteDialog"
                )

            }
        })
    }

    //scheduleDetailDialog Item클릭 이벤트
    fun scheduleDetailDialogItemClickEvent(dialog: ScheduleDetailDialogFragment) {
        dialog.setButtonClickListener(object : ScheduleDetailDialogFragment.OnButtonClickListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun whenDismiss() {
                //화면reload===============================================

                currentMissionApi()//scheduleHome api연결
                scheduleMonthApi()//달력 초기화

                //CurrentMissionAdapter,ScheduleAdaptar 리사이클러뷰 연결
                setCurrentMissionAdapter()

                scheduleOfDayApi(standardDate)//scheduleOfDay api연결
                //화면reload===============================================
            }
        })
    }


    //화면전환 메소드
    fun switchScreen() {
        //history누르면 HistoryActivity로 화면 전환
//        binding.historyTv.setOnClickListener{
//            var historyIntent = Intent(requireActivity(), SuccessMissionActivity::class.java)
//            startActivity(historyIntent)
//        }
        //미션리스트 위에 모두보기 누르면 CurrentMissionActivity로 화면 전환
        binding.viewAllTv.setOnClickListener {
            var missionIntent = Intent(requireActivity(), CurrentMissionActivity::class.java)
            startActivity(missionIntent)
        }
        //floating button누르면 CreateScheduleActivity로 화면 전환
        binding.newScheduleFloatingBtn.setOnClickListener {
            var createSIntent = Intent(requireActivity(), CreateScheduleActivity::class.java)
            startActivity(createSIntent)
        }
    }

    //캘린더 관련 버튼
    @RequiresApi(Build.VERSION_CODES.O)
    private fun calenderBtn() {

        //month-day 어제로 이동
        binding.yesterdayBtn.setOnClickListener {
            selectedDate = standardDate.minusDays(1)
            standardDate = selectedDate
            setCalendarHeader(standardDate)
            scheduleMonthApi()
            scheduleOfDayApi(standardDate)
        }

        //month-day 내일로 이동
        binding.tomorrowBtn.setOnClickListener {
            selectedDate = standardDate.plusDays(1)
            standardDate = selectedDate
            setCalendarHeader(standardDate)
            scheduleMonthApi()
            scheduleOfDayApi(standardDate)
        }

        //캘린더 visible버튼
        binding.calendarVisibleBtn.setOnClickListener {
            binding.calendarLayout.visibility = View.VISIBLE
            binding.calendarMonthDayHeader.visibility = View.GONE
            standardDate = selectedDate
            setCalendarHeader(standardDate)
            scheduleMonthApi()
        }

        //캘린더 gone 버튼
        binding.calendarGoneBtn.setOnClickListener {
            binding.calendarLayout.visibility = View.GONE
            binding.calendarMonthDayHeader.visibility = View.VISIBLE
        }

        //이전달로 이동
        binding.preMonthBtn.setOnClickListener {
            standardDate = standardDate.minusMonths(1)
            binding.selectedMonthTv.text = monthFromDate(standardDate)
            binding.selectedYearTv.text = yearFromDate(standardDate)
            scheduleMonthApi()
        }
        //다음달로 이동
        binding.nextMonthBtn.setOnClickListener {
            standardDate = standardDate.plusMonths(1)
            binding.selectedMonthTv.text = monthFromDate(standardDate)
            binding.selectedYearTv.text = yearFromDate(standardDate)
            scheduleMonthApi()
        }
    }

    //currentMission api연결
    private fun currentMissionApi() {
        val sharedPreferences =
            requireContext().getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", null)



        mDataList.clear()//mDataList초기화

        val service = RetrofitClient.getInstance().create(CurrentMissionService::class.java)
        val listCall = service.currentMission(token)
        Log.d("retrofit", "token="+token )
        listCall.enqueue(object : Callback<CurrentMissionResponse> {
            override fun onResponse(
                call: Call<CurrentMissionResponse>, response: Response<CurrentMissionResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("retrofit", "currentMissionApi " + response.body().toString());
                    val missionList = response.body()?.result

                    //현재 미션 데이터 리스트 리사이클러뷰 연결
                    //디데이 얼마 안남은 미션부터 많이 남은 순으로 정렬돼 있음
                    if (missionList != null) {
                        for (i in 0 until missionList!!.size) {
                            mDataList.add(
                                CurrentMissionResult(
                                    missionList[i].missionId,
                                    missionList[i].missionTitle,
                                    missionList[i].challengerCnt,
                                    missionList[i].categoryId,
                                    missionList[i].image,
                                    missionList[i].dday
                                )
                            )
                        }
                    }
                    setCurrentMissionAdapter()

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

    //scheduleOfDay api연결
    //calendarRvItemClickEvent()안에서만 실행
    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleOfDayApi(date: LocalDate) {
        // JWT 값 가져오기
        val sharedPreferences =
            requireContext().getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", null)


        sDataList.removeAll(sDataList.toSet())//초기화

        val service = RetrofitClient.getInstance().create(ScheduleOfDayService::class.java)
        val listCall = service.scheduleOfDay(token, YYYYMMDDFromDate(date))

        listCall.enqueue(object : Callback<ScheduleOfDayResponse> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<ScheduleOfDayResponse>, response: Response<ScheduleOfDayResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("retrofit", "scheduleOfDayApi_" + response.body().toString());
                    val scheduleList = response.body()?.result

                    if(scheduleList!=null) {
                        //일정 데이터 리스트 sDataList에 추가
                        for (i in 0 until scheduleList!!.size) {
                            sDataList.add(
                                ScheduleOfDayResult(
                                    scheduleList[i].scheduleId,
                                    scheduleList[i].scheduleTitle,
                                    scheduleList[i].scheduleStart,
                                    scheduleList[i].scheduleEnd,
                                    scheduleList[i].scheduleWhen
                                )
                            )
                        }
                    }
                    //if(binding.F.visibility == View.VISIBLE)
                    setScheduleAdapter(date)
                } else {
                    Log.e("retrofit", "scheduleOfDayApi_onResponse: Error ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("retrofit", "scheduleOfDayApi_onResponse: Error Body $errorBody")
                }
            }

            override fun onFailure(call: Call<ScheduleOfDayResponse>, t: Throwable) {
                Log.e("retrofit", "scheduleOfDayApi_onFailure: ${t.message}")
            }
        })
    }


    private var hasScheduleMap: HashMap<String?, Boolean> = HashMap()
    private lateinit var scheduleCntMap : Map<String, Int>

    //scheduleMonthApi 연결: 스케줄 있는지 없는지 체크 | standardDate기준으로 달력 생성
    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleMonthApi() {
        var yyyyMM = YYYY_MMFromDate(standardDate)
        Log.d("retrofit", "date" + standardDate)
        Log.d("retrofit", "yyyyMM" + yyyyMM)

        //해당 월의 마지막 날짜 가져오기(결과: 1월이면 31)
        var yearMonth = YearMonth.from(standardDate)
        var lastDay = yearMonth.lengthOfMonth()

        scheduleCntMap = HashMap<String, Int>()
//        //hasScheduleMap초기화
//        for (j in 1..lastDay) {
//            val formatter = DecimalFormat("00")
//            hasScheduleMap["$yyyyMM-${formatter.format(j)}"] = false
//        }

        // JWT 값 가져오기
        val sharedPreferences =
            requireActivity().getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", "")

        val service = RetrofitClient.getInstance().create(ScheduleMonthService::class.java)
        val listCall = service.scheduleMonth(token, yyyyMM)

        listCall.enqueue(object : Callback<ScheduleMonthResponse> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<ScheduleMonthResponse>, response: Response<ScheduleMonthResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("retrofit", response.body().toString());
                    val scheduleList = response.body()?.result?.schedulesOfDay

                    if(scheduleList != null) {//스케줄이 있는 달일때만 map에 값 넣어주기
                        scheduleCntMap = scheduleList!!

                        for (i in 0 until scheduleList!!.size) {
//                        hasScheduleMap["$yyyyMM-${formatter.format(scheduleList[i])}"] = true
                        }
                    }

                    val dayList = DayInMonthArray()

                    //CalendarAdapter리사이클러뷰 연결
                    calendarAdapter = CalendarAdapter(dayList)
                    binding.calendarRv.layoutManager = GridLayoutManager(activity, 7)
                    binding.calendarRv.adapter = calendarAdapter

                    calendarRvItemClickEvent(dayList)//Calendar rv item클릭 이벤트

                } else {
                    Log.e("retrofit", "scheduleMonthApi_onResponse: Error ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("retrofit", "scheduleMonthApi_onResponse: Error Body $errorBody")
                }
            }

            override fun onFailure(call: Call<ScheduleMonthResponse>, t: Throwable) {
                Log.e("retrofit", "scheduleMonthApi_onFailure: ${t.message}")
            }
        })
    }


    //YYYY 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    private fun yearFromDate(date: LocalDate?): String? {
        var formatter = DateTimeFormatter.ofPattern("yyyy")
        return date?.format(formatter)
    }

    //M월 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    private fun monthFromDate(date: LocalDate?): String? {
        var formatter = DateTimeFormatter.ofPattern("M월")
        return date?.format(formatter)
    }

    //YYYY-MM 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    private fun YYYY_MMFromDate(date: LocalDate?): String? {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM")
        return date?.format(formatter)
    }

    //M월 D일 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    private fun MMDDFromDate(date: LocalDate?): String? {
        var formatter = DateTimeFormatter.ofPattern("M월 d일")
        return date?.format(formatter)
    }

    //YYYY-MM-DD 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    private fun YYYYMMDDFromDate(date: LocalDate?): String? {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date?.format(formatter)
    }

    private fun getDisplayWidthSize(): Int {

        val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val deviceWidth = size.x
        //val deviceHeight = size.y

        return deviceWidth
    }


}