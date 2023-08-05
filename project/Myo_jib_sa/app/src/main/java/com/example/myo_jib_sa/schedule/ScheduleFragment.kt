package com.example.myo_jib_sa.schedule


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.BuildConfig
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.FragmentScheduleBinding
import com.example.myo_jib_sa.schedule.adapter.*
import com.example.myo_jib_sa.schedule.api.RetrofitClient
import com.example.myo_jib_sa.schedule.api.scheduleHome.Mission
import com.example.myo_jib_sa.schedule.api.scheduleHome.ScheduleHomeResponse
import com.example.myo_jib_sa.schedule.api.scheduleHome.ScheduleHomeService
import com.example.myo_jib_sa.schedule.api.scheduleOfDay.ScheduleOfDayResponse
import com.example.myo_jib_sa.schedule.api.scheduleOfDay.ScheduleOfDayResult
import com.example.myo_jib_sa.schedule.api.scheduleOfDay.ScheduleOfDayService
import com.example.myo_jib_sa.schedule.createScheduleActivity.CreateScheduleActivity
import com.example.myo_jib_sa.schedule.currentMissionActivity.CurrentMissionActivity
import com.example.myo_jib_sa.schedule.dialog.ScheduleDetailDialogFragment
import com.example.myo_jib_sa.schedule.dialog.ScheduleEditDialogFragment
import com.example.myo_jib_sa.schedule.dialog.scheduleDeleteDialog
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter



class ScheduleFragment : Fragment() {

    lateinit var binding: FragmentScheduleBinding
    lateinit var calendarAdapter : CalendarAdapter //calendarRvItemClickEvent() 함수에 사용하기 위해 전역으로 선언
    lateinit var scheduleAdaptar : ScheduleAdaptar //scheduleRvItemClickEvent() 함수에 사용하기 위해 전역으로 선언
    val scheduleDetailDialog = ScheduleDetailDialogFragment()
    lateinit var selectedDate : LocalDate //오늘 날짜

    var mDataList = ArrayList<Mission>() //미션 리스트 데이터
    var sDataList = ArrayList<ScheduleOfDayResult>() //일정 리스트 데이터

    private var adLoader: AdLoader? = null //광고를 불러올 adLoader 객체
    //val AD_UNIT_ID = BuildConfig.AD_UNIT_ID
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduleBinding.inflate(inflater, container, false)

        scheduleHomeApi()//scheduleHome api연결
        selectedDate = LocalDate.now()//오늘 날짜 가져오기

        //calendarAdapter 임시 초기화
        calendarAdapter = CalendarAdapter(ArrayList())
        calendarRvItemClickEvent()//Calendar rv item클릭 이벤트

        setCalendarAdapter()//화면 초기화
//      calendarRvItemClickEvent()//Calendar rv item클릭 이벤트


        //애드몹 광고 표시
        createAd()
        adLoader?.loadAd(AdRequest.Builder().build())

        //yyyy년 m월 (예: 2023년 6월 표시)
        binding.selectedYearMonthTv.text = YYYYMMFromDate(selectedDate)
        //m월 d일 일정 표시 (예: 6월 1일 일정 표시)
        binding.selectedMonthDayTv.text = "${MMDDFromDate(selectedDate)} 일정"

        //CurrentMissionAdapter,ScheduleAdaptar 리사이클러뷰 연결
        setCurrentMissionAdapter()
        setScheduleAdapter(selectedDate)
        scheduleRvItemClickEvent()//Schedule rv item클릭 이벤트

        //캘린더 visible버튼
        binding.calenderVisibleBtn.setOnClickListener{
            if(binding.calenderLayout.visibility == View.GONE)
                binding.calenderLayout.visibility = View.VISIBLE
            else
                binding.calenderLayout.visibility = View.GONE
        }

        //화면전환
        switchScreen()
        //캘린더에 이전달 다음달 이동 버튼 세팅
        calenderBtn()



        return binding.root
    }


    //month화면에 보여주기
    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setCalendarAdapter(){
        //month를 month text view에 보여주기 (결과: 1월)
        binding.monthTv.text = monthFromDate(selectedDate)



        CoroutineScope(Dispatchers.Main).launch {
            setHasScheduleMap(selectedDate)

            delay(1000)
            //이번달 날짜 가져오기
            val dayList = dayInMonthArray(selectedDate)

            //CalendarAdapter리사이클러뷰 연결
            calendarAdapter = CalendarAdapter(dayList)
            binding.calendarRv.layoutManager = GridLayoutManager(getActivity(), 7)
            binding.calendarRv.adapter = calendarAdapter

            calendarRvItemClickEvent()//Calendar rv item클릭 이벤트
        }

    }


    //날짜 생성: ArrayList<CalendarData>()생성
    @RequiresApi(Build.VERSION_CODES.O)
    private fun dayInMonthArray(date: LocalDate): ArrayList<CalendarData>{
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
                var currentDate = YYYYMMDDFromDate(LocalDate.of(selectedDate.year, selectedDate.monthValue, i))
                dayList.add(CalendarData(LocalDate.of(selectedDate.year, selectedDate.monthValue, i), hasScheduleMap[currentDate]))
                Log.d("debug", "$date checkResult = ${hasScheduleMap[currentDate]}")
            }
            else if(i<=dayOfWeek || i>(lastDay + dayOfWeek)){//그 외 경우
                dayList.add(CalendarData(null))
            }
            else{
                var currentDate = YYYYMMDDFromDate(LocalDate.of(selectedDate.year, selectedDate.monthValue, i-dayOfWeek))
                dayList.add(CalendarData(LocalDate.of(selectedDate.year, selectedDate.monthValue, i-dayOfWeek), hasScheduleMap[currentDate]))//얘만 살리기
                Log.d("debug", "$currentDate checkResult = ${hasScheduleMap[currentDate]}")
            }
        }



        return dayList
    }


    //광고 생성 메소드
    private fun createAd() {
        MobileAds.initialize(requireActivity())
        adLoader = AdLoader.Builder(requireActivity(), "ca-app-pub-3940256099942544/2247696110")//sample아이디
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

        val currentMissionAdapter = CurrentMissionAdapter(mDataList)
        binding.currentMissionRv.layoutManager =
            LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.currentMissionRv.adapter = currentMissionAdapter
    }


    //setScheduleAdapterAdapter 리사이클러뷰 연결
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setScheduleAdapter(date: LocalDate?){
        var formatter = DateTimeFormatter.ofPattern("D")
        var day = date?.format(formatter)

        //ScheduleAdaptar 리사이클러뷰 연결

//        if(date?.dayOfMonth == 15) {
//            sDataList.add(ScheduleData("헬스 4일차", "19:00", "20:00"))
//            sDataList.add(ScheduleData("헬스 5일차", "19:00", "20:00"))
//            sDataList.add(ScheduleData("헬스 6일차", "19:00", "20:00"))
//            sDataList.add(ScheduleData("헬스 4일차", "19:00", "20:00"))
//            sDataList.add(ScheduleData("헬스 5일차", "19:00", "20:00"))
//            sDataList.add(ScheduleData("헬스 6일차", "19:00", "20:00"))
//        }
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

                //dialog연결 2안
                var scheduleDeleteDialog= scheduleDeleteDialog(requireContext(), binding.scheduleRv.adapter as ScheduleAdaptar, position)
                scheduleDeleteDialog.setButtonClickListener(object: scheduleDeleteDialog.OnButtonClickListener{
                    override fun onClickExitBtn() {
                        scheduleAdaptar.notifyItemChanged(viewHolder.adapterPosition);
                    }
                })
                scheduleDeleteDialog.show()
              }


            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                val sItemRectangleImg: ImageView = viewHolder.itemView.findViewById(R.id.sechedule_rectangle_img)
                sItemRectangleImg.setImageResource(R.drawable.ic_schedule_rectangle)
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val sItemRectangleImg: ImageView? = viewHolder?.itemView?.findViewById(R.id.sechedule_rectangle_img)
                    sItemRectangleImg?.setImageResource(R.drawable.ic_schedule_delete_rectangle)
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
                //2023년 6월 표시
                binding.selectedYearMonthTv.text = YYYYMMFromDate(calendarData.date)
                //6월 1일 일정 표시
                binding.selectedMonthDayTv.text = "${MMDDFromDate(calendarData.date)} 일정"

                var iYear = calendarData.date?.year
                var iMonth = calendarData.date?.monthValue
                var iDay = calendarData.date?.dayOfMonth

//                Toast.makeText(context, "${iYear}년 ${iMonth}월 ${iDay}일", Toast.LENGTH_SHORT)
//                    .show()
                CoroutineScope(Dispatchers.Main).launch {
                    scheduleOfDayApi(YYYYMMDDFromDate(calendarData.date))//scheduleOfDay api연결
                    delay(100)
                    setScheduleAdapter(calendarData.date)
                }
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
            override fun onClickEditBtn() {
                val scheduleEditDialog = ScheduleEditDialogFragment()
                scheduleEditDialogItemClickEvent(scheduleEditDialog)//scheduleEditDialog Item클릭 이벤트 setting
                scheduleEditDialog.show(requireActivity().supportFragmentManager, "ScheduleEditDialog")
            }
        })
    }

    //scheduleEditDialog Item클릭 이벤트
    fun scheduleEditDialogItemClickEvent(dialog: ScheduleEditDialogFragment){
        dialog.setButtonClickListener(object: ScheduleEditDialogFragment.OnButtonClickListener{
            override fun onClickEditBtn() {
                scheduleDetailDialog.dismiss()
            }
        })
    }




    //화면전환 메소드
    fun switchScreen(){
        //history누르면 HistoryActivity로 화면 전환
        binding.historyTv.setOnClickListener{
            var historyIntent = Intent(requireActivity(), HistoryActivity::class.java)
            startActivity(historyIntent)
        }
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

    //캘린더에 이전달 다음달 이동 버튼 세팅
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




    //scheduleHome api연결
    fun scheduleHomeApi() {
        val token : String = BuildConfig.KAKAO_API_KEY
//        Log.d("retrofit", "token = "+token+"l");

        val service = RetrofitClient.getInstance().create(ScheduleHomeService::class.java)
        val listCall = service.scheduleHome(token)

        listCall.enqueue(object : Callback<ScheduleHomeResponse> {
            override fun onResponse(
                call: Call<ScheduleHomeResponse>,
                response: Response<ScheduleHomeResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("retrofit", response.body().toString());
                    val missionList = response.body()?.result?.missionList

                    //현재 미션 데이터 리스트 리사이클러뷰 연결
                    //디데이 얼마 안남은 미션부터 많이 남은 순으로 정렬돼 있음
                    for(i in 0 until missionList!!.size){
                        mDataList.add(Mission(missionList[i].missionId, missionList[i].missionTitle, missionList[i].challengerCnts, missionList[i].dday))
                    }
                    setCurrentMissionAdapter()

                }else {
                    Log.e("retrofit", "onResponse: Error ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("retrofit", "onResponse: Error Body $errorBody")
                }}
            override fun onFailure(call: Call<ScheduleHomeResponse>, t: Throwable) {
                Log.e("retrofit", "onFailure: ${t.message}")
            }
        })
    }


    //scheduleOfDay api연결
    //calendarRvItemClickEvent()안에서만 실행
    fun scheduleOfDayApi(date: String?) {
        val token : String = BuildConfig.KAKAO_API_KEY
//        Log.d("retrofit", "token = "+token+"l");
//
//        val requestBody = ScheduleOfDayRequest(
//            scheduleWhen = date
//        )
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
                    Log.d("retrofit", response.body().toString());
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
                }else {
                    Log.e("retrofit", "onResponse: Error ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("retrofit", "onResponse: Error Body $errorBody")
                }}
            override fun onFailure(call: Call<ScheduleOfDayResponse>, t: Throwable) {
                Log.e("retrofit", "onFailure: ${t.message}")
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


        for (i in 1..lastDay) {
            var currentDate =
                YYYYMMDDFromDate(LocalDate.of(selectedDate.year, selectedDate.monthValue, i))
            scheduleOfDayApiForCheck(currentDate)
        }


        Log.d("debug", "==========================================")
        for ((key, value) in hasScheduleMap) {
            Log.d("debug", "dd$key -> $value")
        }

    }

    //scheduleOfDayApi연결 : 캘린더 파란동그라미 표시용
    fun scheduleOfDayApiForCheck(date: String?) {
        var checkResult: Boolean = false

          val token = BuildConfig.KAKAO_API_KEY
//        Log.d("retrofit", "token = "+token+"l");
//
//        val requestBody = ScheduleOfDayRequest(
//            scheduleWhen = date
//        )

        val service = RetrofitClient.getInstance().create(ScheduleOfDayService::class.java)
        val listCall = service.scheduleOfDay(token, date)


        listCall.enqueue(object : Callback<ScheduleOfDayResponse> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<ScheduleOfDayResponse>,
                response: Response<ScheduleOfDayResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("retrofit", response.body().toString());
                    val scheduleList = response.body()?.result

                    //response에 값이 있으면 해당날짜 일정있음
                    if (!scheduleList.isNullOrEmpty()) {
                        checkResult = true
                        Log.d("debug", "$date scheduleList != null")
                    }
                    else{
                        checkResult = false
                        Log.d("debug", "$date scheduleList == null")
                    }


                    //Log.d("debug", "$date checkResult = $checkResult")
                    hasScheduleMap[date] = checkResult

                } else {
                    Log.e("retrofit", "onResponse: Error ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("retrofit", "onResponse: Error Body $errorBody")
                }
            }

            override fun onFailure(call: Call<ScheduleOfDayResponse>, t: Throwable) {
                Log.e("retrofit", "onFailure: ${t.message}")
            }
        })

    }

    //M월 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    private fun monthFromDate(date : LocalDate):String{
        var formatter = DateTimeFormatter.ofPattern("M월")
        return date.format(formatter)
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
    //YYYY-MM-DD 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    private fun YYYYMMDDFromDate(date : LocalDate?):String?{
        var formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd")
        return date?.format(formatter)
    }

}