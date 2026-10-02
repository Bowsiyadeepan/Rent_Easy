package com.example.renteasy

import android.app.Application
import com.example.renteasy.data.repository.DemoDataStore
import com.example.renteasy.utils.RentEasyLog
import com.google.firebase.FirebaseApp

class RentEasyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)
            RentEasyLog.i("RentEasyApplication", "Firebase initialized successfully.")
        } catch (e: Exception) {
            RentEasyLog.w("RentEasyApplication", "Firebase initialization fallback to demo mode: ${e.localizedMessage}")
        }
        RentEasyLog.i("RentEasyApplication", "Application started. Initializing seeded data...")
        DemoDataStore.initialize(this)
    }
}
