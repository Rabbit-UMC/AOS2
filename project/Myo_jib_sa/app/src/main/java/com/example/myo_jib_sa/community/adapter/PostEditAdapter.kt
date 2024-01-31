package com.example.myo_jib_sa.community.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.community.post.PostPictureActivity
import com.example.myo_jib_sa.community.api.post.ImageList
import com.example.myo_jib_sa.databinding.ItemPostImgBinding
import com.google.gson.Gson


class PostEditAdapter (
    private val context: Context,
    private val dataList:List<String>)
    : RecyclerView.Adapter<PostEditAdapter.ViewHolder>(){

    private var itemClickListener: OnItemClickListener? = null


    //뷰홀더
    inner class ViewHolder(
        private val binding: ItemPostImgBinding
    )
        : RecyclerView.ViewHolder(binding.root){

        init {

            // 이미지 클릭 이벤트
            binding.postImgImg.setOnClickListener {
                itemClickListener?.onImageClick(adapterPosition)
            }

            // 삭제 버튼 클릭 이벤트
            binding.postImgDelete.setOnClickListener {
                itemClickListener?.onDeleteClick(adapterPosition)
            }
        }

        fun bind(item: List<String>, position: Int){

            //이미지 설정
            if(item[position].isNotBlank()){
                Glide.with(context)
                    .load(item[position])
                    .into(binding.postImgImg)
            }else{
                binding.postImgImg.visibility= View.GONE
            }

            //클릭 이벤트
            binding.postImgImg.setOnClickListener {
                //누르면 사진 자세히 보기
                val intent= Intent(context, PostPictureActivity::class.java)
                intent.putExtra("itemListJson", Gson().toJson(item))
                intent.putExtra("current", position)
                context.startActivity(intent)
            }

            binding.postImgDelete.setOnClickListener { //삭제
                binding.postImgImg.setImageURI(null)
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
        holder.bind(dataList, position)
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


    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }
    interface OnItemClickListener {
        fun onImageClick(position: Int)
        fun onDeleteClick(postition:Int)
    }
}