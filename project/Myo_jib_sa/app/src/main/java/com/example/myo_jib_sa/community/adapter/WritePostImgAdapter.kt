package com.example.myo_jib_sa.community.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.community.Retrofit.post.ImageList
import com.example.myo_jib_sa.community.Retrofit.post.ImageListC
import com.example.myo_jib_sa.community.WritePostingActivity
import com.example.myo_jib_sa.databinding.ItemWritePostImglockBinding
import com.example.myo_jib_sa.databinding.ItemWritePostImgplusBinding


class WritePostImgAdapter(
    private val context: Context
    ,private val imgList:List<ImageListC>
)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    //사진 추가, 잠김 구별 상수
    val LOCK =2
    val PLUS =0

    var imagePath: List<ImageListC> = imgList
    private var ps:Int=2

    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
    }

    //뷰홀더 (사진 추가하기)
    inner class plusViewHolder(
        private val binding: ItemWritePostImgplusBinding
    ): RecyclerView.ViewHolder(binding.root){
        val writepostImg: ImageView = binding.missionCertImg
        init {
            binding.writePostPlusBtn.setOnClickListener {
                // 갤러리에서 이미지 선택하기
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                (context as Activity).startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
            }
        }
    }

    //뷰홀더 (잠긴 사진)
    inner class lockViewHolder(
        private val binding: ItemWritePostImglockBinding
    ): RecyclerView.ViewHolder(binding.root){
    }



    // 뷰홀더를 생성하여 반환
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  RecyclerView.ViewHolder {
        return when(viewType){
            PLUS->{
                val binding = ItemWritePostImgplusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                plusViewHolder(binding)
            }
            LOCK->{
                val binding = ItemWritePostImglockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                lockViewHolder(binding)
            }
            else-> throw IllegalArgumentException("Invalid viewType: $viewType")

        }

    }

    //뷰홀더 데이터 설정
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // 이미지 설정 로직을 추가해야 함
        if (holder is plusViewHolder) {
            val plusViewHolder = holder as plusViewHolder
             // 이미지 위치에 해당하는 리스트 인덱스 계산

            val imagePath = imagePath[position] // 해당 포지션의 이미지 경로를 가져옴
            ps=holder.adapterPosition

            if (imagePath != null) {
                // 이미지 경로가 설정되어 있으면 Glide를 사용하여 이미지 로드 및 표시
                Glide.with(context)
                    .load(imagePath)
                    .into(plusViewHolder.writepostImg)
            } else {
            }

        } else if (holder is lockViewHolder) {
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(position==0 || position==1){
            PLUS
        }else{
            LOCK
        }
    }

    //아이템 개수
    override fun getItemCount(): Int {
        return 4
    }

    //간격 설정을 위한 클래스
    class CustomItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.right = spaceHeight // 아이템 사이의 간격을 설정
        }
    }
    // 아이템 간격 설정을 위한 메소드
    fun setItemSpacing(recyclerView: RecyclerView, spacing: Int) {
        recyclerView.addItemDecoration(CustomItemDecoration(spacing))
    }

    // 이미지 경로 설정
    fun setImagePath(path: String?) {
        val updatedList = imagePath.toMutableList()
        if (ps < updatedList.size) {
            updatedList[ps] = ImageListC(path.toString())
        } else {
            updatedList.add(ImageListC(path.toString()))
        }
        imagePath = updatedList
        notifyDataSetChanged()
    }

}

