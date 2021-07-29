package com.modulotech.models

import com.modulotech.utilities.convertDateToString
import java.util.*

data class CallHistory(
    val name: String,
    val phoneFrom: String,
    val type: String,
    val time: Date,
    val duration: String
) {
    override fun toString(): String {
        return "(name = $name, phone = $phoneFrom, type = $type, time = ${convertDateToString(time)}, $duration)"
    }
}