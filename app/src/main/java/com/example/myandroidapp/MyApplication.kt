package com.example.myandroidapp

import android.app.Application
import android.content.Context
import android.util.Log
import com.example.myandroidapp.core.TAG

class MyApplication : Application() {
    lateinit var container: AppContainer

    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "init")
        container = AppContainer(this)
        appContext = applicationContext
    }
}
