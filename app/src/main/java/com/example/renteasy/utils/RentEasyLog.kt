package com.example.renteasy.utils

import android.util.Log

object RentEasyLog {
    private const val TAG_PREFIX = "RentEasy_"

    fun d(tag: String, message: String) {
        Log.d("$TAG_PREFIX$tag", message)
    }

    fun i(tag: String, message: String) {
        Log.i("$TAG_PREFIX$tag", message)
    }

    fun w(tag: String, message: String, throwable: Throwable? = null) {
        Log.w("$TAG_PREFIX$tag", message, throwable)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        Log.e("$TAG_PREFIX$tag", message, throwable)
    }
}
