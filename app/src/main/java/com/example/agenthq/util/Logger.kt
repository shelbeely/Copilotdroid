package com.example.agenthq.util

import android.util.Log
import com.example.agenthq.BuildConfig

object Logger {
    private const val TAG = "AgentHQ"

    fun d(message: String) {
        if (BuildConfig.DEBUG) Log.d(TAG, message)
    }

    fun e(message: String, throwable: Throwable? = null) {
        Log.e(TAG, message, throwable)
    }

    fun w(message: String) {
        Log.w(TAG, message)
    }
}
