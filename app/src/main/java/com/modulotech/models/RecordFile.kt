package com.modulotech.models

import com.modulotech.utilities.convertDateToString
import java.util.*

data class RecordFile(
    val fileName: String,
    val absolutePath: String,
    val phoneFrom: String,
    val time: Date,
    val lastModified: Long
) {
    override fun toString(): String {
        return "($phoneFrom, time = ${convertDateToString(time)}, last = ${Date(lastModified).toString()})"
    }
}