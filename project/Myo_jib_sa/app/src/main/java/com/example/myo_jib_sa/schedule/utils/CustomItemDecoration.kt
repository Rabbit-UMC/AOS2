package com.example.myo_jib_sa.schedule.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.R

//스케줄 리사이클러뷰 사이 divider 커스텀
class CustomItemDecoration(val requireContext: Context): RecyclerView.ItemDecoration() {
    @SuppressLint("ResourceAsColor")
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        //val widthMargin = 20f   // 좌우 Margin
        val height = ConvertDPtoPX(requireContext, 0.5)// 사각형의 height

        val left = parent.paddingLeft.toFloat()
        val right = parent.width - parent.paddingRight.toFloat()
        val paint = Paint().apply { color = R.color.gray4 }
        for(i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)
            val top = view.bottom.toFloat() + (view.layoutParams as RecyclerView.LayoutParams).bottomMargin
            val bottom = top + height  // 세로 길이 = 5 (bottom - top = height)

            // 좌표 (left, top) / (right, bottom) 값을 대각선으로 가지는 사각형
            //c.drawRect(left + widthMargin, top, right - widthMargin, bottom, paint)
            c.drawRect(left, top, right, bottom, paint)
        }
    }

    //dp -> px
    fun ConvertDPtoPX(context: Context, dp: Double): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }
}