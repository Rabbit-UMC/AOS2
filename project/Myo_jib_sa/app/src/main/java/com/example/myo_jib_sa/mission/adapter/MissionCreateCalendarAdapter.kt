package com.example.myo_jib_sa.mission.adapter

import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.databinding.ItemScheduleCalendarDayBinding
import com.example.myo_jib_sa.mission.api.MissionCreateDate
import java.time.LocalDate



class MissionCreateCalendarAdapter(private val dayList:ArrayList<MissionCreateDate>):
    RecyclerView.Adapter<MissionCreateCalendarAdapter.MissionCreateCalendarViewHolder>() {
    private var prePosition : Int = -1

    inner class MissionCreateCalendarViewHolder(private val binding: ItemScheduleCalendarDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(data: MissionCreateDate) {
            //날짜 변수에 담기
            if(data.date==null){
                binding.dayTv.text = ""
            }
            else{
                binding.dayTv.text = data.date.dayOfMonth.toString()
            }
            if(data.isSelected){
                binding.selectedDateCircle.visibility = View.VISIBLE
                binding.dayTv.setTextColor(Color.parseColor("#FFFFFF"));
            }
            else{
                binding.selectedDateCircle.visibility = View.INVISIBLE
                binding.dayTv.setTextColor(Color.parseColor("#000000"));
            }

        }
    }

    //화면 설정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissionCreateCalendarViewHolder {
        val binding =
            ItemScheduleCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MissionCreateCalendarViewHolder(binding)
    }


    //데이터 설정
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MissionCreateCalendarViewHolder, position: Int) {
        if(!dayList[position].isSelected) {
            //날짜 클릭 이벤트
            holder.itemView.setOnClickListener {
                itemClickListener.onClick(dayList[position], position)
            }
        }
        holder.bind(dayList[position])
    }


    //리스너 인터페이스
    interface  OnItemClickListener{
        fun onClick(selectDateData: MissionCreateDate, position: Int)

    }
    // 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener

    override fun getItemCount(): Int {
        return dayList.size
    }

    class GridSpaceDecoration(private val deviceWidth: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

            val position = parent.getChildAdapterPosition(view) //각 아이템뷰의 순서 (index)
            //val totalItemCount = state.itemCount                //총 아이템 수
            //val scrollPosition = state.targetScrollPosition     //스크롤 됬을때 아이템 position

            val column = position % 7      // 0~6

            val leftSpace = (deviceWidth * 0.03).toInt()
            val topSpace = (deviceWidth * 0.04).toInt()

            outRect.top = topSpace


            outRect.left = (deviceWidth * 0.03).toInt()
            outRect.right = (deviceWidth * 0.03).toInt()

        }
    }
}