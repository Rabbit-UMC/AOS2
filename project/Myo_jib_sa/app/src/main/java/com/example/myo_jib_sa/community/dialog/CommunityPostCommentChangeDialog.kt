package com.example.myo_jib_sa.community.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.base.MyojibsaApplication
import com.example.myo_jib_sa.community.Constance
import com.example.myo_jib_sa.community.api.post.PostRetrofitManager
import com.example.myo_jib_sa.databinding.DialogMissionReportFragmentBinding
import com.example.myo_jib_sa.mission.api.MissionAPI

class CommunityPostCommentChangeDialog (private val commentId: Long) : DialogFragment() {
    private lateinit var binding: DialogMissionReportFragmentBinding
    private val retrofit = MyojibsaApplication.sRetrofit.create(MissionAPI::class.java)
    private var listener: ReportDialogListener? = null
    companion object {
        const val DIALOG_MARGIN_DP = 20
        const val DIALOG_HEIGHT_DP = 248
    }


    interface ReportDialogListener {
        fun onReportSubmitted(message: String)
    }

    fun setReportDialogListener(listener: ReportDialogListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogMissionReportFragmentBinding.inflate(inflater, container, false)

        initListener()

        binding.missionReportTitleTxt.text=resources.getString(R.string.post_comment_chage_title)
        binding.missionReportDescTxt.text=resources.getString(R.string.post_comment_chage_decs)
        binding.missionReportYesBtn.text=resources.getString(R.string.post_comment_chage_yes)
        binding.missionReportNoBtn.text=resources.getString(R.string.post_comment_chage_no)

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }

    override fun onResume() {
        super.onResume()
        // 다이얼로그의 크기 설정
        setDialogSize()

        binding.missionReportTitleTxt.text=resources.getString(R.string.post_comment_chage_title)
        binding.missionReportDescTxt.text=resources.getString(R.string.post_comment_chage_decs)
        binding.missionReportYesBtn.text=resources.getString(R.string.post_comment_chage_yes)
        binding.missionReportNoBtn.text=resources.getString(R.string.post_comment_chage_no)
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

    private fun initListener() {
        //미션 report api 호출
        binding.missionReportNoBtn.setOnClickListener {
            dismiss()
        }

        binding.missionReportYesBtn.setOnClickListener {
            val retrofitManager = PostRetrofitManager.getInstance(requireContext())
                retrofitManager.postCommentLock(commentId){ response ->
                    if(response){
                        Log.d("댓글 변경", "${response.toString()}")
                        listener?.onReportSubmitted("댓글이 변경되었어요.")

                    } else {
                        Log.d("댓글 변경 API isSuccess가 false", "${response.toString()}")
                        listener?.onReportSubmitted("오류가 발생했습니다. 다시 시도해주세요.")
                        Log.d("report", "신고가 접수되었어요.")
                    }

                }

        }
    }
}