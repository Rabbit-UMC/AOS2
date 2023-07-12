package com.example.myo_jib_sa.schedule

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.schedule.adapter.ScheduleAdaptar

class RVSwipeCallback (
    val context: Context,
    val recyclerView: RecyclerView
) : ItemTouchHelper.Callback() {

    var adapter: ScheduleAdaptar

    init {
        adapter = (recyclerView.adapter as ScheduleAdaptar)
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

//        val holder = recyclerView.findViewHolderForAdapterPosition(position) as? MyAdapter.ViewHolder
//        if (holder != null) {
//            adapter.performAction(position, holder)
//        }

        //adapter.performAction(viewHolder.adapterPosition, viewHolder)
        adapter.removeTask(viewHolder.adapterPosition)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }
}