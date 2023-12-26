package com.example.myo_jib_sa.community.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.databinding.ItemCreateScheduleCalendarDayBinding
import com.example.myo_jib_sa.databinding.ItemScheduleCalendarDayBinding
import java.time.LocalDate

var prePosition : Int = -1

data class SelectDateData(
    val date: LocalDate?,
    var isSelected :Boolean = false//ture이면 일정 있음, false이면 일정 없음
)

class CreateScheduleCalendarAdapter(private val dayList:ArrayList<SelectDateData>):
    RecyclerView.Adapter<CreateScheduleCalendarAdapter.ViewHolder>() {


    //화면 설정
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemCreateScheduleCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    //데이터 설정
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dayList[position])



        if(!dayList[position].isSelected) {
            //날짜 클릭 이벤트
            holder.itemView.setOnClickListener {
                //ragment나 activity에서 클릭이벤트 정의하고 싶을때 사용
                itemClickListener.onClick(dayList[position], position)
                //===============================================

                dayList[position].isSelected = true
                notifyItemChanged(position)

                if (prePosition != -1) {
                    dayList[prePosition].isSelected = false
                }
                notifyItemChanged(prePosition)
                prePosition = position

            }
        }
    }

    class ViewHolder(private val binding: ItemCreateScheduleCalendarDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(data: SelectDateData) {

            //날짜 변수에 담기
            if(data.date==null){
                binding.dayTv.text = ""
            }
            else{
                binding.dayTv.text = data.date.dayOfMonth.toString()
            }
            if(data.isSelected){
                binding.selectedDateIv.visibility = View.VISIBLE
                binding.dayTv.setTextColor(Color.parseColor("#0F1C4C"));
            }
            else{
                binding.selectedDateIv.visibility = View.INVISIBLE
                binding.dayTv.setTextColor(Color.parseColor("#FFFFFF"));
            }

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