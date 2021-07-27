package com.modulotech.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.modulotech.utilities.Logger
import com.modulotech.utilities.SharedPreferencesManager
import com.modulotech.utilities.getRecordingCallOnXiaoMi
import com.modulotech.utilities.nowByMiniSecond
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
        val list = getRecordingCallOnXiaoMi()
        if (list.isNotEmpty()) {
            val lastSynchronizedTime = SharedPreferencesManager.getLastSynchronizedTime(applicationContext)
            Logger.i("UploadWorker#uploadRecordingCall: lastSynchronizedTime = $lastSynchronizedTime")
            val newRecordFiles = list.filter { it.lastModified > lastSynchronizedTime }
            if (newRecordFiles.isNotEmpty()) {
                newRecordFiles.forEach { file ->
                    Logger.i("uploadRecordingCall: ${file.fileName}")
                }
            }
            SharedPreferencesManager.setLastSynchronizedTime(applicationContext, nowByMiniSecond())
        }
    }
}