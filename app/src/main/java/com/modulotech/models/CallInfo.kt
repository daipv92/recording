package com.modulotech.models

import com.modulotech.utilities.convertDateToString
import java.util.*

data class CallInfo(
    val name: String,
    val phone: String,
    val type: String,
    val time: Date,
    val duration: String,
    val fileName: String,
    val absolutePath: String,
    val myPhoneNumber: String
) {
    override fun toString(): String {
        return "(name = $name, phone = $phone, myPhone = $myPhoneNumber, type = $type, time = ${convertDateToString(time)}" +
                ", duration = $duration, fileName = $fileName, path = $absolutePath)"
    }
}