package com.example.myo_jib_sa.schedule.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.R

data class scheduleData(
    val sTitle:String,
    val sStartTime:String,
    val sFinishTime:String
)
class ScheduleAdaptar (private val scheduleList:ArrayList<scheduleData>):
    RecyclerView.Adapter<ScheduleAdaptar.ItemViewHolder>() {

    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val sTitle_tv: TextView = itemView.findViewById(R.id.schedule_title_tv)
        val sStartTime_tv: TextView = itemView.findViewById(R.id.schedule_start_time_tv)
        val sFinishTime_tv: TextView = itemView.findViewById(R.id.schedule_end_time_tv)
    }

    //화면 설정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule_schedule, parent, false)

        return ItemViewHolder(view)
    }

    //데이터 설정
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        var schedule = scheduleList[position]
        holder.sTitle_tv.text = schedule.sTitle
        holder.sStartTime_tv.text = schedule.sStartTime
        holder.sFinishTime_tv.text = schedule.sFinishTime

        //일정 클릭 이벤트
        holder.itemView.setOnClickListener{
//            var iYear = day?.year
//            var iMonth = day?.monthValue
//            var iDay = day?.dayOfMonth
//
//            Toast.makeText(holder.itemView.context, "${iYear}년 ${iMonth}월 ${iDay}일", Toast.LENGTH_SHORT)
//                .show()
        }



    }

    override fun getItemCount(): Int {
        return scheduleList.size
    }
}