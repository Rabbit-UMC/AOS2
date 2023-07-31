package com.example.myo_jib_sa.schedule.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.schedule.api.scheduleOfDay.ScheduleOfDayResult


class ScheduleAdaptar (private val scheduleList:ArrayList<ScheduleOfDayResult>):
    RecyclerView.Adapter<ScheduleAdaptar.ItemViewHolder>() {

    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val sTitle_tv: TextView = itemView.findViewById(R.id.schedule_title_tv)
        val sStartTime_tv: TextView = itemView.findViewById(R.id.schedule_start_time_tv)
        val sFinishTime_tv: TextView = itemView.findViewById(R.id.schedule_end_time_tv)
        val sItemRectangle_img: ImageView = itemView.findViewById(R.id.sechedule_rectangle_img)
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
        holder.sTitle_tv.text = schedule.scheduleTitle
        holder.sStartTime_tv.text = schedule.scheduleStart
        holder.sFinishTime_tv.text = schedule.scheduleEnd

        //일정 클릭 이벤트
        holder.itemView.setOnClickListener{

            itemClickListener.onClick(schedule)

//            var iYear = day?.year
//            var iMonth = day?.monthValue
//            var iDay = day?.dayOfMonth
//
//            Toast.makeText(holder.itemView.context, "${iYear}년 ${iMonth}월 ${iDay}일", Toast.LENGTH_SHORT)
//                .show()
        }



    }


    //클릭 이벤트 처리 ==============================================
    //리스너 인터페이스
    interface  OnItemClickListener{
        fun onClick(scheduleData: ScheduleOfDayResult)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener
    //==============================================================


    //swipe시 내부 데이터 값 제거
    fun removeTask(position: Int) {
        scheduleList.removeAt(position)

        notifyDataSetChanged()
    }



    fun getItem():ArrayList<ScheduleOfDayResult> {
        return scheduleList
    }

    override fun getItemCount(): Int {
        return scheduleList.size
    }
}