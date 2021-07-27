package com.modulotech.models

data class RecordFile(
    val fileName: String,
    val lastModified: Long
) {
    override fun toString(): String {
        return "$fileName"
    }
}