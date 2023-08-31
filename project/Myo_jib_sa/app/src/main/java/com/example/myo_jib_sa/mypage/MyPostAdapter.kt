package com.example.myo_jib_sa.mypage

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.databinding.ItemMyPostBinding
import com.example.myo_jib_sa.mypage.API.Post

class MyPostAdapter(
    private val context: Context,
    private val dataList: List<Post>,
) : RecyclerView.Adapter<MyPostAdapter.ViewHolder>() {


    inner class ViewHolder(
        private val binding: ItemMyPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Post) {
            // 다른 바인딩 작업도 추가

            //시간 날짜 포멧
            val formattedTime = item.uploadTime.substring(11, 16)
            val formattedDate = item.uploadTime.substring(5, 7) + "/" + item.uploadTime.substring(8, 10)

            binding.postItemNameTxt.text=item.articleTitle
            binding.postItemUploadTimeTxt.text=formattedTime
            binding.postItemUploadDateTxt.text=formattedDate
            binding.postItmeHeartNumTxt.text=item.likeCount.toString()
            binding.postItmeCommentCntTxt.text=item.commentCount.toString()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemMyPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class CustomItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.top = spaceHeight
            outRect.bottom = spaceHeight
        }
    }

    fun setItemSpacing(recyclerView: RecyclerView, spacing: Int) {
        recyclerView.addItemDecoration(CustomItemDecoration(spacing))
    }
}