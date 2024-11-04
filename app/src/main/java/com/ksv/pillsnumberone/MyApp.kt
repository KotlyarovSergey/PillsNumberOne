package com.ksv.pillsnumberone

import android.content.Context

object MyApp {
    private lateinit var _applicationContext: Context
    val applicationContext get() = _applicationContext

    fun init(applicationContext: Context) {
        _applicationContext = applicationContext
    }
}