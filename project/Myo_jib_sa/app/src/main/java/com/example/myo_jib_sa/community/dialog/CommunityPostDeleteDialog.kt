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
import com.example.myo_jib_sa.databinding.DialogCommunityBlueWhiteBinding
import com.example.myo_jib_sa.databinding.DialogMissionReportFragmentBinding
import com.example.myo_jib_sa.mission.api.Mission
import com.example.myo_jib_sa.mission.api.MissionAPI
import com.example.myo_jib_sa.mission.api.MissionReportResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunityPostDeleteDialog (private val postId: Long) : DialogFragment() {
    private lateinit var binding: DialogCommunityBlueWhiteBinding
    private var listener: DeleteDialogListener? = null
    companion object {
        const val DIALOG_MARGIN_DP = 20
        const val DIALOG_HEIGHT_DP = 248
    }


    interface DeleteDialogListener {
        fun onDeleteSubmitted(message: String)
    }

    fun setDeleteDialogListener(listener: DeleteDialogListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogCommunityBlueWhiteBinding.inflate(inflater, container, false)

        binding.missionReportTitleTxt.text=resources.getString(R.string.post_delete_title)
        binding.missionReportDescTxt.text=resources.getString(R.string.post_delete_decs)
        binding.missionReportYesBtn.text=resources.getString(R.string.post_delete_yes)
        binding.missionReportNoBtn.text=resources.getString(R.string.post_delete_no)

        initListener()

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

        binding.missionReportTitleTxt.text=resources.getString(R.string.post_delete_title)
        binding.missionReportDescTxt.text=resources.getString(R.string.post_delete_decs)
        binding.missionReportYesBtn.text=resources.getString(R.string.post_delete_yes)
        binding.missionReportNoBtn.text=resources.getString(R.string.post_delete_no)

        setDialogSize()
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
        //post delete api 호출
        binding.missionReportNoBtn.setOnClickListener {
            dismiss()
        }

        binding.missionReportYesBtn.setOnClickListener {

            val retrofitManager = PostRetrofitManager.getInstance(requireContext())
                retrofitManager.postDelete(postId) { response ->
                    if (response) {
                        //로그
                        Log.d("게시물 삭제", "${response.toString()}")
                        listener?.onDeleteSubmitted("게시글이 삭제되었어요.")
                        Log.d("report", "게시글이 삭제되었어요.")

                    } else {
                        // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                        Log.d("게시물 삭제 API isSuccess가 false", "${response.toString()}")
                        Log.d("report", "게시글 삭제에 실패 했어요")
                        listener?.onDeleteSubmitted("오류가 발생했습니다. 다시 시도해주세요.")
                    }
                }

        }
    }
}