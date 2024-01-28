package com.example.myo_jib_sa.schedule.currentMissionActivity

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.sRetrofit
import com.example.myo_jib_sa.databinding.DialogCurrentMissionDetailBinding
import com.example.myo_jib_sa.schedule.api.MissionAPI
import com.example.myo_jib_sa.schedule.api.MyMissionDetailResponse
import com.example.myo_jib_sa.schedule.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale


class CurrentMissionDetailDialog(private val missionId : Long) : DialogFragment() {
    private lateinit var binding: DialogCurrentMissionDetailBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogCurrentMissionDetailBinding.inflate(inflater, container, false)

                // 레이아웃 배경을 투명하게 해줌
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)


        //x누르면 dialog종료
        binding.exitBtn.setOnClickListener {
            dismiss()
        }

        //missionDetail api연결
        missionDetailApi(missionId)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        resizeDialog()
    }


    //dialog크기 조절
    fun resizeDialog() {
        val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        val deviceHeight = size.y

        params?.width = (deviceWidth * 0.9).toInt()
        params?.height = (deviceWidth * 0.9 * 1.77).toInt()

        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    //dp -> px
    fun ConvertDPtoPX(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }





    //missionDetail api연결
    private fun missionDetailApi(missionId: Long) {
        sRetrofit.create(MissionAPI::class.java).getMyMissionDetail(missionId).enqueue(object : Callback<MyMissionDetailResponse> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<MyMissionDetailResponse>,
                response: Response<MyMissionDetailResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("debug", "retrofit: " + response.body().toString());
                    val result = response.body()!!.result

                    binding.missionTitleTv.text = result.missionTitle
                    binding.missionCategoryTv.text = result.categoryTitle
                    binding.missionStartDateTv.text = formatDate(result.startAt)
                    binding.missionEndDateTv.text = formatDate(result.endAt)
                    binding.missionMemoTv.text = result.content

                    when(result.categoryId){
                        1L->binding.missionCategoryTv.setBackgroundResource(R.drawable.view_round_r12_gray4)
                        2L->binding.missionCategoryTv.setBackgroundResource(R.drawable.view_round_r12_main2)
                        3L->binding.missionCategoryTv.setBackgroundResource(R.drawable.view_round_r12_main4)
                        else -> binding.missionCategoryTv.setBackgroundResource(R.drawable.view_round_r12_gray4)
                    }

                    Glide.with(this@CurrentMissionDetailDialog)
                        .load(result.image)
                        .error(R.drawable.ic_currentmission_free) //에러시 보여줄 이미지
                        .fallback(R.drawable.ic_currentmission_free) //load할 url이 비어있을 경우 보여줄 이미지
                        .into(binding.missionTitleIv)//이미지 설정

                } else {
                    Log.e("retrofit", "onResponse: Error ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("retrofit", "onResponse: Error Body $errorBody")
                }
            }
            override fun onFailure(call: Call<MyMissionDetailResponse>, t: Throwable) {
                Log.e("retrofit", "onFailure: ${t.message}")
            }
        })
    }


    private fun formatDate(inputDate: String?): String? {
        // 입력 형식과 출력 형식 정의
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("M'월' dd'일'", Locale.getDefault())

        return try {
            // 입력 날짜를 파싱하고 새로운 형식으로 포맷합니다.
            val date = inputFormat.parse(inputDate)
            date?.let { outputFormat.format(it) } // null이 아닌 경우에만 포맷 변환
        } catch (e: Exception) {
            e.printStackTrace()
            null // 파싱 오류가 발생한 경우
        }
    }
    //startAt, endAt 포맷: yyyy.mm.dd
    fun missionDateFormatter(startAt: String?): String {
        val formatter = DecimalFormat("00")

        val time = startAt!!.split("-")
        val year = time[0].toInt()
        val month = time[1].toInt()
        val day = time[2].toInt()

        return "$year.${formatter.format(month)}.${formatter.format(day)}"
    }

    //미션 기간 구하기: 시작일 넣는 방식으로 계산 즉, startAt-endAt+1
    @RequiresApi(Build.VERSION_CODES.O)
    fun calMissionPeriod(startAt: String?, endAt: String?): Long{
        val start = startAt!!.split("-")
        val end = endAt!!.split("-")

        val startyear = start[0].toInt()
        val startmonth = start[1].toInt()
        val startday = start[2].toInt()

        val endyear = end[0].toInt()
        val endmonth = end[1].toInt()
        val endday = end[2].toInt()

        // 시작 날짜와 종료 날짜 생성
        val startDate = LocalDate.of(startyear, startmonth, startday)
        val endDate = LocalDate.of(endyear, endmonth, endday)

        return ChronoUnit.DAYS.between(startDate, endDate) + 1

    }

}