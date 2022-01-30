package com.ank.runtracker.common


import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit


object TrackingUtils {

    fun getLocationPermission() =


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION


        )
        else arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,

            )

    fun hasLocationPermissions(context: Context): Boolean = getLocationPermission().all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    fun getFormattedTime(timeinmilli: Long, isMillisecond: Boolean = false): String {
        var timestamp = timeinmilli
        val time = StringBuilder()
        val h = TimeUnit.MILLISECONDS.toHours(timestamp)
        time.append("${if (h < 10) "0" else ""}$h:")

        timestamp -= TimeUnit.HOURS.toMillis(h)
        val min = TimeUnit.MILLISECONDS.toMinutes(timestamp)
        time.append("${if (min < 10) "0" else ""}$min:")
        timestamp -= TimeUnit.MINUTES.toMillis(min)
        val sec = TimeUnit.MILLISECONDS.toSeconds(timestamp)
        time.append("${if (sec < 10) "0" else ""}$sec:")
        if (isMillisecond) {
            timestamp -= TimeUnit.SECONDS.toMillis(sec)
            val milli = TimeUnit.MILLISECONDS.toMillis(timestamp)/10

            time.append("${if (milli < 10) "0" else ""}$milli")
        }
        return time.toString()
    }
}
