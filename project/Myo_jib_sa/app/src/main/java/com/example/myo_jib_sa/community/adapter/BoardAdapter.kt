package com.example.myo_jib_sa.community.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.community.PostActivity
import com.example.myo_jib_sa.community.Retrofit.BoardPost.Articles
import com.example.myo_jib_sa.databinding.ActivityPostBinding
import com.example.myo_jib_sa.databinding.ItemPostBinding

class BoardAdapter(
    private val context: Context,
    private val dataList:List<Articles>,
    private val boardId:Int)
    : RecyclerView.Adapter<BoardAdapter.ViewHolder>(){

    //뷰홀더
    inner class ViewHolder(
        private val binding: ItemPostBinding
    )
        : RecyclerView.ViewHolder(binding.root){
        fun bind(item: Articles){
            binding.postItemNameTxt.text=item.articleTitle
            binding.postItmeCommentCntTxt.text=item.commentCount.toString()
            binding.postItemUploadTimeTxt.text=item.uploadTime.toString()
            binding.postItmeHeartNumTxt.text=item.likeCount.toString()


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

}