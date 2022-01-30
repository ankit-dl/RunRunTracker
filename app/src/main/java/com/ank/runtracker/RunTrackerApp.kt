package com.ank.runtracker

import android.app.Application
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.location.places.PlaceReport
import com.google.android.gms.maps.GoogleMap
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class RunTrackerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())


    }
}