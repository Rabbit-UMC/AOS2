package com.example.myo_jib_sa.community.adapter

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.community.Retrofit.post.ArticleImage
import com.example.myo_jib_sa.community.Retrofit.post.CommentList
import com.example.myo_jib_sa.databinding.ItemCommentBinding
import com.example.myo_jib_sa.databinding.ItemPostImgBinding

class PostImgAdapter(
    private val context: Context,
    private val dataList:List<ArticleImage>)
    : RecyclerView.Adapter<PostImgAdapter.ViewHolder>(){

    //뷰홀더
    inner class ViewHolder(
        private val binding: ItemPostImgBinding
    )
        : RecyclerView.ViewHolder(binding.root){
        fun bind(item: ArticleImage){

            //이미지 설정
            Glide.with(context)
                .load(item.filePath)
                .into(binding.postImgImg)

            //클릭 이벤트
            binding.postImgImg.setOnClickListener {
                //누르면 사진 자세히 보기
            }


        }
    }


    // 뷰홀더를 생성하여 반환
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostImgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    //뷰홀더 데이터 설정
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    //아이템 개수
    override fun getItemCount(): Int {
        Log.d("리사이클러뷰 사이즈", "${dataList.size}")
        return dataList.size
    }

    //간격 설정을 위한 클래스
    class CustomItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.right = spaceHeight // 아이템 사이의 간격을 설정 (오른쪽
        }
    }
    // 아이템 간격 설정을 위한 메소드
    fun setItemSpacing(recyclerView: RecyclerView, spacing: Int) {
        recyclerView.addItemDecoration(CustomItemDecoration(spacing))
    }

}