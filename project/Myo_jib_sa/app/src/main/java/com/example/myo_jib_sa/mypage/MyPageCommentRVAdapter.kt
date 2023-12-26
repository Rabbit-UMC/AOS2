package com.example.myo_jib_sa.mypage

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.databinding.ItemMyPagePostBinding
import com.example.myo_jib_sa.mypage.API.GetMyCommentResult

class MyPageCommentRVAdapter (
    private val context: Context,
    private val dataList: List<GetMyCommentResult>,
) : RecyclerView.Adapter<MyPageCommentRVAdapter.ViewHolder>() {


    inner class ViewHolder(
        private val binding: ItemMyPagePostBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GetMyCommentResult) {
            binding.myPageTabPostTitleTxt.text=item.articleTitle
            binding.myPageTabLikeCntTxt.text=item.likeCount.toString()
            binding.myPageTabCommentCntTxt.text=item.commentCount.toString()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemMyPagePostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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