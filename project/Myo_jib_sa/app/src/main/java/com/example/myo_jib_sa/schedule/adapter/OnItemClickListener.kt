package com.example.myo_jib_sa.schedule.adapter


interface OnItemClickListener {
    fun onItemClick(calendarData: CalendarData)

    companion object {
        fun onItemClick(calendarData: CalendarData) {

        }
    }
}