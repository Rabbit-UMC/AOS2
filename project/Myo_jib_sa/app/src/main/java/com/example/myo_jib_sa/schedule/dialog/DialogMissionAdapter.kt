package com.example.myo_jib_sa.schedule.dialog

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.ItemScheduleMissionBinding
import com.example.myo_jib_sa.schedule.api.MyMissionResult

class DialogMissionAdapter(private val missionList: List<MyMissionResult>):
    RecyclerView.Adapter<DialogMissionAdapter.ViewHolder>() {
    override fun onCreateViewHolder( //화면 설정
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemScheduleMissionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {//데이터 설정
        holder.bind(missionList[position])
    }

    inner class ViewHolder(private val binding: ItemScheduleMissionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MyMissionResult) {
            when(data.categoryId){
                1L -> binding.root.setBackgroundResource(R.drawable.view_round_r16_gray4) //자유
                2L -> binding.root.setBackgroundResource(R.drawable.view_round_r16_main2)//운동
                3L -> binding.root.setBackgroundResource(R.drawable.view_round_r16_main4)//예술
                else ->  binding.root.setBackgroundResource(R.drawable.view_round_r16_gray4) //자유
            }

            binding.missionTitleTv.text = data.missionTitle
            binding.missionDdayTv.text =data.dday

            binding.root.setOnClickListener {
                itemClickListener.onClick(data)
            }
        }
    }

    //클릭 이벤트 처리 ==============================================
    //리스너 인터페이스
    interface  OnItemClickListener{
        fun onClick(data: MyMissionResult)

    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener
    //==============================================================

    override fun getItemCount(): Int {
        return missionList.size
    }

    class GridSpaceDecoration(private val deviceWidth: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

            val position = parent.getChildAdapterPosition(view) //각 아이템뷰의 순서 (index)
            //val totalItemCount = state.itemCount                //총 아이템 수
            //val scrollPosition = state.targetScrollPosition     //스크롤 됬을때 아이템 position

            val column = position % 7      // 0~6

            val leftSpace = (deviceWidth * 0.03).toInt()
            val topSpace = (deviceWidth * 0.03).toInt()

            outRect.top = topSpace


            outRect.left = (deviceWidth * 0.03).toInt()
            outRect.right = (deviceWidth * 0.03).toInt()

        }
    }
}