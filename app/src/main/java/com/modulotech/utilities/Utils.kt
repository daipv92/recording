package com.modulotech.utilities

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun nowByMiniSecond() : Long {
    return Calendar.getInstance().time.time
}

fun convertDateToString(date: Date): String {
    val pattern = "yyyyMMdd HH:mm:ss"
    val df: DateFormat = SimpleDateFormat(pattern)
    return df.format(date)
}

fun convertStringToDate(str: String) : Date {
    val pattern = "yyyyMMddHHmmss"
    val df: DateFormat = SimpleDateFormat(pattern)
    return df.parse(str)
}