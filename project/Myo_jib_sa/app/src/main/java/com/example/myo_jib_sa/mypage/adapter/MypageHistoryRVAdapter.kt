package com.example.myo_jib_sa.mypage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.ItemMypageHistoryMissionBinding
import com.example.myo_jib_sa.mypage.api.GetHistoryResponse
import com.example.myo_jib_sa.mypage.api.UserMissionResDto
import java.text.SimpleDateFormat
import java.util.Locale

class MypageHistoryRVAdapter(private val items: List<UserMissionResDto>, private val isSuccess: Boolean) : RecyclerView.Adapter<MypageHistoryRVAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemMypageHistoryMissionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserMissionResDto) {
            with(binding) {
                if (item.missionId == -1) {
                    configureVisibility(isSuccess)
                    setMissionImage(
                        if(isSuccess) R.drawable.background_mypage_history_success_default else R.drawable.background_mypage_history_fail_default
                    )
                } else {
                    setItemDetails(item)
                    setMissionImage(item.image)
                    historyMissionInfoExistCl.visibility = View.VISIBLE
                    historyMissionInfoSuccessCl.visibility = View.GONE
                    historyMissionInfoFailCl.visibility = View.GONE
                }
            }
        }

        private fun setMissionImage(image: Any) {
            Glide.with(binding.historyMissionTitleIv)
                .load(image)
                .error(R.drawable.ic_app_logo)
                .into(binding.historyMissionTitleIv)
        }
        private fun configureVisibility(isSuccess: Boolean) {
            with(binding) {
                historyMissionInfoExistCl.visibility = View.GONE
                historyMissionInfoSuccessCl.visibility = if (isSuccess) View.VISIBLE else View.GONE
                historyMissionInfoFailCl.visibility = if (!isSuccess) View.VISIBLE else View.GONE
            }
        }

        private fun setItemDetails(item: UserMissionResDto) {
            with(binding) {
                historyMissionTitleTv.text = item.title
                historyMissionStartDateTv.text = convertDateFormat(item.startAt)
                historyMissionEndDateTv.text = convertDateFormat(item.endAt)
                historyMissionWholeCntTv.text = item.challengerCnt.toString()
                historyMissionAchieveCntTv.text = item.successCnt.toString()
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMypageHistoryMissionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    private fun convertDateFormat(dateStr: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("M월 dd일", Locale.getDefault())

        val date = inputFormat.parse(dateStr)
        return if (date != null) outputFormat.format(date) else ""
    }
}