package com.example.myo_jib_sa.community.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.community.post.PostActivity
import com.example.myo_jib_sa.community.api.BoardPost.Articles
import com.example.myo_jib_sa.databinding.ItemPostBinding

class BoardAdapter(
    private val context: Context,
    private val dataList:MutableList<Articles>,
    private val boardId:Long)
    : RecyclerView.Adapter<BoardAdapter.ViewHolder>(){

    //뷰홀더
    inner class ViewHolder(
        private val binding: ItemPostBinding
    )
        : RecyclerView.ViewHolder(binding.root){
        fun bind(item: Articles){
            binding.postItemNameTxt.text=item.articleTitle
            binding.postItmeCommentCntTxt.text=item.commentCount.toString()
            binding.postItmeHeartNumTxt.text=item.likeCount.toString()
            binding.postItemUploadTimeTxt.text=item.uploadTime



            //클릭 이벤트, 해당 게시물로 이동
           binding.root.setOnClickListener{
               val postId=item.articleId
               val intent= Intent(context, PostActivity::class.java)
               intent.putExtra("postId", postId)
               intent.putExtra("boardId", boardId)
               binding.root.context.startActivity(intent)
            }
        }
    }

    // 뷰홀더를 생성하여 반환
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    //뷰홀더 데이터 설정
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    //아이템 개수
    override fun getItemCount(): Int {
        return dataList.size
    }

    //간격 설정을 위한 클래스
    class CustomItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.top = spaceHeight // 아이템 사이의 간격을 설정 (위 넓이)
            outRect.bottom=spaceHeight //아래
        }
    }
    // 아이템 간격 설정을 위한 메소드
    fun setItemSpacing(recyclerView: RecyclerView, spacing: Int) {
        recyclerView.addItemDecoration(CustomItemDecoration(spacing))
    }
    // 기존 데이터를 업데이트하는 메서드
    fun updateData(newDataList: List<Articles>) {
        dataList.addAll(newDataList) // 새로운 데이터로 갱신합니다.
        notifyDataSetChanged() // 어댑터에 데이터가 변경되었음을 알립니다.
    }

    //데이터 리셋 후 추가
    fun resetUpdateData(newDataList: List<Articles>) {
        dataList.clear() // 기존 데이터를 제거합니다.
        dataList.addAll(newDataList) // 새로운 데이터로 갱신합니다.
        notifyDataSetChanged() // 어댑터에 데이터가 변경되었음을 알립니다.
    }

}