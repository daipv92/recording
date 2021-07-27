package com.modulotech.utilities

import android.util.Log

object Logger {
    private const val TAG = "RecordingCall"

    fun i(str: String) {
        Log.i(TAG, str)
    }

    fun d(str: String) {
        Log.d(TAG, str)
    }

    fun e(str: String) {
        Log.e(TAG, str)
    }
}