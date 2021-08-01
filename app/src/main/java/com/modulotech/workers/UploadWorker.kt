package com.modulotech.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.modulotech.api.NetworkAPI
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
            for (call in list) {
                // check this call is recorded or NOT
                if (!call.absolutePath.isNullOrBlank()) {
                    NetworkAPI().uploadRecordFile(
                        customerPhone = call.phone,
                        type = call.type,
                        salePhone = call.myPhoneNumber,
                        createAt = convertDateToString(call.time),
                        fileUri = call.absolutePath
                    )
                }
            }
        }
        SharedPreferencesManager.setLastSynchronizedTime(applicationContext, timeStampNow)
    }
}