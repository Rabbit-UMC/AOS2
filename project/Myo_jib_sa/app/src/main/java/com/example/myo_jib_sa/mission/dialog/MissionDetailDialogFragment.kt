package com.example.myo_jib_sa.mission.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.sRetrofit
import com.example.myo_jib_sa.databinding.DialogMissionDetailBinding
import com.example.myo_jib_sa.mission.api.*
import com.example.myo_jib_sa.mission.api.Mission
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class MissionDetailDialogFragment(private val item: Mission, private val context: Context) : DialogFragment() {
    private lateinit var binding:DialogMissionDetailBinding
    private val retrofit = sRetrofit.create(MissionAPI::class.java)
    private var listener: DetailDialogListener? = null

    interface DetailDialogListener {
        fun onMissionWith(message: String, isSuccess: Boolean)
    }
    fun setDetailDialogListener(listener: DetailDialogListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogMissionDetailBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setDialogSize()
        initDialogInfo()
        initListener()
    }

    private fun setDialogSize() {
        dialog?.let { dialog ->
            val metrics = resources.displayMetrics
            val density = metrics.density
            val marginPx = (DIALOG_MARGIN_DP * density * 2).toInt()
            val width = metrics.widthPixels - marginPx
            val height = (DIALOG_HEIGHT_DP * density).toInt()

            val layoutParams = WindowManager.LayoutParams().apply {
                copyFrom(dialog.window?.attributes)
                this.width = width
                this.height = height
            }

            dialog.window?.apply {
                attributes = layoutParams
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
    }


    private fun initDialogInfo() {
        retrofit.getMissionDetail(item.missionId).enqueue(object : Callback<MissionDetailResponse> {
            override fun onResponse(
                call: Call<MissionDetailResponse>,
                response: Response<MissionDetailResponse>
            ) {
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val detailData = response.body()?.result
                    val density = context.resources.displayMetrics.density
                    val cornerRadiusPx = 16 * density
                    Log.d("getMissionDetail", "${response.body()}")
                    with(binding) {
                        Glide.with(context)
                            .load(detailData?.image)
                            .error(R.drawable.ic_curmission_myo_icon)
                            .transform(CenterCrop(), GranularRoundedCorners(cornerRadiusPx, cornerRadiusPx, 0F, 0F))
                            .into(missionDetailTitleIv)

                        missionDetailCategoryTxt.text = detailData?.categoryTitle
                        missionDetailTitleTxt.text = detailData?.title
                        missionDetailStartDateTxt.text = formatDate(detailData?.startAt)
                        missionDetailEndDateTxt.text = formatDate(detailData?.endAt)
                        missionDetailMemoDescTxt.text = detailData?.content

                        missionWithBtn.isEnabled = detailData?.alreadyIn == false
                    }
                } else {
                    // API 호출 실패 시 처리
                    Log.d("getMissionDetail", "response.isSuccessful fail")
                }
            }

            override fun onFailure(call: Call<MissionDetailResponse>, t: Throwable) {
                // API 호출 실패 시 처리
                Log.d("getMissionDetail", "onFailure $t")
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
    private fun initListener() {
        binding.missionWithBtn.setOnClickListener {
            // 미션 같이하기 api 연결 로직
            Log.d("home","{같이하기 ID: ${item.missionId}")
            retrofit.postMissionWith(item.missionId).enqueue(object : Callback<MissionWithResponse> {
                override fun onResponse(call: Call<MissionWithResponse>, response: Response<MissionWithResponse>) {
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        Log.d("postMissionWith", "response success ${response.body()}")
                        listener?.onMissionWith(SUCCESS_MESSAGE, true)
                    } else {
                        Log.d("postMissionWith", "response.isSuccessful fail")
                        listener?.onMissionWith(ERROR_MESSAGE, false)
                    }

                }
                override fun onFailure(call: Call<MissionWithResponse>, t: Throwable) {
                    Log.d("postMissionWith", "onFailure $t")
                    listener?.onMissionWith(ERROR_MESSAGE, false)
                }
            })
        }

        binding.dialogExitBtn.setOnClickListener{
            dismiss()
        }
    }


    companion object {
        const val DIALOG_MARGIN_DP = 20
        const val DIALOG_HEIGHT_DP = 612
        const val ERROR_MESSAGE = "오류가 발생했습니다. 다시 시도해주세요."
        const val SUCCESS_MESSAGE = "해당 미션을 수락하셨습니다!"
    }
}