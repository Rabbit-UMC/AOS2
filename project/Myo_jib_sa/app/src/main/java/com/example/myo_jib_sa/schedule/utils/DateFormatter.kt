package com.example.myo_jib_sa.schedule.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateFormatter {
    //YYYY-MM-DD 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    public fun YYYYMMDDFromDate(date: LocalDate?): String? {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date?.format(formatter)
    }



    //YYYY년 MM월 D일
    @RequiresApi(Build.VERSION_CODES.O)
    public fun yearMonthDate(date : LocalDate):String{
        var formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
        return date.format(formatter)
    }

    //YYYY 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    public fun yyyyFromDate(date: LocalDate?): String? {
        var formatter = DateTimeFormatter.ofPattern("yyyy")
        return date?.format(formatter)
    }

    //YYYY년 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    public fun yearFromDate(date : LocalDate):String{
        var formatter = DateTimeFormatter.ofPattern("yyyy년")
        return date.format(formatter)
    }

    //M월 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    public fun monthFromDate(date: LocalDate?): String? {
        var formatter = DateTimeFormatter.ofPattern("M월")
        return date?.format(formatter)
    }

    //YYYY-MM 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    public fun YYYY_MMFromDate(date: LocalDate?): String? {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM")
        return date?.format(formatter)
    }

    //M월 D일 형식으로 포맷
    @RequiresApi(Build.VERSION_CODES.O)
    public fun MMDDFromDate(date: LocalDate?): String? {
        var formatter = DateTimeFormatter.ofPattern("M월 d일")
        return date?.format(formatter)
    }


}