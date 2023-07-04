package com.example.myo_jib_sa.schedule.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class CalendarData(
    val date: LocalDate?,
    var hasSchedule :Boolean = false//ture이면 일정 있음, false이면 일정 없음
)


class CalendarAdapter(private val dayList:ArrayList<CalendarData>):
    RecyclerView.Adapter<CalendarAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val dayText: TextView = itemView.findViewById(R.id.day_tv)
        val hasScheduleImg: ImageView = itemView.findViewById(R.id.hasSchedule_iv)
    }

    //화면 설정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule_calendar_day, parent, false)

        return ItemViewHolder(view)
    }

    //데이터 설정
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {


        //날짜 변수에 담기
        var day = dayList[position].date
        if(day==null){
            holder.dayText.text = ""
        }
        else{
            holder.dayText.text = day.dayOfMonth.toString()
        }

        if(dayList[position].hasSchedule == true)
            holder.hasScheduleImg.visibility = View.VISIBLE

        //날짜 클릭 이벤트
        holder.itemView.setOnClickListener{
            itemClickListener.onClick(dayList[position])

            if(dayList[position].hasSchedule == true) {

                var iYear = day?.year
                var iMonth = day?.monthValue
                var iDay = day?.dayOfMonth

                Toast.makeText(holder.itemView.context, "${iYear}년 ${iMonth}월 ${iDay}일", Toast.LENGTH_SHORT)
                    .show()
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
}





