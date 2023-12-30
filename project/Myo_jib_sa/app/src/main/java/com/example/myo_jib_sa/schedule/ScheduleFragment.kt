package com.example.myo_jib_sa.schedule


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
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.BuildConfig
import com.example.myo_jib_sa.schedule.adapter.CalendarAdapter
import com.example.myo_jib_sa.schedule.adapter.CalendarData
import com.example.myo_jib_sa.schedule.adapter.ScheduleAdaptar
import com.example.myo_jib_sa.schedule.api.RetrofitClient
import com.example.myo_jib_sa.schedule.api.scheduleDelete.ScheduleMonthResponse
import com.example.myo_jib_sa.schedule.api.scheduleDelete.ScheduleMonthService
import com.example.myo_jib_sa.schedule.api.scheduleHome.Mission
import com.example.myo_jib_sa.schedule.api.scheduleOfDay.ScheduleOfDayResponse
import com.example.myo_jib_sa.schedule.api.scheduleOfDay.ScheduleOfDayResult
import com.example.myo_jib_sa.schedule.api.scheduleOfDay.ScheduleOfDayService
import com.example.myo_jib_sa.databinding.FragmentScheduleBinding
import com.example.myo_jib_sa.schedule.adapter.CurrentMissionAdapter

import com.example.myo_jib_sa.schedule.createScheduleActivity.CreateScheduleActivity
import com.example.myo_jib_sa.schedule.currentMissionActivity.CurrentMissionActivity
import com.example.myo_jib_sa.schedule.api.currentMission.CurrentMissionResponse
import com.example.myo_jib_sa.schedule.api.currentMission.CurrentMissionService
import com.example.myo_jib_sa.schedule.dialog.ScheduleDeleteDialogFragment
import com.example.myo_jib_sa.schedule.dialog.ScheduleDetailDialogFragment
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
    lateinit var calendarAdapter : CalendarAdapter //calendarRvItemClickEvent() 함수에 사용하기 위해 전역으로 선언
    lateinit var scheduleAdaptar : ScheduleAdaptar //scheduleRvItemClickEvent() 함수에 사용하기 위해 전역으로 선언
    lateinit var scheduleDetailDialog : ScheduleDetailDialogFragment
    lateinit var selectedDate : LocalDate //오늘 날짜

    var mDataList = ArrayList<Mission>() //미션 리스트 데이터
    var sDataList = ArrayList<ScheduleOfDayResult>() //일정 리스트 데이터

    private var adLoader: AdLoader? = null //광고를 불러올 adLoader 객체
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduleBinding.inflate(inflater, container, false)

        scheduleDetailDialog =  ScheduleDetailDialogFragment(requireContext())//scheduleDetailDialog 설정
        selectedDate = LocalDate.now()//오늘 날짜 가져오기

        //calendarAdapter 임시 초기화
        calendarAdapter = CalendarAdapter(ArrayList())
        binding.calendarRv.addItemDecoration(CalendarAdapter.GridSpaceDecoration(getDisplayWidthSize()))
        calendarRvItemClickEvent()//Calendar rv item클릭 이벤트


        //애드몹 광고 표시
        createAd()
        adLoader?.loadAd(AdRequest.Builder().build())

        //m월 d일 일정 표시 (예: 6월 1일 일정 표시)
        binding.selectedMonthDayTv.text = MMDDFromDate(selectedDate)
        //YYYY년 표시 - calendar
        binding.selectedYearTv.text = yearFromDate(selectedDate)
        //MM월 표시 - calendar
        binding.selectedMonthTv.text = monthFromDate(selectedDate)

        //CurrentMissionAdapter,ScheduleAdaptar 리사이클러뷰 연결
        setScheduleAdapter(selectedDate)
        scheduleRvItemClickEvent()//Schedule rv item클릭 이벤트


        //화면전환
        switchScreen()
        //캘린더 관련 모든 버튼
        calenderBtn()



        return binding.root
    }


    //화면 reload 기능: 기본적인 데이터 세팅
    //화면이 사용자와 상호작용할수 있는 상태일때 (=화면이 눈에 보일때
   @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        Log.d("debug", "onResume")
        currentMissionApi()//scheduleHome api연결


        //CurrentMissionAdapter,ScheduleAdaptar 리사이클러뷰 연결
        //setCurrentMissionAdapter()
        //setScheduleAdapter(selectedDate)
        //scheduleAdaptar.notifyDataSetChanged()
        //scheduleRvItemClickEvent()//Schedule rv item클릭 이벤트


        CoroutineScope(Dispatchers.Main).launch{
            delay(50)
            setCalendarAdapter()//화면 초기화
            scheduleOfDayApi(YYYYMMDDFromDate(selectedDate))//scheduleOfDay api연결

        }
    }



    //month화면에 보여주기
    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setCalendarAdapter(){

//        //month를 month text view에 보여주기 (결과: 1월)
//        binding.selectedMonthTv.text = monthFromDate(selectedDate)
        //m월 d일 일정 표시 (예: 6월 1일 일정 표시)
        binding.selectedMonthDayTv.text = MMDDFromDate(selectedDate)
        //YYYY년 표시 - calendar
        binding.selectedYearTv.text = yearFromDate(selectedDate)
        //MM월 표시 - calendar
        binding.selectedMonthTv.text = monthFromDate(selectedDate)

        //일정 있는지 없는지 api로 체크
        //<api 안에서>
        // -> val dayList = dayInMonthArray(selectedDate)로 월별 날짜 불러오기
        //CalendarAdapter리사이클러뷰 연결
        //calendarRvItemClickEvent() //Calendar rv item클릭 이벤트
        setHasScheduleMap(selectedDate)


    }


    //날짜 생성: ArrayList<CalendarData>()생성
    @RequiresApi(Build.VERSION_CODES.O)
    private fun dayInMonthArray(date: LocalDate): ArrayList<CalendarData>{
        var yearMonth = YearMonth.from(date)
        val dayList = ArrayList<CalendarData>()



        //해당 월의 마지막 날짜 가져오기(결과: 1월이면 31)
        var lastDay = yearMonth.lengthOfMonth()
        //해당 월의 첫번째 날 가져오기(결과: 2023-01-01)
        var firstDay = selectedDate.withDayOfMonth(1)
        //첫 번째날 요일 가져오기(결과: 월 ~일이 1~7에 대응되어 나타남)
        var dayOfWeek = firstDay.dayOfWeek.value

        for(i in 1..42){
            if(dayOfWeek == 7){//그 달의 첫날이 일요일일때 작동: 한칸 아래줄부터 날짜 표시되는 현상 막기위해
                if(i>lastDay) {
                    break
                    //dayList.add(CalendarData(null)) //끝에 빈칸 자르기
                }
                else if(i == selectedDate.dayOfMonth){
                    var currentDate = YYYYMMDDFromDate(
                        LocalDate.of(
                            selectedDate.year,
                            selectedDate.monthValue,
                            i
                        )
                    )
                    dayList.add(
                        CalendarData(
                            LocalDate.of(
                                selectedDate.year,
                                selectedDate.monthValue,
                                i
                            ), hasScheduleMap[currentDate], true
                        )
                    )
                }
                else {
                    var currentDate = YYYYMMDDFromDate(
                        LocalDate.of(
                            selectedDate.year,
                            selectedDate.monthValue,
                            i
                        )
                    )
                    dayList.add(
                        CalendarData(
                            LocalDate.of(
                                selectedDate.year,
                                selectedDate.monthValue,
                                i
                            ), hasScheduleMap[currentDate]
                        )
                    )
                }
            }
//            else if(i<=dayOfWeek || i>(lastDay + dayOfWeek)){//그 외 경우
//                dayList.add(CalendarData(null))
//            }
            else if(i<=dayOfWeek){ //끝에 빈칸 자르기위해
                dayList.add(CalendarData(null))
            }
            else if(i>(lastDay + dayOfWeek)){//끝에 빈칸 자르기위해
                break
            }
            else if(i-dayOfWeek == selectedDate.dayOfMonth){
                var currentDate = YYYYMMDDFromDate(
                    LocalDate.of(
                        selectedDate.year,
                        selectedDate.monthValue,
                        i-dayOfWeek
                    )
                )
                dayList.add(
                    CalendarData(
                        LocalDate.of(
                            selectedDate.year,
                            selectedDate.monthValue,
                            i-dayOfWeek
                        ), hasScheduleMap[currentDate], true
                    )
                )
            }
            else{
                var currentDate = YYYYMMDDFromDate(LocalDate.of(selectedDate.year, selectedDate.monthValue, i-dayOfWeek))
                dayList.add(CalendarData(LocalDate.of(selectedDate.year, selectedDate.monthValue, i-dayOfWeek), hasScheduleMap[currentDate]))//얘만 살리기
            }
        }


        return dayList
    }


    //광고 생성 메소드
    private fun createAd() {
        MobileAds.initialize(requireActivity())
        adLoader = AdLoader.Builder(requireActivity(), BuildConfig.AD_UNIT_ID)//sample아이디
            .forNativeAd { ad : NativeAd ->
                val template: TemplateView = binding.myTemplate
                template.setNativeAd(ad)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Handle the failure by logging, altering the UI, and so on.
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder()
                // Methods in the NativeAdOptions.Builder class can be
                // used here to specify individual options settings.
                .build())
            .build()
    }


    //CurrentMissionAdapter 리사이클러뷰 연결
    private fun setCurrentMissionAdapter() {

//        mDataList.add(Mission(1, "미션1", 10, "30"))
//        mDataList.add(Mission(1, "미션2", 10, "30"))
//        mDataList.add(Mission(1, "미션3", 10, "30"))
//        mDataList.add(Mission(1, "미션4", 10, "30"))
//        mDataList.add(Mission(1, "미션5", 10, "30"))

        val currentMissionAdapter = CurrentMissionAdapter(mDataList)
        binding.currentMissionRv.layoutManager =
            LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.currentMissionRv.adapter = currentMissionAdapter
    }


    //setScheduleAdapterAdapter 리사이클러뷰 연결
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setScheduleAdapter(date: LocalDate?){
        //var formatter = DateTimeFormatter.ofPattern("D")
        //var day = date?.format(formatter)

        //ScheduleAdaptar 리사이클러뷰 연결

//        if(date?.dayOfMonth == 15) {
//            sDataList.add(ScheduleData("헬스 4일차", "19:00", "20:00"))
//            sDataList.add(ScheduleData("헬스 5일차", "19:00", "20:00"))
//            sDataList.add(ScheduleData("헬스 6일차", "19:00", "20:00"))
//            sDataList.add(ScheduleData("헬스 4일차", "19:00", "20:00"))
//            sDataList.add(ScheduleData("헬스 5일차", "19:00", "20:00"))
//            sDataList.add(ScheduleData("헬스 6일차", "19:00", "20:00"))
//        }
        Log.d("retrofit", "$date : $sDataList")
        scheduleAdaptar = ScheduleAdaptar(sDataList)
        binding.scheduleRv.layoutManager = LinearLayoutManager(getActivity())
        binding.scheduleRv.adapter = scheduleAdaptar

        //noScheduleIv의 visibility설정
        if(sDataList.size == 0)
            binding.noSchedule.visibility = View.VISIBLE
        else
            binding.noSchedule.visibility = View.GONE

        //swipe시 삭제 이벤트
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position = viewHolder.adapterPosition

                var bundle = Bundle()
                //bundle.putLong("scheduleId", scheduleData.scheduleId)

                //dialog연결 2안
                var scheduleDeleteDialog = ScheduleDeleteDialogFragment(binding.scheduleRv.adapter as ScheduleAdaptar, position)
                scheduleDeleteDialog.setButtonClickListener(object: ScheduleDeleteDialogFragment.OnButtonClickListener{
                    override fun onClickExitBtn() {
                        //scheduleAdaptar.notifyItemChanged(viewHolder.adapterPosition);
                        //CoroutineScope(Dispatchers.Main).launch{
                            //delay(50)
                            setCalendarAdapter()//화면 초기화
                            scheduleOfDayApi(YYYYMMDDFromDate(selectedDate))//scheduleOfDay api연결
                        //}
                    }
                })
                scheduleDeleteDialog.arguments = bundle
                scheduleDeleteDialog.show(requireActivity().supportFragmentManager, "scheduleDeleteDialog")

              }


            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                //todo
                //val sItemRectangleImg: ImageView = viewHolder.itemView.findViewById(R.id.sechedule_rectangle_img)
                //sItemRectangleImg.setImageResource(R.drawable.ic_schedule_rectangle)
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    //todo
                   // val sItemRectangleImg: ImageView? = viewHolder?.itemView?.findViewById(R.id.sechedule_rectangle_img)
                   // sItemRectangleImg?.setImageResource(R.drawable.ic_schedule_delete_rectangle)
                }
            }
        }).apply {
            // ItemTouchHelper에 RecyclerView 설정
            attachToRecyclerView(binding.scheduleRv)
        }

        //캘린더 클릭할 때 마다 일정리스트가 다시 set되고, 따라서 item클릭 이벤트도 다시 연결해 주어야 함함
       scheduleRvItemClickEvent()//Schedule rv item클릭 이벤트
    }


    //Calendar rv item클릭 이벤트
    fun calendarRvItemClickEvent() {
        calendarAdapter.setItemClickListener(object : CalendarAdapter.OnItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(calendarData: CalendarData) {


                // 클릭 시 이벤트 작성
                Log.d("debug", "클릭!")
                //2023년
                binding.selectedYearTv.text = yearFromDate(calendarData.date)
                //6월 표시
                binding.selectedMonthTv.text = monthFromDate(calendarData.date)
                //6월 1일 일정 표시
                binding.selectedMonthDayTv.text = MMDDFromDate(calendarData.date)

//                var iYear = calendarData.date?.year
//                var iMonth = calendarData.date?.monthValue
//                var iDay = calendarData.date?.dayOfMonth
//
//                selectedDate = calendarData.date!!
//

                scheduleOfDayApi(YYYYMMDDFromDate(calendarData.date))//scheduleOfDay api연결

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
                scheduleDetailDialog.show(requireActivity().supportFragmentManager, "ScheduleDetailDialog")
            }
        })
    }

    //scheduleDetailDialog Item클릭 이벤트
    fun scheduleDetailDialogItemClickEvent(dialog: ScheduleDetailDialogFragment){
        dialog.setButtonClickListener(object: ScheduleDetailDialogFragment.OnButtonClickListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClickEditBtn() {
                //화면reload===============================================

                currentMissionApi()//scheduleHome api연결
                setCalendarAdapter()//화면 초기화

                //CurrentMissionAdapter,ScheduleAdaptar 리사이클러뷰 연결
                setCurrentMissionAdapter()

                scheduleOfDayApi(YYYYMMDDFromDate(selectedDate))//scheduleOfDay api연결
                //화면reload===============================================
            }
        })
    }



    //화면전환 메소드
    fun switchScreen(){
        //history누르면 HistoryActivity로 화면 전환
//        binding.historyTv.setOnClickListener{
//            var historyIntent = Intent(requireActivity(), SuccessMissionActivity::class.java)
//            startActivity(historyIntent)
//        }
        //미션리스트 위에 모두보기 누르면 CurrentMissionActivity로 화면 전환
        binding.viewAllTv.setOnClickListener{
            var missionIntent = Intent(requireActivity(), CurrentMissionActivity::class.java)
            startActivity(missionIntent)
        }
        //floating button누르면 CreateScheduleActivity로 화면 전환
        binding.newScheduleFloatingBtn.setOnClickListener{
            var createSIntent = Intent(requireActivity(), CreateScheduleActivity::class.java)
            startActivity(createSIntent)
        }
    }

    //캘린더 관련 버튼
    @RequiresApi(Build.VERSION_CODES.O)
    private fun calenderBtn(){

        //month-day 어제로 이동
        binding.yesterdayBtn.setOnClickListener {
            selectedDate = selectedDate.minusDays(1)
            setCalendarAdapter()
        }

        //month-day 내일로 이동
        binding.tomorrowBtn.setOnClickListener {
            selectedDate =selectedDate.plusDays(1)
            setCalendarAdapter()
        }

        //캘린더 visible버튼
        binding.calendarVisibleBtn.setOnClickListener{
                binding.calendarLayout.visibility = View.VISIBLE
                binding.calendarMonthDayHeader.visibility = View.GONE
        }

        //캘린더 gone 버튼
        binding.calendarGoneBtn.setOnClickListener{
            binding.calendarLayout.visibility = View.GONE
            binding.calendarMonthDayHeader.visibility = View.VISIBLE
        }

        //이전달로 이동
        binding.preMonthBtn.setOnClickListener{
            selectedDate = selectedDate.minusMonths(1)
            //CoroutineScope(Dispatchers.Main).launch {
                setCalendarAdapter()
                //calendarRvItemClickEvent()
            //}
        }
        //다음달로 이동
        binding.nextMonthBtn.setOnClickListener{
            selectedDate =selectedDate.plusMonths(1)
            //CoroutineScope(Dispatchers.Main).launch {
                setCalendarAdapter()
                //calendarRvItemClickEvent()
            //}
        }
    }

    //currentMission api연결
    private fun currentMissionApi() {
        val sharedPreferences = requireContext().getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", null)

        mDataList.clear()//mDataList초기화

        val service = RetrofitClient.getInstance().create(CurrentMissionService::class.java)
        val listCall = service.currentMission(token)
        listCall.enqueue(object : Callback<CurrentMissionResponse> {
            override fun onResponse(
                call: Call<CurrentMissionResponse>,
                response: Response<CurrentMissionResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("retrofit", "currentMissionApi "+response.body().toString());
                    val missionList = response.body()?.result

                    //현재 미션 데이터 리스트 리사이클러뷰 연결
                    //디데이 얼마 안남은 미션부터 많이 남은 순으로 정렬돼 있음
                    for(i in 0 until missionList!!.size){
                        mDataList.add(Mission(missionList[i].missionId, missionList[i].missionTitle, missionList[i].challengerCnt, missionList[i].dday))
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
    fun scheduleOfDayApi(date: String?) {
        // SharedPreferences 객체 가져오기
        val sharedPreferences = requireContext().getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        // JWT 값 가져오기
        val token = sharedPreferences.getString("jwt", null)

        //val token : String = BuildConfig.API_TOKEN
//        Log.d("retrofit", "token = "+token+"l");
//
        sDataList.removeAll(sDataList.toSet())//초기화

        val service = RetrofitClient.getInstance().create(ScheduleOfDayService::class.java)
        val listCall = service.scheduleOfDay(token, date)

        listCall.enqueue(object : Callback<ScheduleOfDayResponse> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<ScheduleOfDayResponse>,
                response: Response<ScheduleOfDayResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("retrofit", "scheduleOfDayApi"+response.body().toString());
                    val scheduleList = response.body()?.result

                    //일정 데이터 리스트 sDataList에 추가
                    for(i in 0 until  scheduleList!!.size){
                        sDataList.add(ScheduleOfDayResult(
                            scheduleList[i].scheduleId,
                            scheduleList[i].scheduleTitle,
                            scheduleList[i].scheduleStart,
                            scheduleList[i].scheduleEnd,
                            scheduleList[i].scheduleWhen
                        ))
                    }
                    //if(binding.F.visibility == View.VISIBLE)
                        setScheduleAdapter(selectedDate)
                }else {
                    Log.e("retrofit", "scheduleOfDayApi_onResponse: Error ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("retrofit", "scheduleOfDayApi_onResponse: Error Body $errorBody")
                }}
            override fun onFailure(call: Call<ScheduleOfDayResponse>, t: Throwable) {
                Log.e("retrofit", "scheduleOfDayApi_onFailure: ${t.message}")
            }
        })
    }


    private var hasScheduleMap:HashMap<String?, Boolean> = HashMap()
    @RequiresApi(Build.VERSION_CODES.O)
    fun setHasScheduleMap(date: LocalDate) {
        //val dayList = ArrayList<CalendarData>()
        var yearMonth = YearMonth.from(date)

        //hasScheduleMap.clear()//초기화

        //해당 월의 마지막 날짜 가져오기(결과: 1월이면 31)
        var lastDay = yearMonth.lengthOfMonth()
        //해당 월의 첫번째 날 가져오기(결과: 2023-01-01)
        var firstDay = selectedDate.withDayOfMonth(1)
        //첫 번째날 요일 가져오기(결과: 월 ~일이 1~7에 대응되어 나타남)
        var dayOfWeek = firstDay.dayOfWeek.value



        var currentMonth = YYYY_MMFromDate(LocalDate.of(selectedDate.year, selectedDate.monthValue, selectedDate.dayOfMonth))//MMMM-DD형태로 포맷
        scheduleMonthApi(currentMonth, lastDay)//날짜별로 스케줄 있는지 없는지 체크
    }

    //scheduleMonthApi 연결: 스케줄 있는지 없는지 체크
    private fun scheduleMonthApi(monthDate: String?, lastDay:Int) {
        var checkResult: Boolean = false

        //hasScheduleMap초기화
        for(j in 1..lastDay) {
            val formatter = DecimalFormat("00")
            hasScheduleMap["$monthDate-${formatter.format(j)}"] = false
        }

        // JWT 값 가져오기
        val sharedPreferences = requireContext().getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", "")


        val service = RetrofitClient.getInstance().create(ScheduleMonthService::class.java)
        val listCall = service.scheduleMonth(token, monthDate)

        listCall.enqueue(object : Callback<ScheduleMonthResponse> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<ScheduleMonthResponse>,
                response: Response<ScheduleMonthResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("retrofit", response.body().toString());
                    val scheduleList = response.body()?.result?.dayList
                    val formatter = DecimalFormat("00")

                    //현재 미션 데이터 리스트 리사이클러뷰 연결
                    //디데이 얼마 안남은 미션부터 많이 남은 순으로 정렬돼 있음
                    for(i in 0 until scheduleList!!.size){
                        hasScheduleMap["$monthDate-${formatter.format(scheduleList[i])}"] = true
                    }

                    val dayList = dayInMonthArray(selectedDate)

                    //CalendarAdapter리사이클러뷰 연결
                    calendarAdapter = CalendarAdapter(dayList)
                    binding.calendarRv.layoutManager = GridLayoutManager(activity, 7)
                    binding.calendarRv.adapter = calendarAdapter

                    calendarRvItemClickEvent()//Calendar rv item클릭 이벤트

                }else {
                    Log.e("retrofit", "scheduleMonthApi_onResponse: Error ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("retrofit", "scheduleMonthApi_onResponse: Error Body $errorBody")
                }}
            override fun onFailure(call: Call<ScheduleMonthResponse>, t: Throwable) {
                Log.e("retrofit", "scheduleMonthApi_onFailure: ${t.message}")
            }
        })
    }

    //YYYY 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    private fun yearFromDate(date : LocalDate?):String?{
        var formatter = DateTimeFormatter.ofPattern("YYYY")
        return date?.format(formatter)
    }
    //M월 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    private fun monthFromDate(date : LocalDate?):String?{
        var formatter = DateTimeFormatter.ofPattern("M월")
        return date?.format(formatter)
    }
    //YYYY년 M월 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    private fun YYYYMMFromDate(date : LocalDate?):String?{
        var formatter = DateTimeFormatter.ofPattern("YYYY년 M월")
        return date?.format(formatter)
    }
    //YYYY- MM 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    private fun YYYY_MMFromDate(date : LocalDate?):String?{
        var formatter = DateTimeFormatter.ofPattern("YYYY-MM")
        return date?.format(formatter)
    }
    //M월 D일 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    private fun MMDDFromDate(date : LocalDate?):String?{
        var formatter = DateTimeFormatter.ofPattern("M월 d일")
        return date?.format(formatter)
    }
    //YYYY-MM-DD 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    private fun YYYYMMDDFromDate(date : LocalDate?):String?{
        var formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd")
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