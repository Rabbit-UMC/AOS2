package com.example.customcalendar_ver2

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

var prePosition : Int = -1

data class SelectDateData(
    val date: LocalDate?,
    var isSelected :Boolean = false//ture이면 일정 있음, false이면 일정 없음
)

class CalendarAdapter(private val dayList:ArrayList<SelectDateData>):
    RecyclerView.Adapter<CalendarAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val dayText: TextView = itemView.findViewById(R.id.day_tv)
        val selectedDateImg: ImageView = itemView.findViewById(R.id.selectedDate_iv)
    }

    //화면 설정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_day, parent, false)

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
        if(dayList[position].isSelected == true){
            holder.selectedDateImg.visibility = View.VISIBLE
        }
        else{
            holder.selectedDateImg.visibility = View.INVISIBLE
        }



            //날짜 클릭 이벤트
            holder.itemView.setOnClickListener{
                //ragment나 activity에서 클릭이벤트 정의하고 싶을때 사용
                itemClickListener.onClick(dayList[position], position)
                //===============================================

                dayList[position].isSelected = true
                notifyItemChanged(position)

                if(prePosition != -1){
                    dayList[prePosition].isSelected = false
                }
                notifyItemChanged(prePosition)
                prePosition = position

            }
    }

    //클릭 이벤트 처리 ==============================================
    //리스너 인터페이스
    interface  OnItemClickListener{
        fun onClick(selectDateData: SelectDateData, position: Int)

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