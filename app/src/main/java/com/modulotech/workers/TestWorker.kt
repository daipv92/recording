package com.modulotech.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.modulotech.utilities.Logger

class TestWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Logger.i("TestWorker")
        return Result.success()
    }
}