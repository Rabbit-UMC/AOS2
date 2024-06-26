package com.example.myo_jib_sa.mission

import android.content.res.Resources
import android.graphics.Canvas
import android.util.Log
import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.mission.adapter.MissionRVAdapter

class SwipeHelper: ItemTouchHelper.Callback() {  // ItemTouchHelper.Callback 을 구현해야 한다
    private var currentPosition: Int? = null
    private var previousPosition: Int? = null
    private var currentDx = 0f
    private var clamp = 0f

    private var currentSwipeView: View? = null

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        currentDx = 0f
        previousPosition = viewHolder.adapterPosition
        val reportTextView = (viewHolder as? MissionRVAdapter.MissionViewHolder)?.getReportTextView()
        reportTextView?.elevation = 0f
        getDefaultUIUtil().clearView(getView(viewHolder))
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        //super.onSelectedChanged(viewHolder, actionState)
        viewHolder?.let {
            getDefaultUIUtil().onSelected(getView(it))
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val view = getView(viewHolder)
            val isClamped = getTag(viewHolder)
            val x =  clampViewPositionHorizontal(view, dX, isClamped, isCurrentlyActive)
            val reportTextView = (viewHolder as? MissionRVAdapter.MissionViewHolder)?.getReportTextView()

            currentDx = x
            getDefaultUIUtil().onDraw(
                c,
                recyclerView,
                view,
                x,
                dY,
                actionState,
                isCurrentlyActive
            )
            ViewCompat.setElevation(view, 6.dpToPx())

            if (isCurrentlyActive) {
                // 스와이프 중인 상태에서의 뷰 업데이트
                view.setBackgroundResource(R.drawable.background_item_clamped_mission_layout)
                reportTextView?.elevation = 6.dpToPx()
                Log.d("isNotClamped", "reportTextView?.isClickable : ${reportTextView?.isClickable}")
            }
            else {
                view.setBackgroundResource(R.drawable.background_item_clamped_mission_layout)
                reportTextView?.elevation = 6.dpToPx()
                reportTextView?.isClickable = true
                Log.d("isNotClamped", "reportTextView?.isClickable : ${reportTextView?.isClickable}")
                if(!isClamped) {
                    view.setBackgroundResource(R.drawable.background_item_mission_layout)
                    reportTextView?.elevation = 0f
                    reportTextView?.isClickable = false
                    Log.d("isClamped", "reportTextView?.isClickable : ${reportTextView?.isClickable}")
                }
            }
        }
    }
    private fun getView(viewHolder: RecyclerView.ViewHolder): View {
        // 아이템뷰에서 스와이프 영역에 해당하는 뷰 가져오기
        return (viewHolder as MissionRVAdapter.MissionViewHolder).itemView
            .findViewById(R.id.mission_item_cl)
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return defaultValue * 20
    }
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        val isClamped = getTag(viewHolder)
        // 현재 View가 고정되어있지 않고 사용자가 -clamp 이상 swipe시 isClamped true로 변경 아닐시 false로 변경
        setTag(viewHolder, !isClamped && currentDx <= -clamp)
        return 2f
    }

    // dp를 픽셀로 변환하는 확장 함수
    private fun Int.dpToPx(): Float {
        val scale = Resources.getSystem().displayMetrics.density
        return this * scale
    }
    private fun clampViewPositionHorizontal(
        view: View,
        dX: Float,
        isClamped: Boolean,
        isCurrentlyActive: Boolean
    ) : Float {
        // View의 가로 길이의 절반까지만 swipe 되도록
        val min: Float = -clamp
        // RIGHT 방향으로 swipe 막기
        val max: Float = 0f

        val x = if (isClamped) {
            // View가 고정되었을 때 swipe되는 영역 제한
            if (isCurrentlyActive) dX - clamp else -clamp
        } else {
            dX
        }

        return min.coerceAtLeast(x).coerceAtMost(max)
    }

    private fun setTag(viewHolder: RecyclerView.ViewHolder, isClamped: Boolean) {
        // isClamped를 view의 tag로 관리
        viewHolder.itemView.tag = isClamped
    }

    private fun getTag(viewHolder: RecyclerView.ViewHolder) : Boolean {
        // isClamped를 view의 tag로 관리
        return viewHolder.itemView.tag as? Boolean ?: false
    }

    fun setClamp(clamp: Float) {
        this.clamp = clamp.toInt().dpToPx()
    }

    // 다른 View가 swipe 되거나 터치되면 고정 해제
    fun removePreviousClamp(recyclerView: RecyclerView) {
        if (currentPosition == previousPosition)
            return
        previousPosition?.let {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(it) ?: return
            getView(viewHolder).translationX = 0f
            setTag(viewHolder, false)
            previousPosition = null
        }
    }
}