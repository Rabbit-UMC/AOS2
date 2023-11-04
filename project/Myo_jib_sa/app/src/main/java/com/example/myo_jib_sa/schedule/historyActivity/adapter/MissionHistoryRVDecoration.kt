package com.example.myo_jib_sa.schedule.historyActivity.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MissionHistoryRVDecoration(
    private val divWidth:Int,
    private val divHeight:Int
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
//        val count = state.itemCount
//
//
//        if(position == 0){
//            outRect.right=20
//            outRect.bottom = divHeight
//        } else{
//            outRect.right = 0
//            outRect.left=0
//            outRect.bottom = divHeight
//        }

        val spanCount =2
        val column = position % spanCount + 1      // 1부터 시작

        /** 첫번째 행(row-1) 이후부터 있는 아이템에만 상단에 [space] 만큼의 여백을 추가한다. 즉, 첫번째 행에 있는 아이템에는 상단에 여백을 주지 않는다.*/
        if (position >= spanCount){
            outRect.top = divHeight
        }
        /** 첫번째 열이 아닌(None Column-1) 아이템들만 좌측에 [space] 만큼의 여백을 추가한다. 즉, 첫번째 열에 있는 아이템에는 좌측에 여백을 주지 않는다. */
        if (column != 1){
            outRect.left = divWidth/2
        }

    }
}