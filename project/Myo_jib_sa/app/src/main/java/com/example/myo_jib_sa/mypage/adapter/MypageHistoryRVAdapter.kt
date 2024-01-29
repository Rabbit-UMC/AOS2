package com.example.myo_jib_sa.mypage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.ItemMypageHistoryMissionBinding
import com.example.myo_jib_sa.mypage.api.GetHistoryResponse
import com.example.myo_jib_sa.mypage.api.UserMissionResDto
import java.text.SimpleDateFormat
import java.util.Locale

class MypageHistoryRVAdapter(private val items: List<UserMissionResDto>) : RecyclerView.Adapter<MypageHistoryRVAdapter.ViewHolder>() {
    inner class ViewHolder(
        private val binding: ItemMypageHistoryMissionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserMissionResDto) {
            with(binding) {
                Glide.with(historyMissionTitleIv)
                    .load(item.image)
                    .error(R.drawable.ic_app_logo)
                    .into(historyMissionTitleIv)
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