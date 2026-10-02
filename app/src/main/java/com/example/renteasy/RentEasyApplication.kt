package com.example.renteasy

import android.app.Application
import com.example.renteasy.data.repository.DemoDataStore
import com.example.renteasy.utils.RentEasyLog

class RentEasyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RentEasyLog.i("RentEasyApplication", "Application started. Initializing seeded data...")
        DemoDataStore.initialize(this)
    }
}
