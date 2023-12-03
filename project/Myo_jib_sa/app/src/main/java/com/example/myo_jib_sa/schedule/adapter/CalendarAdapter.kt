package com.example.myo_jib_sa.schedule.adapter

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


var prePosition : Int = -1
data class CalendarData(
    val date: LocalDate?,
    var hasSchedule :Boolean? = false,//ture이면 일정 있음, false이면 일정 없음
    var isSelected : Boolean = false//ture이면 선택, false이면 미션택
)


class CalendarAdapter(private val dayList:ArrayList<CalendarData>):
    RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

//    class ItemViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
//        val dayText: TextView = itemView.findViewById(R.id.day_tv)
//        val hasScheduleImg: ImageView = itemView.findViewById(R.id.hasSchedule_iv)
//        val isSelectedImg : ImageView = itemView.findViewById(R.id.selected_date_circle)
//    }

    //화면 설정
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CalendarAdapter.ViewHolder {
        val binding =
            ItemScheduleCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    //데이터 설정
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dayList[position])


        //날짜 클릭 이벤트
        holder.itemView.setOnClickListener{
            itemClickListener.onClick(dayList[position])
            Log.d("position", "position ${position}")

            if(dayList[position].hasSchedule == true) {

//                var iYear = day?.year
//                var iMonth = day?.monthValue
//                var iDay = day?.dayOfMonth
//
////                Toast.makeText(holder.itemView.context, "${iYear}년 ${iMonth}월 ${iDay}일", Toast.LENGTH_SHORT)
////                    .show()
            }


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
            if(data.hasSchedule == true)
                binding.hasScheduleIv.visibility = View.VISIBLE
            else
                binding.hasScheduleIv.visibility = View.INVISIBLE


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

    class HorizontalSpaceDecoration(private val deviceWidth: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

            val position = parent.getChildAdapterPosition(view) //각 아이템뷰의 순서 (index)
            //val totalItemCount = state.itemCount                //총 아이템 수
            //val scrollPosition = state.targetScrollPosition     //스크롤 됬을때 아이템 position

            Log.d("position", "position ${position}")
            val spanCount =7
            val column = position % spanCount      // 1부터 시작

            val leftSpace = (deviceWidth * 0.03).toInt()
            val topSpace = (deviceWidth * 0.04).toInt()

            /** 첫번째 행(row-1) 이후부터 있는 아이템에만 상단에 [space] 만큼의 여백을 추가한다. 즉, 첫번째 행에 있는 아이템에는 상단에 여백을 주지 않는다.*/

                outRect.top = topSpace
            /** 첫번째 열이 아닌(None Column-1) 아이템들만 좌측에 [space] 만큼의 여백을 추가한다. 즉, 첫번째 열에 있는 아이템에는 좌측에 여백을 주지 않는다. */
            //outRect.left = leftSpace
            if(column != 0)
                outRect.left = leftSpace
            //outRect.right = leftSpace








            //첫번째 아이템이 아닐때 left -margin
            //if (position % 7 != 0){
                //outRect.left = leftSpace
                //outRect.top =topSpace
            //}

            //outRect.set(0,0,0,0)   //left, top, bottom, right 한번에 주는 속성



        }
    }
}





