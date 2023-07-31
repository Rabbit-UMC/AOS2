package com.example.myo_jib_sa.schedule.currentMissionActivity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.ActivityDeleteCurrentMissionBinding
import com.example.myo_jib_sa.schedule.currentMissionActivity.adapter.*
import kotlin.concurrent.thread


class DeleteCurrentMissionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeleteCurrentMissionBinding
    private var position = 0 //CurrentMissionActivity에서 받아온 longclick한 아이템 포지션
    private var missionList = ArrayList<CurrentMissionDeleteData>() //CurrentMissionCurrentMissionDeleteAdapter의 데이터 리스트
    private lateinit var currentMissionDeleteAdapter : CurrentMissionCurrentMissionDeleteAdapter
    private var scheduleList = ArrayList<ScheduleDeleteAdapterData>() //CurrentMissionScheduleDeleteAdapter의 데이터 리스트
    private lateinit var scheduleDeleteAdapter : CurrentMissionScheduleDeleteAdapter
    private var isAllSeleted = false //전체선택
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteCurrentMissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCurrentMissionCurrentMissionDeleteAdapter()        //CurrentMissionCurrentMissionDeleteAdapter 연결
        currentMissionCurrentMissionDeleteRvItemClickEvent()        //currentMissionCurrentMissionDeleteRv item클릭 이벤트


        setActivity()//화면 셋팅
        setButton()//버튼 셋팅

        setCurrentMissionScheduleDeleteAdapter(missionList[position].missionTitle)        //CurrentMissionScheduleDeleteAdapter 연결

        // 2초동안 액션 없으면 작동  ------------1안
        //handlerDelayStart(MESSAGE_WHAT_TIMER, LIMIT_TIME);

        // 2초동안 액션 없으면 작동  ------------2안
        //buttonControl()

        // 2초동안 액션 없으면 작동  ------------3안
        //TouchListener()

        handlerDelayStart(MESSAGE_WHAT_TIMER, LIMIT_TIME)

    }
    //화면 셋팅
    private fun setActivity(){
        if(intent.hasExtra("position")){
            position = intent.getIntExtra("position", 0)
        }
            missionList[position].selected = true
        currentMissionDeleteAdapter.notifyItemChanged(position); //해당 포지션 아이템만 업데이트
    }
    //CurrentMissionCurrentMissionAdapter 연결
    private fun setCurrentMissionCurrentMissionDeleteAdapter(){

        missionList.add(CurrentMissionDeleteData("헬스1", "D+5", 10, R.drawable.ic_currentmission_exercise, 1))
        missionList.add(CurrentMissionDeleteData("헬스2", "D+5", 10, R.drawable.ic_currentmission_exercise, 1))
        missionList.add(CurrentMissionDeleteData("미션 제목3", "D+10", 10, R.drawable.ic_currentmission_art, 1))
        missionList.add(CurrentMissionDeleteData("미션 제목4", "D+10", 10, R.drawable.ic_currentmission_art, 1))
        missionList.add(CurrentMissionDeleteData("미션 제목5", "D+10", 10, R.drawable.ic_currentmission_art, 1))
        missionList.add(CurrentMissionDeleteData("미션 제목6", "D+10", 10, R.drawable.ic_currentmission_art, 1))
        missionList.add(CurrentMissionDeleteData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art, 1))
        missionList.add(CurrentMissionDeleteData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art, 1))
        missionList.add(CurrentMissionDeleteData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art, 1))


        currentMissionDeleteAdapter = CurrentMissionCurrentMissionDeleteAdapter(missionList)
        binding.missionListRv.layoutManager = GridLayoutManager(this, 2)
        binding.missionListRv.adapter = currentMissionDeleteAdapter
    }

    //CurrentMissionScheduleAdapter 연결
    private fun setCurrentMissionScheduleDeleteAdapter(missionTitle:String){
        //scheduleList.clear()
        scheduleList = ArrayList<ScheduleDeleteAdapterData>()
        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 4일차", "2023.07.01", 1))
        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 4일차", "2023.07.01", 1))
        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 3일차", "2023.06.30", 1))
        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 3일차", "2023.06.30", 1))
        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 3일차", "2023.06.30", 1))
        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 2일차", "2023.06.29", 1))
        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 2일차", "2023.06.29", 1))
        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 2일차", "2023.06.29", 1))
        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 2일차", "2023.06.29", 1))
        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 2일차", "2023.06.29", 1))


        scheduleDeleteAdapter = CurrentMissionScheduleDeleteAdapter(scheduleList)
        binding.scheduleListRv.layoutManager = LinearLayoutManager(this)
        binding.scheduleListRv.adapter = scheduleDeleteAdapter
    }


    //currentMissionCurrentMissionRv item클릭 이벤트
    private fun currentMissionCurrentMissionDeleteRvItemClickEvent() {
        currentMissionDeleteAdapter.setItemClickListener(object : CurrentMissionCurrentMissionDeleteAdapter.OnItemClickListener {

            var delay:Long = 0//클릭 간격

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(currentMissionData: CurrentMissionDeleteData) {
                if (System.currentTimeMillis() > delay) {
                    //한번 클릭 동작
                    setCurrentMissionScheduleDeleteAdapter(currentMissionData.missionTitle)


                    delay = System.currentTimeMillis()+200
                    return
                }
                if (System.currentTimeMillis() <= delay) {
                    //두번 클릭 동작
                    // 미션 상세 다이어로그 띄우기
                    var bundle = Bundle()
                    bundle.putLong("scheduleId", currentMissionData.missionId)
                    Log.d("debug", "\"scheduleId\", ${currentMissionData.missionId}")
                    val currentMissionDetailDialogFragment = CurrentMissionDetailDialogFragment()
                    currentMissionDetailDialogFragment.arguments = bundle

                    //scheduleDetailDialogItemClickEvent(scheduleDetailDialog)//scheduleDetailDialog Item클릭 이벤트 setting
                    currentMissionDetailDialogFragment.show(supportFragmentManager, "currentMissionDetailDialogFragment")
                }
            }
        })
    }

    //버튼 셋팅
    private fun setButton(){
        //전체 선택 버튼
        binding.allSeleteV.setOnClickListener{
            if(isAllSeleted){//전체 선택 해제
                for(i in 0 until scheduleList.size) {
                    scheduleList[i].selected = false
                }
                scheduleDeleteAdapter.notifyDataSetChanged();//rv update

                binding.allSeleteV.setBackgroundColor(Color.parseColor("#D9D9D9")) //회색으로 버튼 색 변경
                isAllSeleted = false
            }else{//전체 선택
                for(i in 0 until scheduleList.size) {
                    scheduleList[i].selected = true
                }
                scheduleDeleteAdapter.notifyDataSetChanged();//rv update

                binding.allSeleteV.setBackgroundColor(Color.parseColor("#C7DAFA")) //파란색으로 버튼 색 변경
                isAllSeleted = true
            }
        }

        //뒤로가기 버튼 클릭
        binding.goBackBtn.setOnClickListener {
//            var scheduleIntent = Intent(this, ScheduleFragment::class.java)
//            startActivity(scheduleIntent)
//            if(!isFinishing) {
            finish()
            //}
        }


        //삭제 버튼 클릭

    }


//    // =====================================================------------1안
//    //2초동안 동작 없으면
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        super.onTouchEvent(event)
//        if (event.getAction() === MotionEvent.ACTION_DOWN) {
//            Log.d("handler", "scroll action")
//            handlerDelayStop(MESSAGE_WHAT_TIMER)
//            handlerDelayStart(MESSAGE_WHAT_TIMER, LIMIT_TIME)
//            return true
//        }
//        return false
//    }
//
//    private var MESSAGE_WHAT_TIMER = 1
//    private var LIMIT_TIME = 2000 //2초
//
//    // 2초동안 동작 없으면 제어하는 핸들러
//    var mHandler = Handler(Looper.getMainLooper()) {
//        fun handleMessage(msg: Message) {
//
//            // 타이머 진행
//            if (msg.what === MESSAGE_WHAT_TIMER) {
//                Log.d("handler", " : MESSAGE_WHAT_TIMER ")
//                //LIMIT_TIME동안 동작이 없으면 할 거 적성
//                binding.deleteFbtn.visibility = View.VISIBLE
//
//            }
//            // else if(msg.what == MESSAGE_WHAT_CANCEL){
//            //     Log.d("MESSAGE_WHAT_CANCEL", " : MESSAGE_WHAT_CANCEL ");
//            //     removeMessages(MESSAGE_WHAT_TIMER);
//            //
//            // }
//        }
//
//        true
//    }
//
//    private fun handlerDelayStop(what: Int) {
//        Log.d("handler", " : handlerDelayStop ")
//        mHandler.removeMessages(what)
//
//    }
//    private fun handlerDelayStart(what: Int, time: Int) {
//        Log.d("handler", " : handlerDelayStart ")
//        mHandler.sendEmptyMessageDelayed(what, time.toLong())
//    }




//    // =====================================================------------2안
//    private val inactivityTimeout = 2000L // 5초 (버튼이 나타날 때까지의 대기 시간)
//    private val handler = Handler(Looper.getMainLooper())
//    private var isButtonVisible = false
//
//    @SuppressLint("ClickableViewAccessibility")
//    fun buttonControl(){
//        // 사용자의 터치 이벤트를 감지하기 위해 루트 뷰에 onTouchListener를 설정합니다.
//        binding.root.setOnTouchListener { _, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    // 사용자가 화면을 터치함, 버튼을 나타냄
//                    showButton()
//                    // 비활동 핸들러를 리셋
//                    handler.removeCallbacks(showButtonRunnable)
//                    handler.postDelayed(showButtonRunnable, inactivityTimeout)
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    // 사용자가 화면에 상호 작용함, 비활동 핸들러를 리셋
////                    handler.removeCallbacks(showButtonRunnable)
////                    handler.postDelayed(showButtonRunnable, inactivityTimeout)
//                }
//                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
//                    // 사용자가 터치를 놓거나 취소함, 비활동 핸들러를 리셋
////                    handler.removeCallbacks(showButtonRunnable)
////                    handler.postDelayed(showButtonRunnable, inactivityTimeout)
//                }
//            }
//            true // 터치 이벤트를 소비하기 위해 'true'를 반환
//        }
//    }
//
//    // 버튼을 나타내는 Runnable
//    private val showButtonRunnable = Runnable {
//        showButton()
//    }
//
//    // 버튼을 보이게 함
//    private fun showButton() {
//        if (!isButtonVisible) {
//            binding.deleteFbtn.visibility = View.VISIBLE
//            isButtonVisible = true
//        }
//    }
//    // 버튼을 숨김
//    private fun hideButton() {
//        if (isButtonVisible) {
//            binding.deleteFbtn.visibility = View.GONE
//            isButtonVisible = false
//        }
//    }



    //3안
    var started = true
    private var MESSAGE_WHAT_TIMER = 1
    private var LIMIT_TIME = 2000 //2초


        //2초동안 동작 없으면

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val userAction = event.action
        when (userAction) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("handler_debug", "scroll action")
                handlerDelayStop(MESSAGE_WHAT_TIMER)
                handlerDelayStart(MESSAGE_WHAT_TIMER, LIMIT_TIME)
            }
            MotionEvent.ACTION_MOVE -> {
                // 사용자가 화면에 상호 작용함, 비활동 핸들러를 리셋
//                    handler.removeCallbacks(showButtonRunnable)
//                    handler.postDelayed(showButtonRunnable, inactivityTimeout)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // 사용자가 터치를 놓거나 취소함, 비활동 핸들러를 리셋
//                    handler.removeCallbacks(showButtonRunnable)
//                    handler.postDelayed(showButtonRunnable, inactivityTimeout)
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private val handler = @SuppressLint("HandlerLeak")
    object: Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            //binding.textTimer.text = formatTime(msg.arg1)
            when(msg.what){
                MESSAGE_WHAT_TIMER -> {
                     Log.d("handler_debug", " : MESSAGE_WHAT_TIMER ")

                    binding.deleteFbtn.visibility = View.VISIBLE
                }
            }
        }
    }

    fun start(){

        started = true
        //sub thread
        var total = LIMIT_TIME
        thread(start = true){
            while(true){
                if(!started||total == 0) break

                total --

                val msg = Message()
                if(total == 0)
                    msg.what = MESSAGE_WHAT_TIMER//명령어 저장
                msg.arg1 = total
                handler.sendMessage(msg)

//                runOnUiThread {
//                    binding.textTimer.text = formatTime(total)
//                }
            }
        }

    }

        private fun handlerDelayStop(what: Int) {
        Log.d("handler_debug", " : handlerDelayStop ")
        handler.removeMessages(what)

    }
    private fun handlerDelayStart(what: Int, time: Int) {
        Log.d("handler_debug", " : handlerDelayStart ")
        handler.sendEmptyMessageDelayed(what, time.toLong())
    }



}
