package com.modulotech.utilities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.provider.CallLog
import android.telephony.SubscriptionManager
import androidx.core.app.ActivityCompat
import com.modulotech.models.CallHistory
import com.modulotech.models.CallInfo
import com.modulotech.models.RecordFile
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

const val OUT_GOING = "Outgoing"
const val IN_COMING = "Incoming"
const val MISSED = "Missed"

fun getCallInfo(context: Context, timeFrom: Long): List<CallInfo> {
    // check permissions
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 &&
        (
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_CALL_LOG
                ) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_CALL_LOG
                ) != PackageManager.PERMISSION_GRANTED
                )
    ) {
        Logger.e("getCallInfo: can not get record cause to miss permissions")
        return emptyList()
    }
    val myPhoneNumber = getMyPhoneNumber(context)

    // load data
    val callHistoryList = getCallHistory(context, timeFrom)
    val recordFileList = getRecordingCallOnXiaoMi(timeFrom)

    // result
    val callInfoList = ArrayList<CallInfo>()

    // filter in-coming and out-going call
    val connectedCallList =
        callHistoryList.filter { (it.type == OUT_GOING || it.type == IN_COMING) && it.duration != "0" }
            .toMutableList()
    if (connectedCallList.isNotEmpty()) {
        for (call in connectedCallList) {
            val timeDifferencePossible = 60 * 1000  // 60 seconds
            val record = recordFileList.firstOrNull { record ->
                val difference = record.time.time - call.time.time
                record.phoneFrom == call.phoneFrom && difference >= 0 && difference < timeDifferencePossible
            }
            callInfoList.add(
                CallInfo(
                    name = call.name,
                    phone = call.phoneFrom,
                    type = call.type,
                    time = call.time,
                    duration = call.duration,
                    fileName = record?.fileName ?: "",
                    absolutePath = record?.absolutePath ?: "",
                    myPhoneNumber = myPhoneNumber
                )
            )
        }
    }

    Logger.i("getCallInfo: $callInfoList")
    return callInfoList
}

fun getCallHistory(context: Context, timeFrom: Long): List<CallHistory> {
    val callHistoryList = ArrayList<CallHistory>()
    val projection = arrayOf(
        CallLog.Calls.CACHED_NAME,
        CallLog.Calls.NUMBER,
        CallLog.Calls.TYPE,
        CallLog.Calls.DATE,
        CallLog.Calls.DURATION,
    )
    val order = CallLog.Calls.DATE + " DESC"
    val cursor: Cursor? =
        context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            CallLog.Calls.DATE + ">= ?",
            arrayOf(timeFrom.toString()),
            order
        )
    if (cursor != null) {
        while (cursor.moveToNext()) {
            val name: String? = cursor.getString(0)
            val phone: String = cursor.getString(1)
            val type: String = convertCallTypeToString(cursor.getString(2))
            val time: Date = convertDateStringToDate(cursor.getString(3))
            val duration: String? = cursor.getString(4)
            callHistoryList.add(
                CallHistory(
                    name = name ?: "",
                    phoneFrom = phone ?: "",
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

private fun convertDateStringToDate(date: String): Date {
    return if (date.isNullOrBlank()) return Date(0) else Date(date.toLong())
}

fun convertCallTypeToString(callType: String?): String {
    if (callType.isNullOrBlank()) {
        return ""
    }
    return when (callType.toInt()) {
        CallLog.Calls.OUTGOING_TYPE -> OUT_GOING
        CallLog.Calls.INCOMING_TYPE -> IN_COMING
        CallLog.Calls.MISSED_TYPE -> MISSED
        else -> ""
    }
}

fun getRecordingCallOnXiaoMi(timeFrom: Long): List<RecordFile> {
    return getPlayList("/storage/emulated/0/MIUI/sound_recorder/call_rec/", timeFrom)
}

fun getPlayList(rootPath: String, timeFrom: Long): List<RecordFile> {
    val fileList = ArrayList<RecordFile>()
    val rootFolder = File(rootPath)
    val files: Array<File>? = rootFolder.listFiles()
    if (files != null) {
        for (file in files) {
            if (file.isFile && file.name.endsWith(".mp3") && file.lastModified() >= timeFrom) {
                // name: Name(0982268745)_20210725230215.mp3
                val regex = Regex("(.*)\\((\\d{9,12})\\)_(.*)\\.mp3")
                val result = regex.find(file.name)
                val nameIndex = 1
                val phoneIndex = 2
                val timeIndex = 3
                if (result != null) {
                    val name = result.groupValues[nameIndex]
                    val phone = result.groupValues[phoneIndex]
                    val time = result.groupValues[timeIndex]
                    fileList.add(
                        RecordFile(
                            fileName = file.name,
                            absolutePath = file.absolutePath,
                            lastModified = file.lastModified(),
                            phoneFrom = phone,
                            time = convertStringToDate(time)
                        )
                    )
                } else {
                    Logger.i("getPlayList: can not parse with name -> ${file.name}")
                }
            }
        }
    }
    fileList.sortByDescending { it.time }
    Logger.i("getPlayList: $fileList")
    return fileList
}

private fun getMyPhoneNumber(context: Context): String {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return ""
        }
        val subscriptionManager = SubscriptionManager.from(context)
        val subsInfoList = subscriptionManager.activeSubscriptionInfoList
        return if (subsInfoList.isNotEmpty()) {
            subsInfoList[0].number
        } else {
            ""
        }
    }
    return ""
}