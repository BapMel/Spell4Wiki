package com.manimarank.spell4wiki.utils

import android.util.Log
import com.manimarank.spell4wiki.BuildConfig

/**
 * Utility class for print log data
 */
object Print {
    private const val TAG = "Spell4Wiki - App"
    fun log(message: String?) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message ?: "")
        }
    }

    fun error(message: String?) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, message ?: "")
        }
    }
}