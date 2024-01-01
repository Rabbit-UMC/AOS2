package com.example.myo_jib_sa.schedule.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.databinding.ItemScheduleCalendarDayBinding
import java.time.LocalDate


data class CalendarData(
    val date: LocalDate?,
    var scheduleCnt :Int? = 0,//일정 개수
    var isSelected : Boolean = false//ture이면 선택, false이면 미션택
)


class CalendarAdapter(private val dayList:ArrayList<CalendarData>):
    RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
    var prePosition : Int = -1


    override fun onCreateViewHolder( //화면 설정
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemScheduleCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {//데이터 설정
        holder.bind(dayList[position])

        //날짜 클릭 이벤트
        holder.itemView.setOnClickListener{
            itemClickListener.onClick(dayList[position])
            Log.d("position", "position ${position}")

            dayList[position].isSelected = true
            notifyItemChanged(position)

            if (prePosition != -1) {
                dayList[prePosition].isSelected = false
            }
            notifyItemChanged(prePosition)
            prePosition = position

        }
    }

    class ViewHolder(private val binding: ItemScheduleCalendarDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(data: CalendarData) {

            //날짜 변수에 담기
            if(data.date==null){
                binding.dayTv.text = ""
            }
            else{
                binding.dayTv.text = data.date.dayOfMonth.toString()
            }
            //스케줄 가지고 있으면 표시
            Log.d("debug", ""+data.scheduleCnt)

            binding.hasScheduleIv.visibility = View.GONE
            binding.twoScheduleLayout.visibility = View.GONE
            binding.threeScheduleLayout.visibility = View.GONE
            when(data.scheduleCnt){
                null, 0 -> binding.hasScheduleIv.visibility = View.GONE
                1-> binding.hasScheduleIv.visibility = View.VISIBLE
                2->binding.twoScheduleLayout.visibility = View.VISIBLE
                else ->binding.threeScheduleLayout.visibility = View.VISIBLE
            }

            //선택하면 파란 동그라미 & 글자 색 변경
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

    //클릭 이벤트 처리 ==============================================
    //리스너 인터페이스
    interface  OnItemClickListener{
        fun onClick(calendarData: CalendarData)

    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener
    //==============================================================

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
            val topSpace = (deviceWidth * 0.03).toInt()

            outRect.top = topSpace


            outRect.left = (deviceWidth * 0.03).toInt()
            outRect.right = (deviceWidth * 0.03).toInt()

        }
    }
}





