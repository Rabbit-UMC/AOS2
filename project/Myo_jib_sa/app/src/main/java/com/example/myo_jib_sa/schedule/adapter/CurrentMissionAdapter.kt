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

data class currentMissionData(
    val mTitle:String,
    val mNum:String,
    val mDDay:String
)
class CurrentMissionAdapter(private val missionList:ArrayList<currentMissionData>):
    RecyclerView.Adapter<CurrentMissionAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val mTitle_tv: TextView = itemView.findViewById(R.id.mission_title_tv)
        val mNum_tv: TextView = itemView.findViewById(R.id.mission_numOfchallenger_tv)
        val mDDay_tv: TextView = itemView.findViewById(R.id.mission_dDay_tv)
    }

    //화면 설정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule_current_mission, parent, false)

        return ItemViewHolder(view)
    }

    //데이터 설정
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        var mission = missionList[position]
        holder.mTitle_tv.text = mission.mTitle
        holder.mNum_tv.text = "${mission.mNum}명"
        holder.mDDay_tv.text = "D ${mission.mDDay}"

        //미션 클릭 이벤트
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
        return missionList.size
    }
}