package com.example.myo_jib_sa.Schedule.spinnerViewpager

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import com.example.myo_jib_sa.databinding.FragmentSpinnerMissionBinding
import com.example.myo_jib_sa.Schedule.api.RetrofitClient
import com.example.myo_jib_sa.Schedule.api.scheduleDetail.ScheduleDetailResult
import com.example.myo_jib_sa.Schedule.api.scheduleHome.Mission
import com.example.myo_jib_sa.Schedule.api.scheduleHome.ScheduleHomeResponse
import com.example.myo_jib_sa.Schedule.api.scheduleHome.ScheduleHomeService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SpinnerMissionFragment : Fragment() {
    private lateinit var binding : FragmentSpinnerMissionBinding
    private lateinit var missionTitleList : MutableList<String>//numberpicker타이틀 리스트
    private lateinit var titleIdMap : HashMap<String, Long>//id로 찾기
    private lateinit var titleTitleMap : HashMap<Long, String>//Title로 찾기
    private lateinit var missionList:List<Mission>
    private var scheduleData : ScheduleDetailResult = ScheduleDetailResult(//sharedPreferences로 받은값 저장
        scheduleId = 0,
        missionId = 0,
        missionTitle = "",
        scheduleTitle= "",
        startAt= "",
        endAt= "",
        content= "",
        scheduleWhen= ""
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpinnerMissionBinding.inflate(inflater, container, false)

        // missionTitleList 초기화
        missionTitleList = mutableListOf()
        titleIdMap = hashMapOf()
        titleTitleMap = hashMapOf()

        getData()//sharedPreference로 값 받기
        //값 저장할 sharedPreference 부르기
        val sharedPreference = requireContext().getSharedPreferences(
            "scheduleModifiedData",
            Context.MODE_PRIVATE
        )
        val editor = sharedPreference.edit()
        //값 변경하지 않았을때 기본값으로 전달
        editor.putString("missionTitle", scheduleData.missionTitle)
        scheduleData.missionId?.let { editor.putLong("missionId", it) }
        editor.apply()// data 저장!

        scheduleHomeApi()//api연결을 통해 missionTitleList를 setting


        return binding.root
    }
    override fun onPause() {
        super.onPause()


    }

    //sharedPreference값 받기
    fun getData(){
        val sharedPreference = requireContext().getSharedPreferences("scheduleData",
            Context.MODE_PRIVATE
        )
        scheduleData.scheduleTitle = sharedPreference.getString("scheduleTitle", "").toString()
        scheduleData.scheduleWhen = sharedPreference.getString("scheduleDate", "").toString()
        scheduleData.missionTitle = sharedPreference.getString("missionTitle", "").toString()
        scheduleData.startAt = sharedPreference.getString("scheduleStartTime", "").toString()
        scheduleData.endAt = sharedPreference.getString("scheduleEndTime", "").toString()
        scheduleData.content = sharedPreference.getString("scheduleMemo", "").toString()
        scheduleData.missionId = sharedPreference.getLong("missionId", 0)
        scheduleData.scheduleId = sharedPreference.getLong("scheduleId", 0)
    }

    //Numberpicker 초기값 설정
    private fun setNumberpickerInitvalue(missionTitle :String?):Int{
        var initValue = 0
        for(i in 0 until missionList!!.size){
            if(missionList[i].missionTitle == missionTitle)
                initValue = i+1
        }
        return initValue
    }

    private fun setNumberpicker(){
        var numberpicker = binding.missionSpinner

            numberpicker.minValue = 0
            numberpicker.maxValue = missionTitleList.size - 1
            numberpicker.value = setNumberpickerInitvalue(scheduleData.missionTitle)//초기값 설정
            numberpicker.displayedValues = missionTitleList.toTypedArray()
            numberpicker.wrapSelectorWheel = true //순환
            numberpicker.descendantFocusability =
                NumberPicker.FOCUS_BLOCK_DESCENDANTS //editText가 눌리는 것을 막는다


            numberpicker.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener {
                override fun onValueChange(picker: NumberPicker, oldVal: Int, newVal: Int) {
                    //Display the newly selected value from picker
                    //tv.setText("Selected value : " + missionTitleList.get(newVal))
                    var pickMissionTitle = missionTitleList.get(newVal)
                    Log.d("debug", "missionTitle : $pickMissionTitle")

                    scheduleData.missionId = titleIdMap[pickMissionTitle]!!
                    Log.d(
                        "debug",
                        "고른 미션 이름: ${titleTitleMap[scheduleData.missionId]}.${scheduleData.missionId}"
                    )
                    val sharedPreference = requireContext().getSharedPreferences("scheduleModifiedData", Context.MODE_PRIVATE)
                    val editor = sharedPreference.edit()
                    editor.putString("missionTitle", titleTitleMap[scheduleData.missionId])
                    editor.putLong("missionId", scheduleData.missionId!!)
                    editor.apply()// data 저장!
                }
            })
    }

    //scheduleHome api연결 for missionList받기위해
    private fun scheduleHomeApi() {
        // SharedPreferences 객체 가져오기
        val sharedPreferences = requireContext().getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        // JWT 값 가져오기
        val token = sharedPreferences.getString("jwt", null)

        //val token : String = BuildConfig.API_TOKEN
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
                    missionList = response.body()?.result!!.missionList


                    missionTitleList.add("없음")
                    titleIdMap["없음"] = -1 //-1 = missionId
                    titleTitleMap[-1] = "없음"

                    for(i in 0 until missionList!!.size){
                        var title = missionTitleFormat(missionList[i])
                        missionTitleList.add(title)
                        titleIdMap[title] = missionList[i].missionId
                        titleTitleMap[missionList[i].missionId] = missionList[i].missionTitle
                    }
                    setNumberpicker()


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



    //미션타이틀 포맷(예: 미션제목(D-5))
    fun missionTitleFormat(mission: Mission):String{

        return "${mission.missionTitle} (${mission.dday})"
    }


}