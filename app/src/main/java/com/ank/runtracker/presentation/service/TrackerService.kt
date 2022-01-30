package com.ank.runtracker.presentation.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.location.Location

import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.ank.runtracker.R
import com.ank.runtracker.common.Constants
import com.ank.runtracker.common.Constants.NOTIFICATION_ID
import com.ank.runtracker.common.TrackingUtils
import com.ank.runtracker.data.db.RunDao
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias pathline = MutableList<LatLng>
typealias polylines = MutableList<pathline>

@AndroidEntryPoint
class TrackerService : LifecycleService() {


    private var serviceKilled: Boolean = false

    @Inject
    lateinit var dao: RunDao

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder
    lateinit var currentNotification: NotificationCompat.Builder
    private var isFirstTime = true
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val timeRunInSec = MutableLiveData<Long>()

    companion object {
        val timeRunInMilli = MutableLiveData<Long>()
        val isTracking = MutableLiveData<Boolean>()
        val paths = MutableLiveData<polylines>()

    }

    private var isTimerEnabled = false
    private var lapTime = 0L// timer start time and stop to reset to 0
    private var totalRun = 0L // total time of run sum of all lap time
    private var timerStarted = 0L // timestamp of started timer
    private var lastSecondTimeStamp = 0L

    @SuppressLint("VisibleForTests")
    override fun onCreate() {
        super.onCreate()
        initialValues()
        currentNotification = notificationBuilder
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        isTracking.observe(this) {
            updateLocationTracking(it)
            updateNotificationActionState(it)
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        intent?.let {
            when (it.action) {
                Constants.START_RESUME_SERVICE -> {
                    if (isFirstTime) {
                        startForegroundService()
                        isFirstTime = false
                        Timber.d("START_SERVICE")
                    } else {
                        Timber.d("RESUME_SERVICE")
                        startTimer()
                    }

                }
                Constants.STOP_SERVICE -> {
                    Timber.d("STOP_SERVICE")
                    stopService()

                }
                Constants.PAUSE_SERVICE -> {
                    Timber.d("PAUSE_SERVICE")
                    pauseService()
                }


            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {

        startTimer()


        val notificationMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationMgr)
        }


        startForeground(NOTIFICATION_ID, notificationBuilder.build())
        timeRunInSec.observe(this) {
            if (!serviceKilled) {
                val notification = notificationBuilder
                    .setContentText(TrackingUtils.getFormattedTime(it * 1000L))
                notificationMgr.notify(NOTIFICATION_ID, notification.build())
            }
        }

    }

    // every time when sart our timer
    private fun startTimer() {

        addEmptyPolyLines()
        isTracking.postValue(true)
        timerStarted = System.currentTimeMillis()
        isTimerEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                // time difference now and time started
                lapTime = System.currentTimeMillis() - timerStarted

                timeRunInMilli.postValue(totalRun + lapTime)

                if (timeRunInMilli.value!! >= lastSecondTimeStamp + 1000L) {
                    timeRunInSec.postValue(timeRunInSec.value!! + 1)
                    lastSecondTimeStamp += 1000L

                }
                delay(50L)
            }
            totalRun += lapTime

        }


    }


    private fun updateNotificationActionState(isTracking: Boolean) {
        var serviceAction = Constants.START_RESUME_SERVICE
        val notificationActionText = if (isTracking) {
            serviceAction = Constants.PAUSE_SERVICE
            "Pause"
        } else {
            "Resume"
        }

        val pauseIntent = Intent(this, TrackerService::class.java).apply {
            action = serviceAction
        }
        val pendingIntent = PendingIntent.getService(this, 1, pauseIntent, FLAG_IMMUTABLE)




        currentNotification.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(currentNotification, ArrayList<NotificationCompat.Action>())
        }
        currentNotification = notificationBuilder
            .addAction(R.drawable.ic_pause_black_24dp, notificationActionText, pendingIntent)
        notificationManager.notify(NOTIFICATION_ID, currentNotification.build())
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTracking.value == true) {
                result.locations.forEach { location ->
                    addPathPoints(location)
                    Timber.d("NEW LOCATION: ${location.latitude}, ${location.longitude}")

                }

            }
        }


    }

    private fun pauseService() {

        isTracking.postValue(false)
        isTimerEnabled = false

    }

    private fun stopService() {


        serviceKilled = true
        isFirstTime = true
        pauseService()
        initialValues()
        stopForeground(true)
        stopSelf()


    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking && TrackingUtils.hasLocationPermissions(this)) {
            val request = LocationRequest.create().apply {
                interval = 5000L
                fastestInterval = 2000L


            }
            fusedLocationProviderClient.requestLocationUpdates(
                request,
                locationCallback, Looper.myLooper()!!
            )
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun addPathPoints(location: Location?) {
        location?.let {
            paths.value?.apply {
                last().add(LatLng(it.latitude, it.longitude))
                paths.postValue(this)
                Timber.d("location ${it.latitude} - ${it.longitude} ")

            }
        }

    }

    private fun initialValues() {
        isTracking.postValue(false)
        paths.postValue(mutableListOf())
        timeRunInSec.postValue(0L)
        timeRunInMilli.postValue(0L)
    }

    private fun addEmptyPolyLines() = paths.value?.apply {
        add(mutableListOf())
        paths.postValue(this)
    } ?: paths.postValue(mutableListOf(mutableListOf()))


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)


    }

    override fun onDestroy() {
        super.onDestroy()
        timeRunInSec.removeObservers(this)
        timeRunInMilli.removeObservers(this)
    }

}