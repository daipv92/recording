package com.modulotech.utilities

import android.content.Context
import android.content.SharedPreferences


object SharedPreferencesManager {
    private const val NAME = "record_share_ref"

    // properties
    private const val LAST_SYNCHRONIZED_TIME = "last_synchronized_time"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    }

    fun getLastSynchronizedTime(context: Context): Long {
        return getSharedPreferences(context).getLong(LAST_SYNCHRONIZED_TIME, 0)
    }

    fun setLastSynchronizedTime(context: Context, newValue: Long) {
        val editor = getSharedPreferences(context).edit()
        editor.putLong(LAST_SYNCHRONIZED_TIME, newValue)
        editor.commit()
    }
}