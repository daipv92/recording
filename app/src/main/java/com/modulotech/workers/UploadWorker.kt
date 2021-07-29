package com.modulotech.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
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

    private fun uploadRecordingCall() {
        Logger.i("UploadWorker#uploadRecordingCall")
        val list = getCallInfo(applicationContext, nowByMiniSecond() - 5 * 24 * 60 * 60 * 1000)
        if (list.isNotEmpty()) {
            val lastSynchronizedTime = SharedPreferencesManager.getLastSynchronizedTime(applicationContext)
            Logger.i("UploadWorker#uploadRecordingCall: lastSynchronizedTime = $lastSynchronizedTime")
            SharedPreferencesManager.setLastSynchronizedTime(applicationContext, nowByMiniSecond())
        }
    }
}