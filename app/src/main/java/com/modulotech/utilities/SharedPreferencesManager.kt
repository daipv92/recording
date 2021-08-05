package com.modulotech.utilities

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.modulotech.models.CallInfo

object SharedPreferencesManager {
    private const val NAME = "record_share_ref"

    // properties
    private const val LAST_SYNCHRONIZED_TIME = "last_synchronized_time"
    private const val LAST_SYNCHRONIZED_LIST = "last_synchronized_list"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.applicationContext.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    }

    fun getLastSynchronizedTime(context: Context): Long {
        return getSharedPreferences(context).getLong(LAST_SYNCHRONIZED_TIME, 0)
    }

    fun setLastSynchronizedTime(context: Context, newValue: Long) {
        val editor = getSharedPreferences(context).edit()
        editor.putLong(LAST_SYNCHRONIZED_TIME, newValue)
        editor.commit()
    }

    fun getSynchronizedCallList(context: Context): List<CallInfo> {
        val serializedObject: String? = getSharedPreferences(context).getString(LAST_SYNCHRONIZED_LIST, null)
        return if (serializedObject != null) {
            val gson = Gson()
            gson.fromJson(serializedObject, object : TypeToken<List<CallInfo>>() {}.type)
        } else {
            emptyList()
        }
    }

    fun setSynchronizedCallList(context: Context, list: List<CallInfo>) {
        val gson = Gson()
        val json = gson.toJson(list)
        val editor = getSharedPreferences(context).edit()
        editor.putString(LAST_SYNCHRONIZED_LIST, json)
        editor.commit()
    }
}