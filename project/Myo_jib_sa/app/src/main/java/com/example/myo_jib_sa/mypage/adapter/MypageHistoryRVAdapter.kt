package com.example.myo_jib_sa.mypage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.databinding.ItemMypageHistoryMissionBinding
import com.example.myo_jib_sa.mypage.api.GetHistoryResponse

class MypageHistoryRVAdapter(private val items: List<GetHistoryResponse>) : RecyclerView.Adapter<MypageHistoryRVAdapter.ViewHolder>() {
    inner class ViewHolder(
        private val binding: ItemMypageHistoryMissionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GetHistoryResponse) {
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


}