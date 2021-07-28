package com.modulotech.utilities

import android.content.Context
import android.database.Cursor
import android.provider.CallLog
import com.modulotech.models.CallHistory
import com.modulotech.models.RecordFile
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


fun getCallHistory(context: Context) : List<CallHistory> {
    val callHistoryList = ArrayList<CallHistory>()
    val projection = arrayOf(
        CallLog.Calls.CACHED_NAME,
        CallLog.Calls.NUMBER,
        CallLog.Calls.TYPE,
        CallLog.Calls.DATE,
        CallLog.Calls.DURATION
    )
    val order = CallLog.Calls.DATE + " DESC"
    val cursor: Cursor? =
        context.contentResolver.query(CallLog.Calls.CONTENT_URI, projection, null, null, order)
    if (cursor != null) {
        while (cursor.moveToNext()) {
            val name: String? = cursor.getString(0)
            val phone: String? = cursor.getString(1)
            val type: String = convertCallTypeToString(cursor.getString(2))
            val time: Date? = convertDateStringToDate(cursor.getString(3))
            val duration: String? = cursor.getString(4)
            callHistoryList.add(
                CallHistory(
                    name = name ?: "",
                    phone = phone ?: "",
                    type = type,
                    time = time,
                    duration = duration ?: ""
                )
            )
        }
        cursor.close()
    }
    Logger.i("getCallHistory: $callHistoryList")
    return callHistoryList
}

fun convertDateStringToDate(date: String) : Date? {
    return if (date.isNullOrBlank()) return null else Date(date.toLong())
}

fun convertCallTypeToString(callType: String?): String {
    if (callType.isNullOrBlank()) {
        return ""
    }
    return when (callType.toInt()) {
        CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
        CallLog.Calls.INCOMING_TYPE -> "Incoming"
        CallLog.Calls.MISSED_TYPE -> "Missed"
        else -> ""
    }
}

fun getRecordingCallOnXiaoMi() : List<RecordFile> {
    return getPlayList("/storage/emulated/0/MIUI/sound_recorder/call_rec/")
}
fun getPlayList(rootPath: String): List<RecordFile> {
    val fileList = ArrayList<RecordFile>()
    val rootFolder = File(rootPath)
    val files: Array<File>? = rootFolder.listFiles()
    if (files != null) {
        for (file in files) {
            if (file.isFile && file.name.endsWith(".mp3")) {
                file.lastModified()
                fileList.add(
                    RecordFile(
                        fileName = file.name,
                        lastModified = file.lastModified()
                    )
                )
            }
        }
    }
    Logger.i(fileList.toString())
    return fileList
}