package com.example.myo_jib_sa.community.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Rect
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
import com.example.myo_jib_sa.community.PostActivity
import com.example.myo_jib_sa.community.Retrofit.BoardPost.Articles
import com.example.myo_jib_sa.community.Retrofit.Constance
import com.example.myo_jib_sa.community.Retrofit.communityHome.CommunityHomeManager
import com.example.myo_jib_sa.community.Retrofit.communityHome.MainMission
import com.example.myo_jib_sa.community.Retrofit.communityHome.PopularArticle
import com.example.myo_jib_sa.community.Retrofit.missionCert.MCrecyclrImg
import com.example.myo_jib_sa.community.Retrofit.missionCert.MissionCertRetrofitManager
import com.example.myo_jib_sa.databinding.ItemMissionCertificationImgBinding

class MissionCertAdapter(
    private val context: Context,
    private val dataList:List<MCrecyclrImg>)
    : RecyclerView.Adapter<MissionCertAdapter.ViewHolder>(){

    private var doubleTap = false
    private val doubleTapDelay: Long = 1000 // 1초 (1초 동안 길게 눌러야 두 번 터치로 인식)
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
        fun bind(item: MCrecyclrImg){
            //이미지 설정
            Glide.with(context)
                .load(item.data1.filePath)
                .into(binding.missionImg1)
            Glide.with(context)
                .load(item.data2.filePath)
                .into(binding.missionImg2)
            Glide.with(context)
                .load(item.data3.filePath)
                .into(binding.missionImg3)

            //클릭 이벤트
            imgTouch(binding.missionImg1, item.data1.imageId)
            imgTouch(binding.missionImg2, item.data2.imageId)
            imgTouch(binding.missionImg3, item.data3.imageId)
        }
    }

    //클릭 이벤트
    private fun imgTouch(img:ImageView, imgId:Int){
        img.setOnTouchListener(object : View.OnTouchListener {
            private var numTaps = 0

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        numTaps++
                        if (numTaps == 1) {
                            handler.postDelayed({
                                if (!doubleTap) {
                                    //todo: dialog 띄우고 확인 눌렀을 떄만 신고하기
                                    report(Constance.jwt, imgId)
                                }
                                numTaps = 0
                            }, doubleTapDelay)
                        } else if (numTaps == 2) {
                            // 두 번 터치 이벤트 처리
                            doubleTap = true

                            // Check the like status from SharedPreferences
                            val isLiked = sharedPreferences.getBoolean("isLiked_$imgId", false)

                            // Toggle like/unlike based on the current like status
                            if (isLiked) {
                                unlike(Constance.jwt, imgId) { isSuccess ->
                                    if (isSuccess) {
                                        // Update the like status in SharedPreferences
                                        sharedPreferences.edit().putBoolean("isLiked_$imgId", false).apply()
                                        showToast("좋아요 취소")
                                    } else {
                                        showToast("좋아요 취소 실패")
                                    }
                                }
                            } else {
                                like(Constance.jwt, imgId) { isSuccess ->
                                    if (isSuccess) {
                                        // Update the like status in SharedPreferences
                                        sharedPreferences.edit().putBoolean("isLiked_$imgId", true).apply()
                                        showToast("좋아요")
                                    } else {
                                        showToast("좋아요 실패")
                                    }
                                }
                            }

                            handler.postDelayed({
                                doubleTap = false
                            }, doubleTapDelay)
                        }
                    }
                }
                return true
            }
        })
    }

    //토스트 메시지
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    //좋아요
    private fun like(author:String ,imgId:Int, callback: (Boolean) -> Unit){
        val retrofitManager = MissionCertRetrofitManager.getInstance(context)
        retrofitManager.missionImgLike(author, imgId){response ->
            if(response){
                    Log.d("missionImgLike", "missionImgLike 성공")
                    callback(true)
            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                Log.d("missionImgLike", "missionImgLike 실패")
                callback(true)
            }


        }
    }


    //좋아요 삭제
    private fun unlike(author:String ,imgId:Int, callback: (Boolean) -> Unit){
        val retrofitManager = MissionCertRetrofitManager.getInstance(context)
        retrofitManager.missionImgUnlike(author, imgId){response ->
            if(response){
                Log.d("missionImgUnlike", "missionImgUnlike 성공")
                callback(true)
            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                Log.d("missionImgUnlike", "missionImgUnlike 실패")
                callback(true)
            }


        }
    }

    //신고하기
    private fun report(author:String ,imgId:Int){
        val retrofitManager = MissionCertRetrofitManager.getInstance(context)
        retrofitManager.report(author, imgId){response ->
            if(response){
                Log.d("mission report", "mission report 성공")
                showToast("신고 완료")
            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리

                Log.d("mission report", "mission report 실패")
                showToast("신고 실패")
            }


        }
    }


    // 뷰홀더를 생성하여 반환
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMissionCertificationImgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
