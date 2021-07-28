package com.modulotech.models

import java.util.*

data class CallHistory(
    val name: String,
    val phone: String,
    val type: String,
    val time: Date?,
    val duration: String
) {
    override fun toString(): String {
        return "$name, $phone, $type, $duration"
    }
}