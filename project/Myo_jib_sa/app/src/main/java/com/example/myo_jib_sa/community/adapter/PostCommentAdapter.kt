package com.example.myo_jib_sa.community.adapter

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.community.Retrofit.communityHome.MainMission
import com.example.myo_jib_sa.community.Retrofit.post.CommentList
import com.example.myo_jib_sa.databinding.ItemCommentBinding
import com.example.myo_jib_sa.databinding.ItemCommunityMissionBinding

class PostCommentAdapter(
    private val context: Context,
    private val dataList:List<CommentList>)
    : RecyclerView.Adapter<PostCommentAdapter.ViewHolder>(){

    //뷰홀더
    inner class ViewHolder(
        private val binding: ItemCommentBinding
    )
        : RecyclerView.ViewHolder(binding.root){
        fun bind(item: CommentList){
            //댓글 작성자 이름, 내용 세팅
            binding.commentWriterNameTxt.text=item.commentAuthorName
            binding.commentPostTextTxt.text=item.commentContent

            //프로필 이미지 설정
            Glide.with(context)
                 .load(item.commentAuthorProfileImage)
                 .into(binding.commentProfileImg)

            //commentBtn 아이콘 상태 설정

            //클릭 이벤트
            binding.commentBtn.setOnClickListener {
                //삭제, 글 바꾸기 (글 작성자, 댓글 작성자에 따라 기능 다르게 설정)
            }

            //바뀐 글인지 아닌지 상태 체크 후 내용 색상 바꾸기

            }
        }


    // 뷰홀더를 생성하여 반환
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        Log.d("리사이클러뷰","linkMrecyclr 어댑터 뷰홀더")
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
            outRect.bottom = spaceHeight // 아이템 사이의 간격을 설정 (아래
        }
    }
    // 아이템 간격 설정을 위한 메소드
    fun setItemSpacing(recyclerView: RecyclerView, spacing: Int) {
        recyclerView.addItemDecoration(CustomItemDecoration(spacing))
    }

}