//package com.example.myo_jib_sa.schedule.historyActivity
//
//import android.content.Context
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.util.Log
//import androidx.recyclerview.widget.GridLayoutManager
//import com.example.myo_jib_sa.schedule.api.RetrofitClient
//import com.example.myo_jib_sa.databinding.ActivityFailedMissionBinding
//import com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMission.*
//import com.example.myo_jib_sa.schedule.historyActivity.adapter.MissionHistoryAdapter
//import com.example.myo_jib_sa.schedule.historyActivity.adapter.MissionHistoryRVDecoration
//import com.example.myo_jib_sa.schedule.historyActivity.api.profile_nickName.ProfileNickNameService
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//class FailedMissionActivity : AppCompatActivity() {
//    private lateinit var binding : ActivityFailedMissionBinding
//    private lateinit var missionHistoryAdapter : MissionHistoryAdapter
//    private var failuresMissionList = ArrayList<HistoryMissionList>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityFailedMissionBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        //setMissionHistoryAdapter() //todo: 지우기
//        setButton()
//        profileNickNameApi()
//    }
//
//    override fun onResume(){
//        super.onResume()
//        failuresMissionHistoryApi() //todo: 주석 해제
//    }
//
//    private fun setButton(){
//        binding.changeBtn.setOnClickListener {
//            var intent = Intent(this, SuccessMissionActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//
//        binding.goBackBtn.setOnClickListener {
//            finish()
//        }
//    }
//
//
//    //MissionHistoryAdapter 연결
//    private fun setMissionHistoryAdapter(){
//        //todo: 지우기
////        for(i in 0 until 9) {
////            failuresMissionList.add(HistoryMissionList(i.toLong(), 20, i.toLong(), "content$i", "2023-08-30", "2023-08-20", "https://rabbit-umc-bucket.s3.ap-northeast-2.amazonaws.com/dfdfdfd/dadf18f5-379d-4a44-8e15-bdc05ed63596%E1%84%8B%E1%85%AE%E1%86%AB%E1%84%83%E1%85%A9%E1%86%BC%E1%84%8F%E1%85%A1%E1%84%90%E1%85%A6%E1%84%80%E1%85%A9%E1%84%85%E1%85%B5.png", 1,"미션$i"))
////        }
//
//        missionHistoryAdapter = MissionHistoryAdapter(failuresMissionList, getDisplayWidthSize(), getDisplayHeightSize())
//        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
//        binding.recyclerView.addItemDecoration(
//            MissionHistoryRVDecoration(
//            (getDisplayWidthSize() * 0.03).toInt(),
//            (getDisplayWidthSize() * 0.03).toInt()
//        )
//        )
//        binding.recyclerView.adapter = missionHistoryAdapter
//    }
//
//    //failuresMissionHistory api연결
//    private fun failuresMissionHistoryApi() {
//        // SharedPreferences 객체 가져오기
//        val sharedPreferences = getSharedPreferences("getJwt", Context.MODE_PRIVATE)
//        // JWT 값 가져오기
//        val token = sharedPreferences.getString("jwt", null)
//
//        //val token: String = BuildConfig.API_TOKEN
//        Log.d("debug", "token = "+token+"l");
//
//        failuresMissionList = ArrayList<HistoryMissionList>()
//
//        val service = RetrofitClient.getInstance().create(FailuresMissionService::class.java)
//        val listCall = service.failuresMission(token)
//
//        listCall.enqueue(object : Callback<HistoryMissionResponse> {
//            override fun onResponse(
//                call: Call<HistoryMissionResponse>,
//                response: Response<HistoryMissionResponse>
//            ) {
//                if (response.isSuccessful) {
//                    Log.d("retrofit", response.body().toString());
//                    val missionListResult = response.body()!!.result.missionHomeResList
//
//                    for(i in 0 until missionListResult.size) {
//                        failuresMissionList.add(missionListResult[i])
//                    }
//                    setMissionHistoryAdapter()
//
//
//
//                    val result = response.body()!!.result
//                    binding.scoreTv.text = result.point.toString()
//                    binding.missionCntTv.text = result.missionCnt.toString()
//                    binding.failMissionCntTv.text = result.targetCnt.toString()
//
//                } else {
//                    Log.e("retrofit", "onResponse: Error ${response.code()}")
//                    val errorBody = response.errorBody()?.string()
//                    Log.e("retrofit", "onResponse: Error Body $errorBody")
//                }
//            }
//            override fun onFailure(call: Call<HistoryMissionResponse>, t: Throwable) {
//                Log.e("retrofit", "onFailure: ${t.message}")
//            }
//        })
//    }
//
//    //profileNickName api연결
//    private fun profileNickNameApi() {
//        // SharedPreferences 객체 가져오기
//        val sharedPreferences = getSharedPreferences("getJwt", Context.MODE_PRIVATE)
//        // JWT 값 가져오기
//        val token = sharedPreferences.getString("jwt", null)
//
//        //val token: String = BuildConfig.API_TOKEN
//        Log.d("debug", "token = "+token+"l");
//
//
//
//        val service = RetrofitClient.getInstance().create(ProfileNickNameService::class.java)
//        val listCall = service.profileNickNameMission(token)
//
//        listCall.enqueue(object : Callback<ProfileNickNameResponse> {
//            override fun onResponse(
//                call: Call<ProfileNickNameResponse>,
//                response: Response<ProfileNickNameResponse>
//            ) {
//                if (response.isSuccessful) {
//                    Log.d("retrofit", response.body().toString());
//                    val result = response.body()!!.result
//
//                    binding.nameTv.text = result.userName
//
//
//                } else {
//                    Log.e("retrofit", "onResponse: Error ${response.code()}")
//                    val errorBody = response.errorBody()?.string()
//                    Log.e("retrofit", "onResponse: Error Body $errorBody")
//
//                }
//            }
//            override fun onFailure(call: Call<ProfileNickNameResponse>, t: Throwable) {
//                Log.e("retrofit", "onFailure: ${t.message}")
//            }
//        })
//    }
//
//    //화면 width
//    private fun getDisplayWidthSize(): Int {
//
//        val display = this.applicationContext?.resources?.displayMetrics
//
//        return display?.widthPixels!!
//    }
//    //화면 height
//    private fun getDisplayHeightSize(): Int {
//
//        //statusbarHeight
//        var statusbarHeight = 0
//        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
//        if (resourceId > 0) {
//            statusbarHeight = resources.getDimensionPixelSize(resourceId)
//        }
//
//        //navigationbarHeight
//        val resourceId2 = resources.getIdentifier("navigation_bar_height", "dimen", "android")
//        var navigationbarHeight = 0
//        if (resourceId2 > 0) {
//            navigationbarHeight = resources.getDimensionPixelSize(resourceId)
//        }
//
//        val display = this.applicationContext?.resources?.displayMetrics
//
//        return display?.heightPixels!! + statusbarHeight + navigationbarHeight
//
//    }
//}