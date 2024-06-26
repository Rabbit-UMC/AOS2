package com.example.myo_jib_sa.community.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Constance
import com.example.myo_jib_sa.community.api.missionCert.MCrecyclrImg
import com.example.myo_jib_sa.community.api.missionCert.MissionCertRetrofitManager
import com.example.myo_jib_sa.community.api.missionCert.MissionProofImages
import com.example.myo_jib_sa.community.missionCert.MissionPictureActivity
import com.example.myo_jib_sa.databinding.ItemMissionCertificationImgBinding

class MissionCertAdapter(
    private val context: Context,
    private val dataList:List<MCrecyclrImg>,
    private val date:Int,
    private val mainMissionId:Long
    )
    : RecyclerView.Adapter<MissionCertAdapter.ViewHolder>(){

    private var doubleTap = false
    private val doubleTapDelay: Long = 800 // 0.8초안에 두번 눌러야 인식
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var sharedPreferences: SharedPreferences


    //뷰홀더
    inner class ViewHolder(
        private val binding: ItemMissionCertificationImgBinding
    )
        : RecyclerView.ViewHolder(binding.root){
        init {
            // Initialize sharedPreferences inside the ViewHolder constructor
            sharedPreferences = itemView.context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        }
        fun bind(item: MCrecyclrImg, position:Int){

            //둥근 모서리
            binding.missionImg1.clipToOutline=true
            binding.missionImg2.clipToOutline=true
            binding.missionImg3.clipToOutline=true

            //좋아요 설정
            setLike(item, binding)

            //이미지 설정
            if(item.data1.filePath=="empty"){
                binding.missionImg1.visibility=View.GONE
                binding.missionHeart1.visibility=View.GONE
                binding.like1Txt.visibility=View.GONE
                binding.constraintLayout1.background = ColorDrawable(Color.TRANSPARENT)
                binding.view1.visibility=View.INVISIBLE
            }else{
                Log.d("미션 리사이클러 이미지 확인",item.data1.filePath )
                Glide.with(context)
                    .load(item.data1.filePath)
                    .into(binding.missionImg1)
                binding.like1Txt.text=item.data1.likeCount.toString()
            }

            if(item.data2.filePath=="empty"){
                binding.missionImg2.visibility=View.INVISIBLE
                binding.missionHeart2.visibility=View.GONE
                binding.like2Txt.visibility=View.GONE
                binding.constraintLayout2.background = ColorDrawable(Color.TRANSPARENT)
                binding.view2.visibility=View.INVISIBLE
            }else{
                Log.d("미션 리사이클러 이미지 확인",item.data2.filePath )
                Glide.with(context)
                    .load(item.data2.filePath)
                    .into(binding.missionImg2)
                binding.like2Txt.text=item.data2.likeCount.toString()
            }

            if(item.data3.filePath=="empty"){
                binding.missionImg3.visibility=View.INVISIBLE
                binding.missionHeart3.visibility=View.GONE
                binding.like3Txt.visibility=View.GONE
                binding.constraintLayout3.background = ColorDrawable(Color.TRANSPARENT)
                binding.view3.visibility=View.INVISIBLE
            }else{
                Log.d("미션 리사이클러 이미지 확인",item.data3.filePath )
                Glide.with(context)
                    .load(item.data3.filePath)
                    .into(binding.missionImg3)
                binding.like3Txt.text=item.data3.likeCount.toString()
            }

            //클릭 이벤트
            imgTouch(binding.missionImg1, item.data1, 3*position, mainMissionId)
            imgTouch(binding.missionImg2, item.data2, 3*position+1, mainMissionId)
            imgTouch(binding.missionImg3, item.data3, 3*position+2, mainMissionId)
        }
    }

    //처음 좋아요 상태 설정
    private fun setLike(item: MCrecyclrImg, binding: ItemMissionCertificationImgBinding){

        if(item.data1.isLike){
            binding.missionHeart1.setImageResource(R.drawable.ic_like)
            sharedPreferences.edit().putBoolean("isLiked_${item.data1.imageId}", true).apply()
        }else{
            binding.missionHeart1.setImageResource(R.drawable.ic_unlike)
            sharedPreferences.edit().putBoolean("isLiked_${item.data1.imageId}", false).apply()
        }

        if(item.data2.isLike){
            binding.missionHeart2.setImageResource(R.drawable.ic_like)
            sharedPreferences.edit().putBoolean("isLiked_${item.data2.imageId}", true).apply()
        }else{
            binding.missionHeart2.setImageResource(R.drawable.ic_unlike)
            sharedPreferences.edit().putBoolean("isLiked_${item.data2.imageId}", false).apply()
        }

        if(item.data3.isLike){
            binding.missionHeart3.setImageResource(R.drawable.ic_like)
            sharedPreferences.edit().putBoolean("isLiked_${item.data3.imageId}", true).apply()
        }else{
            binding.missionHeart3.setImageResource(R.drawable.ic_unlike)
            sharedPreferences.edit().putBoolean("isLiked_${item.data3.imageId}", false).apply()
        }
    }

    //클릭 이벤트
    private fun imgTouch(img:ImageView, data:MissionProofImages, position:Int, mainMissionId:Long){
        img.setOnClickListener {
            val intent=Intent(context, MissionPictureActivity::class.java)
            intent.putExtra("filePath", data.filePath)
            intent.putExtra("imgId", data.imageId)
            intent.putExtra("isLike", data.isLike)
            intent.putExtra("likeCount", data.likeCount)
            intent.putExtra("isReportable", true)
            intent.putExtra("position", position)
            intent.putExtra("date", date)
            intent.putExtra("mainMissionId", mainMissionId)
            context.startActivity(intent)
        }
    }

    //토스트 메시지
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // 뷰홀더를 생성하여 반환
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMissionCertificationImgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    //뷰홀더 데이터 설정
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item, position)
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
