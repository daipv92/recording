package com.modulotech.workers

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.modulotech.api.NetworkAPI
import com.modulotech.models.CallInfo
import com.modulotech.utilities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UploadWorker constructor(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        Logger.i("UploadWorker#doWork")
        return withContext(Dispatchers.IO) {
            uploadRecordingCall()
            return@withContext Result.success()
        }
    }

    private suspend fun uploadRecordingCall() {
        val lastSynchronizedTime = SharedPreferencesManager.getLastSynchronizedTime(applicationContext)
        Logger.i("UploadWorker#uploadRecordingCall: from = ${convertTimeStampToString(lastSynchronizedTime)}")
        val list = getCallInfo(applicationContext, lastSynchronizedTime)
        val timeStampNow = nowByMiniSecond()
        if (list.isNotEmpty()) {
            val synchronizedCallList = mutableListOf<CallInfo>()
            for (call in list) {
                // check this call is recorded or NOT
                if (!call.absolutePath.isNullOrBlank()) {
                    val result = NetworkAPI().uploadRecordFile(
                        customerPhone = call.phone,
                        type = call.type,
                        salePhone = call.myPhoneNumber,
                        createAt = convertDateToString(call.time),
                        fileUri = call.absolutePath
                    )
                    if (result) {
                        synchronizedCallList.add(call)
                    }
                }
            }

            // update storage
            if (synchronizedCallList.isNotEmpty()) {
                val oldList = SharedPreferencesManager.getSynchronizedCallList(applicationContext)
                synchronizedCallList.addAll(oldList)
                val finalList = if (synchronizedCallList.size > 10) synchronizedCallList.subList(0, 9) else synchronizedCallList
               SharedPreferencesManager.setSynchronizedCallList(applicationContext, finalList)
            }
        }
        SharedPreferencesManager.setLastSynchronizedTime(applicationContext, timeStampNow)
        sendBroadcastUpdated(applicationContext)
    }

    private fun sendBroadcastUpdated(applicationContext: Context) {
        Intent().also { intent ->
            intent.action = ACTION_UPDATE_SYNCHRONIZED_LIST
            applicationContext.sendBroadcast(intent)
        }
    }
}